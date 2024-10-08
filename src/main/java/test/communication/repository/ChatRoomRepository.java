package test.communication.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import test.communication.domain.ChatRoomDTO;

import java.util.*;

@Repository
public class ChatRoomRepository {
	private Map<String, ChatRoomDTO> chatRoomDTOMap;

	@PostConstruct
	private void init() {
		chatRoomDTOMap = new LinkedHashMap<>();
	}

	public List<ChatRoomDTO> findAllRooms() {
		List<ChatRoomDTO> result = new ArrayList<>(chatRoomDTOMap.values());
		Collections.reverse(result);
		return result;
	}

	public ChatRoomDTO findRoomById(String id) {
		return chatRoomDTOMap.get(id);
	}

	public ChatRoomDTO createChatRoomDTO(String name) {
		ChatRoomDTO room = ChatRoomDTO.create(name);
		chatRoomDTOMap.put(room.getRoomId(), room);
		return room;
	}
}
