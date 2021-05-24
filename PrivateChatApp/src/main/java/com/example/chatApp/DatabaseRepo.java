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
    
    @Transactional
    public void insertUser(String username, String password) {
    	entityManager.createNativeQuery("insert into users (user_name, user_password) values (?,?)")
        .setParameter(1, username)
        .setParameter(2, password)
        .executeUpdate();
    }
    
    @Transactional
    public void insertFriend(Long user, Long friend) {
    	entityManager.createNativeQuery("insert into friends (user_id, user_friend) values (?,?)")
        .setParameter(1, user)
        .setParameter(2, friend)
        .executeUpdate();
    }
    
    @Transactional
    public void insertMessage(Long sender, Long recipient, String message) {
    	entityManager.createNativeQuery("insert into messages (sender_id, recipient_id, message_content) values (?,?,?)")
        .setParameter(1, sender)
        .setParameter(2, recipient)
        .setParameter(3, message)
        .executeUpdate();
    }
    
    List<User> getUsers() {
    	return entityManager.createQuery("select user from User user", User.class)
        		.getResultList();
    }
    
    User getUser(String username) {
    	return entityManager.createQuery("select user from User user where user_name = ?1", User.class)
    			.setParameter(1, username)
        		.getSingleResult();
    }
    
    User getUserById(Long id) {
    	return entityManager.createQuery("select user from User user where user_id = ?1", User.class)
    			.setParameter(1, id)
        		.getSingleResult();
    }
    
    List<Message> getMessages(Long recipientId) {
    	return entityManager.createQuery("select mes from Message mes where sender_id=?1 OR recipient_id = ?1", Message.class)
    			.setParameter(1, recipientId)
        		.getResultList();
    }
    
    List<Friend> getFriends(Long userId) {
    	return entityManager.createQuery("select fri from Friend fri where user_id = ?1", Friend.class)
    			.setParameter(1, userId)
        		.getResultList();
    }
}