package com.example.chatApp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//Hibernate entity mapping to the merchants postgres table

@Entity
@Table(name="friends")
public class Friend {
	@Id
    private Long friend_id;
    private Long user_id;
	private Long user_friend;
	private int friend_unread;
	
	public Friend() {}
	
	public Long getId() {
		return this.user_id;
	}
	
	public Long getFriend() {
		return this.user_friend;
	}
	
	public int getUnread() {
		return this.friend_unread;
	}
	
	public String toString() {
		return user_id + " " + user_friend + " " + friend_unread;
	}
}