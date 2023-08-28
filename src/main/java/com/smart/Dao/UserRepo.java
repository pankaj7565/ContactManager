package com.smart.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.User;



public interface UserRepo extends JpaRepository<User, Integer> {
	
//	dyanamic email lana hai 
	@Query("select u from User u where u.email = :email")	
	public User getUserByUserName(@Param("email")String email);
	
	
	
//	for email
	
	
	

}
