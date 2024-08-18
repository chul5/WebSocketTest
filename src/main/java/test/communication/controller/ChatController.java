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

	/*
	* Login을 할 수 있는 URL
	* */
	@GetMapping("/login")
	public String test() {
		return "test";
	}

	/**
	 * V1 에서 사용했던 URL지금은 사용하지 않는다.
	 * @param model
	 * @return
	 */
	@GetMapping("/chatTest")
	public String chatGET(Model model) {

		log.info("@ChatController, chat GET()");
		model.addAttribute("chat", chatService.getChat());

		return "chat";
	}


}
