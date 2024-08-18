package test.communication.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import test.communication.domain.Chat;
import test.communication.service.ChatService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ChatController {
	private final ChatService chatService;

	@GetMapping("/test")
	public String test() {
		return "test";
	}

	@GetMapping("/chatTest")
	public String chatGET(Model model) {

		log.info("@ChatController, chat GET()");
		model.addAttribute("chat", chatService.getChat());

		return "chat";
	}


}
