package test.communication.repository;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import test.communication.domain.Chat;

import java.util.List;

@Repository
public class ChatRepository {
	@Autowired
	private EntityManager em;

	public void saveChat(Chat chat) {
		em.persist(chat);
	}

	public List<Chat> getChat(){
		return em.createQuery("select c from Chat c", Chat.class).getResultList();
	}
}
