package me.exrates.controller.ngContorollers;

import me.exrates.model.ChatMessage;
import me.exrates.model.enums.ChatLang;
import me.exrates.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NgChatController {

	private final SimpMessagingTemplate template;

	private final ChatService chatService;

	@Autowired
	public NgChatController(SimpMessagingTemplate template, ChatService chatService) {
		this.template = template;
		this.chatService = chatService;
	}

	@MessageMapping("/topic/chat/{lang}")
	public void onReceivedNewMessage(@DestinationVariable String lang, String message){
		this.template.convertAndSend("/topic/chat/" + lang, message);
	}

	@DeleteMapping(value = "/2a8fy7b07dxe44/chat/deleteMessage/")
	public ResponseEntity<ChatMessage> onDeleteChatItem(@RequestParam String lang,
														@RequestParam Long id,
														@RequestParam Integer userId,
														@RequestParam String nickname,
														@RequestParam String body){
		if (lang == null || lang.isEmpty()){
			new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		ChatMessage message = new ChatMessage();
		message.setId(id);
		message.setUserId(userId);
		message.setBody(body);
		message.setNickname(nickname);

		ChatLang chatLang = ChatLang.toInstance(lang);
		chatService.deleteMessage(message, chatLang);
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
}
