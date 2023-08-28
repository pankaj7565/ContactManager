package com.smart.Dao;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

import java.util.List;

//	this is new comment
public interface ContactRepo extends JpaRepository<Contact, Integer> {
//	pegination
	
	@Query("from Contact as c where c.user.id =:userId")
//	public List<Contact> findContactByUser(@Param("userId")int userId);
	
	
	public Page<Contact> findContactByUser(@Param("userId")
	int userId,org.springframework.data.domain.Pageable pageable );
//	pagable has 2 info -> current page , contact per page
	
	
	
//	for the search result

	
	public List<Contact> findByNameContainingAndUser(String name,User user);
	
		
		
		
		
		
		
	
	
	
	
	
	
	

}
