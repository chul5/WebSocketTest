package test.communication;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
public class ChatController {

	@GetMapping("/test")
	public String test(){
		return "test";
	}

	@GetMapping("/chat")
	public String chatGET() {

		log.info("@ChatController, chat GET()");

		return "chat";
	}
}
