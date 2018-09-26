package me.exrates.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AngularWsController {

	private final SimpMessagingTemplate template;

	@Autowired
	public AngularWsController(SimpMessagingTemplate template) {
		this.template = template;
	}

	@MessageMapping("/send/message")
	public void onReciveChatMessage(String msd){
		this.template.convertAndSend("/chat",msd );
	}
}
