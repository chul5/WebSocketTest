package test.communication;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
	private String type;
	private String sender;
	private String receiver;
	private Object data;

	public void newConnect(){
		this.type = "new";
	}

	public void closeConnect(){
		this.type = "close";
	}

}
