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
    public void insertUser(String username, String password, String name, String email) {
    	entityManager.createNativeQuery("insert into users (user_name, user_password, user_pname, user_email) values (?,?,?,?)")
        .setParameter(1, username)
        .setParameter(2, password)
        .setParameter(3, name)
        .setParameter(4, email)
        .executeUpdate();
    }
    
    @Transactional
    public void editUser(String Username, String name, String email, String password) {
    	entityManager.createNativeQuery("update users set user_pname = ?, user_email = ?, user_password = ? where user_name = ?")
        .setParameter(1, name)
        .setParameter(2, email)
        .setParameter(3, password)
        .setParameter(4, Username)        
        .executeUpdate();
    }
    
    @Transactional
    public void insertFriend(Long user, Long friend) {
    	entityManager.createNativeQuery("insert into friends (user_id, user_friend, friend_unread) values (?,? ,0)")
        .setParameter(1, user)
        .setParameter(2, friend)
        .executeUpdate();
    }
    
    @Transactional
    public void insertMessage(Long sender, Long recipient, String message, java.sql.Timestamp time) {
    	entityManager.createNativeQuery("insert into messages (sender_id, recipient_id, message_content, message_time) values (?,?,?,?)")
        .setParameter(1, sender)
        .setParameter(2, recipient)
        .setParameter(3, message)
        .setParameter(4, time)
        .executeUpdate();
    }
    
    @Transactional
    public void setRead(Long id) {
    	entityManager.createNativeQuery("update messages set message_read = true where message_id = ?1")
        .setParameter(1, id)
        .executeUpdate();
    }
    
    @Transactional
    public void removeFriend(Long id, Long friendId) {
    	entityManager.createNativeQuery("delete from friends where user_id = ?1 AND user_friend = ?2")
        .setParameter(1, id)
        .setParameter(2, friendId)
        .executeUpdate();
    }
    
    @Transactional
    public void increaseUnread(int amount, Long id, Long friendId) {
    	entityManager.createNativeQuery("update friends set friend_unread = friend_unread+?1 where user_id = ?2 and user_friend = ?3")
        .setParameter(1, amount)
        .setParameter(2, id)
        .setParameter(3, friendId)
        .executeUpdate();
    }
    
    @Transactional
    public void removeUnread(Long id, Long friendId) {
    	entityManager.createNativeQuery("update friends set friend_unread = 0 where user_id = ?1 and user_friend = ?2")
        .setParameter(1, id)
        .setParameter(2, friendId)
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
    
    List<Message> getMessages(Long userId, Long friendId) {
    	return entityManager.createQuery("select mes from Message mes where (sender_id=?1 AND recipient_id=?2) OR (recipient_id=?1 AND sender_id=?2) order by mes.id desc", Message.class)
    			.setParameter(1, userId)
    			.setParameter(2, friendId)
    			.setMaxResults(100)
        		.getResultList();
    }
    
    Message getLatestMessage(Long userId, Long friendId) {
    	return entityManager.createQuery("select mes from Message mes where (sender_id=?1 AND recipient_id=?2) OR (recipient_id=?1 AND sender_id=?2) order by mes.id desc", Message.class)
    			.setParameter(1, userId)
    			.setParameter(2, friendId)
    			.setMaxResults(1)
    			.getSingleResult();
    }
    
    List<Message> getUnreadMessage(Long userId, Long friendId) {
    	return entityManager.createQuery("select mes from Message mes where ((sender_id=?1 AND recipient_id=?2) OR (recipient_id=?1 AND sender_id=?2)) and mes.message_read is null order by mes.id desc", Message.class)
    			.setParameter(1, userId)
    			.setParameter(2, friendId)
    			.getResultList();
    }
    
    List<Friend> getFriends(Long userId) {
    	return entityManager.createQuery("select fri from Friend fri where user_id = ?1 order by fri.id", Friend.class)
    			.setParameter(1, userId)
        		.getResultList();
    }
    
    List<Friend> getFriendRequests(Long userId) {
    	return entityManager.createQuery("select fri from Friend fri where user_friend = ?1 AND (select count(fri2) from Friend fri2 where user_id = ?1 AND user_friend = fri.user_id) = 0", Friend.class)
    			.setParameter(1, userId)
        		.getResultList();
    }
    
    Friend getFriend(Long user, Long friend) {
    	return entityManager.createQuery("select fri from Friend fri where user_id = ?1 AND user_friend = ?2 order by friend_id", Friend.class)
    			.setParameter(1, user)
    			.setParameter(2, friend)
        		.getSingleResult();
    }
    
    List<Friend> getUnread(Long user) {
    	return entityManager.createQuery("select fri from Friend fri where user_id = ?1 AND friend_unread <> 0", Friend.class)
    			.setParameter(1, user)
        		.getResultList();
    }
}