package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.Dao.ContactRepo;
import com.smart.Dao.UserRepo;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class SearchController {
	
	
	
	@Autowired
	private UserRepo repo;
	
	@Autowired
	private ContactRepo contactRepo;
	
    
	
	
//	search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?>search(@PathVariable("query")
	String query,Principal principal){
		
		
		System.out.println(query);
		
		User user = this.repo.getUserByUserName(principal.getName());
		
		List<Contact> contact = 
				this.contactRepo.findByNameContainingAndUser(query, user);
		
		
		
		return ResponseEntity.ok(contact);
		
		
		
		
	}

	
	
	
}
