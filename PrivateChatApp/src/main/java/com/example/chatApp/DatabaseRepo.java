package com.example.chatApp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

//class that contains methods for performing SQL queries

@Repository
public class DatabaseRepo {
	@Autowired
    @PersistenceContext
    public EntityManager entityManager;
    
	//insert merchant into POSTGRES
    @Transactional
    public void insertUser(String username, String password) {
    	entityManager.createNativeQuery("insert into users (user_name, user_password) values (?,?)")
        .setParameter(1, username)
        .setParameter(2, password)
        .executeUpdate();
    }
    
    //read merchants from hibernate entities
    List<User> getUsers() {
    	return entityManager.createQuery("select user from User user", User.class)
        		.getResultList();
    }
}