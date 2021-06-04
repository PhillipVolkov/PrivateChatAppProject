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
	private Boolean message_read;
	private java.sql.Timestamp message_time;
	
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
	
	public Boolean getRead() {
		return this.message_read;
	}
	
	public void setContents(String contents) {
		this.message_content = contents;
	}
	
	public java.sql.Timestamp getTimeStamp() {
		return this.message_time;
	}
	
	public String getTimeString() {
		String time = "";
		time += this.message_time.getHours()%12;
		
		time += ":";
		
		if (this.message_time.getMinutes() < 10) time += "0";
		time += this.message_time.getMinutes();
		
		if (this.message_time.getHours() > 12) time += " PM";
		else time += " AM";
		
		return time;
	}
	
	public String getDateString() {
		final String days[] = new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
		
		String date = "";
		date += days[this.message_time.getDay()];
		date += " " + this.message_time.getMonth();
		date += "/" + this.message_time.getDay();
		date += "/" + (this.message_time.getYear() + 1900);
				
		return date;
	}
	
	public String toString() {
		return message_id + " " + " " + sender_id + " " + recipient_id + " " + message_read + "\t" + message_content;
	}
}