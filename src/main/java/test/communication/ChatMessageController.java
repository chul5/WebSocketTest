package test.communication;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import test.communication.domain.Chat;
import test.communication.service.ChatService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {
	private final ChatService chatService;
	@GetMapping("/messages")
	public List<Chat> getChat() {
		return chatService.getChat();
	}
}
