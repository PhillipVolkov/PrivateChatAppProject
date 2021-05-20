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
    public void insertMerchant(Merchant merchant) {
    	entityManager.createNativeQuery("insert into merchants (merchant_name, merchant_pattern, category_id) values (?,?,?)")
        .setParameter(1, merchant.getName())
        .setParameter(2, merchant.getPattern())
        .setParameter(3, merchant.getCategoryId())
        .executeUpdate();
    }
    
    //read merchants from hibernate entities
    List<Merchant> getMerchants() {
    	return entityManager.createQuery("select mer from Merchant mer", Merchant.class)
        		.getResultList();
    }
}