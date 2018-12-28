package me.exrates.service.impl;

import javafx.util.Pair;
import me.exrates.dao.ChatDao;
import me.exrates.model.ChatMessage;
import me.exrates.model.User;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.enums.UserRole;
import me.exrates.service.ChatService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IllegalChatMessageException;
import me.exrates.service.util.ChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final int MESSAGE_BARRIER = 50;
    private final int CACHE_BARRIER = 150;
    private final int MAX_MESSAGE = 256;
    private final boolean INCLUSIVE = true;

    private final ChatDao chatDao;
    private final UserService userService;
    private final EnumMap<ChatLang,ChatComponent> chats;

    private AtomicLong GENERATOR;
    private long flushCursor;

    private final Predicate<String> deprecatedChars = Pattern.compile("^[^<>{}&*\"/;`]*$").asPredicate();

    private final Logger LOG = LogManager.getLogger(ChatServiceImpl.class);

    private final UserRole[] authoritiesForLinks = {UserRole.ADMIN_USER, UserRole.ADMINISTRATOR};

    @Autowired
    public ChatServiceImpl(final ChatDao chatDao,
                           final UserService userService,
                           final EnumMap<ChatLang, ChatComponent> chats)
    {
        this.chatDao = chatDao;
        this.userService = userService;
        this.chats = chats;
    }

    @PostConstruct
    public void cacheWarm() {
        final List<Long> ids = new ArrayList<>();
        Stream.of(ChatLang.values())
                .map(lang -> new Pair<>(lang,new TreeSet<>(chatDao.findLastMessages(lang, MESSAGE_BARRIER))))
                .forEach(pair -> {
                    final ChatComponent comp = chats.get(pair.getKey());
                    final NavigableSet<ChatMessage> cache = pair.getValue();
                    final ChatMessage tail;
                    if (cache.isEmpty()) {
                        tail = null;
                        ids.add(0L);
                    } else {
                        tail = cache.last();
                        ids.add(cache.first().getId());
                    }
                    comp.setCache(cache);
                    comp.setTail(tail);
                });
        GENERATOR = new AtomicLong(ids.stream().reduce(Long::max).orElse(0L));
        flushCursor = GENERATOR.get();
    }

    @Override
    public ChatMessage persistMessage(final String body, final String email, ChatLang lang) throws IllegalChatMessageException {
    if (body.isEmpty() || body.length() > MAX_MESSAGE || (!deprecatedChars.test(body) ? !hasAuthorityForLinks() : false
         )) {
            throw new IllegalChatMessageException("Message contains invalid symbols : " + body);
        }
        final User user = userService.findByEmail(email);
        final ChatMessage message = new ChatMessage();
        message.setBody(body);
        message.setUserId(user.getId());
        message.setNickname(user.getNickname());
        message.setId(GENERATOR.incrementAndGet());
        message.setTime(LocalDateTime.now());
        final ChatComponent comp = chats.get(lang);
        try {
            comp.getLock().writeLock().lock();
            comp.getCache().add(message);
            if (isNull(comp.getTail())) {
                comp.setTail(message);
            }
            if (comp.getCache().size() > MESSAGE_BARRIER) {
                comp.setTail(comp.getCache().lower(comp.getTail()));
            }
            if (comp.getCache().size() == CACHE_BARRIER) {
                comp.setCache(new TreeSet<>(comp.getCache().headSet(comp.getTail(), INCLUSIVE)));
            }
        } finally {
            comp.getLock().writeLock().unlock();
        }
        return message;
    }

    private boolean hasAuthorityForLinks(){
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (UserRole role : authoritiesForLinks) {
            if(authorities.contains(new SimpleGrantedAuthority(role.toString()))) return true;
        }
        return false;
    }

    public NavigableSet<ChatMessage> getLastMessages(final ChatLang lang) {
        final ChatComponent comp = chats.get(lang);
        if (comp.getCache().size() == 0) {
            return new TreeSet<>();
        }
        final NavigableSet<ChatMessage> result;
        try {
            comp.getLock().readLock().lock();
            result = new TreeSet<>(comp.getCache().headSet(comp.getTail(), INCLUSIVE));
        } finally {
            comp.getLock().readLock().unlock();
        }
        return result;
    }

    @Scheduled(fixedDelay = 1000L, initialDelay = 1000L)
    public void flushCache() {
        for (ChatLang lang : ChatLang.values()) {
            final ChatMessage cacheCeil = new ChatMessage();
            cacheCeil.setId(flushCursor);
            final ChatMessage higher = getLastMessages(lang).lower(cacheCeil);
            if (higher != null) {
                final NavigableSet<ChatMessage> newMessages = getLastMessages(lang).headSet(higher, INCLUSIVE);
                chatDao.persist(lang, newMessages);
                final long newFlushCursor = newMessages.first().getId();
                if (flushCursor < newFlushCursor) {
                    flushCursor = newFlushCursor;
                }
            }
        }
    }

    @Override
    public List<ChatHistoryDto> getChatHistory(ChatLang chatLang) {
        return chatDao.getChatHistory(chatLang);
    }

    @Override
    @Transactional
    public void deleteMessage(final ChatMessage message, final ChatLang lang) {
        final ChatComponent comp = chats.get(lang);
        try {
            comp.getLock().writeLock().lock();
            comp.getCache().remove(message);
        } finally {
            comp.getLock().writeLock().unlock();
        }

        chatDao.delete(lang, message);

    }
}
