package me.exrates.security.ipsecurity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.LoginAttemptDto;
import me.exrates.model.enums.IpBanStatus;
import me.exrates.security.exception.BannedIpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "ip_log")
@Service
@PropertySource(value = {"classpath:/ip_ban.properties"})
public class IpBlockingServiceImpl implements IpBlockingService {

    @Value("${ban.short.attempts.num}")
    private Integer attemptsBeforeShortBan;

    @Value("${ban.long.attempts.num}")
    private Integer attemptsBeforeLongBan;

    @Value("${ban.short.time}")
    private Integer shortBanTime;

    @Value("${ban.long.time}")
    private Integer longBanTime;

    private ConcurrentMap<IpTypesOfChecking, LoadingCache<String, LoginAttemptDto>> ipAddressCache;

    public IpBlockingServiceImpl() {
        ipAddressCache = new ConcurrentReferenceHashMap<>();
        Arrays.stream(IpTypesOfChecking.values())
                .forEach(value -> ipAddressCache.put(value, buildCache()));
    }

    @Override
    public void checkIp(String ipAddress, IpTypesOfChecking ipTypesOfChecking) {
        LoadingCache<String, LoginAttemptDto> ipAddressCache = this.ipAddressCache.get(ipTypesOfChecking);
        LoginAttemptDto attempt;
        try {
            attempt = ipAddressCache.get(ipAddress);
        } catch (ExecutionException e) {
            log.error("Failed to get cache for " + ipTypesOfChecking, e);
            return;
        }
        if (attempt != null) {
            synchronized (attempt) {
                if (attempt.getAttempts().size() > attemptsBeforeLongBan) {
                    processError(ipAddress, longBanTime, attempt);
                } else if (attempt.getAttempts().size() > attemptsBeforeShortBan
                        && attempt.getExpiresAt() != null
                        && attempt.getExpiresAt().isAfter(LocalDateTime.now())) {
                    processError(ipAddress, shortBanTime, attempt);
                }
            }
        }
    }

    @Override
    public void failureProcessing(String ipAddress, IpTypesOfChecking ipTypesOfChecking) {
        LoadingCache<String, LoginAttemptDto> ipAddressCache = this.ipAddressCache.get(ipTypesOfChecking);
        LoginAttemptDto attempt;
        try {
            attempt = ipAddressCache.get(ipAddress);
        } catch (ExecutionException e) {
            log.error("Failed to get cache for " + ipTypesOfChecking, e);
            return;
        }
        log.warn("User from IP: {}, failed to submit valid data {} time(s)", ipAddress, attempt == null ? 1 : attempt.getAttempts().size());
        if (attempt != null) {
            synchronized (attempt) {
                attempt.addNewAttempt();
                if (attempt.getAttempts().size() == shortBanTime) {
                    attempt.setStatus(IpBanStatus.BAN_SHORT);
                    attempt.setExpiresAt(LocalDateTime.now().plusSeconds(shortBanTime));
                } else if (attempt.getAttempts().size() > longBanTime) {
                    attempt.setStatus(IpBanStatus.BAN_LONG);
                    attempt.setExpiresAt(LocalDateTime.now().plusSeconds(longBanTime));
                }
            }
        }
    }

    @Override
    public void successfulProcessing(String ip, IpTypesOfChecking ipTypesOfChecking) {
        LoadingCache<String, LoginAttemptDto> ipAddressCache = this.ipAddressCache.get(ipTypesOfChecking);
        ipAddressCache.invalidate(ip);
    }

    private LoadingCache<String, LoginAttemptDto> buildCache() {
        return CacheBuilder
                .newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, LoginAttemptDto>() {
                    @Override
                    public LoginAttemptDto load(String ipAddress) {
                        return new LoginAttemptDto(ipAddress);
                    }
                });
    }

    private void processError(String ipAddress, int time, LoginAttemptDto attemptDto) {
        String message = String.format("IP %s is banned for %d minutes: number of incorrect attempts exceeded: [%d]!",
                ipAddress, time / 60, attemptDto.getAttempts().size());
        log.warn(message);
        throw new BannedIpException(message, shortBanTime);
    }
}