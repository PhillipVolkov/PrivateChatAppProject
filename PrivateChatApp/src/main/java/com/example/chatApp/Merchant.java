package com.example.chatApp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//Hibernate entity mapping to the merchants postgres table

@Entity
@Table(name="merchants")
public class Merchant {
	@Id
    private Long merchant_id;
	
	private String merchant_name;
	private String merchant_pattern;
    private Long category_id;
    
	public Long getId() {
		return this.merchant_id;
	}
	
	public String getName() {
		return this.merchant_name;
	}
	
	public String getPattern() {
		return this.merchant_pattern;
	}
	
	public Long getCategoryId() {
		return this.category_id;
	}
	
	public void setName(String newName) {
		this.merchant_name = newName;
	}
	
	public void setPattern(String newPattern) {
		this.merchant_pattern = newPattern;
	}
	
	public void setCategoryId(Long newId) {
		this.category_id = newId;
	}
}