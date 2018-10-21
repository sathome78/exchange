package me.exrates.ngcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NgWsController {

	private final SimpMessagingTemplate template;

	@Autowired
	public NgWsController(SimpMessagingTemplate template) {
		this.template = template;
	}

	@MessageMapping("/send/message")
	public void onReceiveChatMessage(String msd){
		this.template.convertAndSend("/chat", msd);
	}
}
