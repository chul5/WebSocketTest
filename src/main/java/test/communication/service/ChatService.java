package test.communication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.communication.domain.Chat;
import test.communication.repository.ChatRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final ChatRepository chatRepository;

	@Transactional
	public void saveChat(Chat chat) {
		chatRepository.saveChat(chat);
	}

	@Transactional
	public List<Chat> getChat(){
		return chatRepository.getChat();
	}
}
