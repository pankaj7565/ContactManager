package com.smart.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
//componet apne ap iska object bana lega
public class sessionHelper {

	
	public void removeMessage() {
		
		
		try {
			System.out.println("removing message from session");
//			iski help se hum get seeion attribute nikal sakte hai
 HttpSession session = ((ServletRequestAttributes)RequestContextHolder.
					getRequestAttributes()).getRequest().getSession();
 
 session.removeAttribute("message");
			
			
			
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
