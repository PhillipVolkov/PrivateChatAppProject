package com.example.chatApp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//Hibernate entity mapping to the merchants postgres table

@Entity
@Table(name="messages")
public class Message {
	@Id
    private Long message_id;
	
    private Long sender_id;
    private Long recipient_id;
	private String message_content;
	
	public Message() {}
	
	public Long getId() {
		return this.message_id;
	}
	
	public Long getSender() {
		return this.sender_id;
	}
	
	public Long getRecipient() {
		return this.recipient_id;
	}
	
	public String getContents() {
		return this.message_content;
	}
}