package com.example.chatApp;

public class Typing {
	private Long user_id;
	private Long recipient_id;
	private boolean isTyping;
	
	public Typing (Long user, Long recipient, boolean typing) {
		this.user_id = user;
		this.recipient_id = recipient;
		this.isTyping = typing;
	}
	
	public Long getUser() {
		return this.user_id;
	}
	
	public Long getRecipient() {
		return this.recipient_id;
	}
	
	public boolean isTyping() {
		return this.isTyping;
	}
}