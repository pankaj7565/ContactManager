package com.smart.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.Dao.UserRepo;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@org.springframework.stereotype.Controller
public class Controller {
	
	
	@Autowired
	private BCryptPasswordEncoder encoder;
//	
//	
	@Autowired
//	autowire lagane se iska obejct mil jaye ag
	private UserRepo repo;
	
//	home controller
	
	@GetMapping("/")
	public String home(Model m) {
		
		m.addAttribute("title","About Contact Manager");
		
		
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model m) {
		
		m.addAttribute("title","About Contact Manager");
		
		
		return "about";
	}
	
	
	
	@GetMapping("/signup")
	public String signup(Model m) {
		
		
		m.addAttribute("title","Register - Smart Contact Manager");
		m.addAttribute("user",new User());
		
		return "signup";
	}
//	this handler for register user
	
//	@PostMapping
	@Valid
    @PostMapping("/do_register")
	public String registeruser(@Validated @ModelAttribute("user")
	User user,BindingResult bindingResult ,@RequestParam(value="aggreement",defaultValue = "false")
	boolean aggreement,Model model,HttpSession session) {
		
		try {
//			if aggrement not done
			if(!aggreement) {
				System.out.println("You have not aggred the term and conditon");
				throw new Exception("you have not aggred the term and condition");
			}
			
			
			if(bindingResult.hasErrors()) {
				System.out.println("ERROR"+bindingResult.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			
//			this is just for entering user data manually
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			
			user.setPassword(encoder.encode(user.getPassword()));
			
			
			 
			
			System.out.println("aggreement"+ aggreement);
			System.out.println("User"+user);
//			database saving message
			User result = this.repo.save(user);
			System.out.println(result);
//			show on the page
//			if every thing went right and here the blank user will go
//			new user dalae ga field blank aaye ga page pe
			model.addAttribute("user",new User());
//			showing message
			session.setAttribute("message",new Message("Succesfull Register","alert-success"));
			return "signup";
			
			
		} catch (Exception e) {
			// TODO: handle exception
//			try block se exception aha rha hai
			e.printStackTrace();
//			user ko add kre ga aur siggup page dhika dega aur session mai message dale ga aur mgs dhika dega 
			model.addAttribute("user",user);
			session.setAttribute("message",new Message("something went error "+e.getMessage(),"alert-danger"));
		
			return "signup";
		}
		
		
		
	}
	
	
	
//	handler for custom login 
	
	@GetMapping("/signin")
	public String customLogin(Model model) {
		
		model.addAttribute("title","Login page");
	
		
		  
		
		return "login";
		
		
	}
	
	


}
