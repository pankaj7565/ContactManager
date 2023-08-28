package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smart.Dao.UserRepo;
import com.smart.entities.User;

@Service
public class UserDetaisServiceImp implements UserDetailsService {

	@Autowired
	private UserRepo repo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		
//		fetching user from database
		User userByUserName = repo.getUserByUserName(username);
		
		if(userByUserName==null) {
			throw new UsernameNotFoundException("could not found the user");
		}
		
		CustomUserDetails customUserDetails= new CustomUserDetails(userByUserName);
		
		return customUserDetails;
	}
	
	

}
