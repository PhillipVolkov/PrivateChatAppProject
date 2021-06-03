package com.example.chatApp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//Hibernate entity mapping to the merchants postgres table

@Entity
@Table(name="users")
public class User {
	@Id
    private Long user_id;
	private String user_name;
	private String user_password;
        private String user_email;
	private String user_pname;
        
	public User() {}
	
	public Long getId() {
		return this.user_id;
	}
	
	public String getUsername() {
		return this.user_name;
	}
	
	public String getPassword() {
		return this.user_password;
	}
        
        public String getEmail() {
		return this.user_email;
	}
	
	public String getPname() {
		return this.user_pname;
	}
	
	public void setUsername(String newName) {
		this.user_name = newName;
	}
	
	public void setPassword(String newPass) {
		this.user_password = newPass;
	}
}