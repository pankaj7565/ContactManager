package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.Dao.UserRepo;
import com.smart.entities.User;
import com.smart.service.Service;

import jakarta.servlet.http.HttpSession;


@Controller
public class ForgotController {

	Random random = new Random(10000);
	@Autowired
	private Service EmailService;

	@Autowired
	private UserRepo repo;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

//	email id from open handler

	@RequestMapping("/forgot")
	public String openEmailFrom() {

		return "forgot_form";
	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession httpSession) {

		System.out.println("email" + email);
//		/generating otp of 4 digit

		int otp = random.nextInt(99999);

		System.out.println("otp " + otp);

		String Subject = "otp from SCM";
		String message = "" + "<div style='border:1px solid #e2e2e2; padding:20px'>" + "<h1>" + "OTP = " + "<b>" + otp
				+ "</n>" + "</h1>" + "" + "</div>";

		String to = email;
//		

//		write code for send otp to mail
		boolean flag = this.EmailService.sendEmail(to, Subject, message);

		if (flag) {

			httpSession.setAttribute("MyOtp", otp);
			httpSession.setAttribute("email", email);
			return "verify_otp";
		} else {

			httpSession.setAttribute("message", "check your email id");

			return "forgot_form";
		}

	}

//	verify otp 

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("OTP") int OTP, HttpSession session) {


		int MyOtp = (int) session.getAttribute("MyOtp");
		
		String email = (String) session.getAttribute("email");

		
		
		
		if (MyOtp == OTP) {
//			password change form 
			
			User user = this.repo.getUserByUserName(email);
			System.out.println("User = "+user);

			if (user==null) {
//				send error message
				session.setAttribute("message", "user doest not exit with this email");
				return "forgot_form";
			} else {
				
//				send change password form

			}
			return "password_change_form";
		} else {

			session.setAttribute("message", "you have entered wrong otp !!");

			return "verify_otp";

		}

	}
	
//	change-password
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword")
	String password,HttpSession session) {
		
		
		
		String email = (String) session.getAttribute("email");
		
		User user = this.repo.getUserByUserName(email);
		
		user.setPassword(this.bCryptPasswordEncoder.encode(password));
		this.repo.save(user);
		
	
	
		return "redirect:/signin?change=password changed successfully";
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
