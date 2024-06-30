package test.communication.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Chat extends Auditable{
	@Id
	@GeneratedValue
	private Long id;

	private String name;
	private String content;

	public Chat(){

	}

	public static Chat createChat(String payload) {
		String[] lines = payload.split(":");
		return Chat.builder()
				.name(lines[0])
				.content(lines[1])
				.build();
	}
}
