package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import com.razorpay.*;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Dao.ContactRepo;
import com.smart.Dao.MyOrderRepo;
import com.smart.Dao.UserRepo;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Component
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepo repo;
	@Autowired
	private ContactRepo contactRepo;

	@Autowired
	private MyOrderRepo myOrderRepo;

//	medthod for adding the common data to response
	@ModelAttribute
	public void commonData(Model model, Principal principal) {

		String Username = principal.getName();
		System.out.println(Username);

		User user = repo.getUserByUserName(Username);
		System.out.println("User" + user);

		model.addAttribute("user", user);

	}

//	dashboard home
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "User Dashboard");
//		String Username = principal.getName();
//		System.out.println(Username);
//	
//		
//		User user =repo.getUserByUserName(Username);
//		System.out.println("User"+user);
//		
//		model.addAttribute("user",user);
//		/get the user using the username

		return "normal/user_dashboard";

	}
//	add form handler after login

	@GetMapping("/add-contact")
	public String addContact(Model model) {

		model.addAttribute("title", "add contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact";
	}

//	processing add comtact form
//	data send kre he ha wo post mehtod mai aha rha hai 
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact

			, @RequestParam("proImage") MultipartFile file
			// user naem ko fetch kr rhe ha and user name is email
			, Principal principal, HttpSession session) {

		try {

//      		adding contact in the database video 54 from image adding
			String name = principal.getName();
			User user = this.repo.getUserByUserName(name);

//  		image upload code processing
			if (file.isEmpty()) {
				System.out.println("File is empty");
				contact.setImage("default.png");
			} else {
//      			file upload adn update the contact name
//      			contact.setImage(name)
				contact.setImage(file.getOriginalFilename());

//      			path nika rhe hai
				File saveFile = new ClassPathResource("static/image").getFile();
//      			/path nika rha ha
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("image is uploded");
			}

//      		for bidirectional mapping user ko dena the conatct and contact ko dena the user

			user.getContacts().add(contact);
			contact.setUser(user);

			this.repo.save(user);
			System.out.println("Data" + contact);
			System.out.println("add to data base");
// here is the success message
//      		do it later
			session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
					.getSession();
			session.setAttribute("message", new Message("your contact is added !! Add more", "success"));

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("ERROR " + e.getMessage());
			e.printStackTrace();
//			error message bootstrap
//			HttpSession httpSession = ((ServletRequestAttributes)RequestContextHolder.
// 					getRequestAttributes()).getRequest().getSession();
			session.setAttribute("message", new Message("some thing went wrong!! Try again", "danger"));

		}

		return "normal/add_contact";
	}

//	view contact handler

//	data bhejne ke liyea model lete hai
//	per page  =5
//	currennt page =0

	@GetMapping("/show-contact/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "show contact");

//		contact ki list ko bhejna hai 
//		String name = principal.getName();
//		User user = this.repo.getUserByUserName(name);
//		List<Contact> contacts = user.getContacts();

		String name = principal.getName();
		User user = this.repo.getUserByUserName(name);

		Pageable pageable = PageRequest.of(page, 3);

//	List<Contact> findContactByUser = this.contactRepo.findContactByUser(user.getId(),pageable);

		Page<Contact> findContactByUser = this.contactRepo.findContactByUser(user.getId(), pageable);

		m.addAttribute("contact", findContactByUser);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", findContactByUser.getTotalPages());

		return "normal/show-contact";

	}

//	tho show thr particular contact details

	@GetMapping("/{cid}/contact")
	public String contactdetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {

		System.out.println("CID" + cid);

		Optional<Contact> findById = this.contactRepo.findById(cid);
		Contact contact = findById.get();

//		  login user and see its data
		String uname = principal.getName();
//		 login user we got here

		User user = this.repo.getUserByUserName(uname);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		return "normal/contact_details";
	}

//	delete contact handler

	@GetMapping("/delete/{cid}")
	@Transactional
	public String delete(@PathVariable("cid") Integer cid, Model model, HttpSession httpSession, Principal principal) {

		Optional<Contact> Cp = this.contactRepo.findById(cid);
		Contact contact = Cp.get();

//		unlink with user with contact to delete its contact with id
//		contact.setUser(null);

		User user = this.repo.getUserByUserName(principal.getName());

		user.getContacts().remove(contact);

		this.repo.save(user);

//		assingent remove photo
//		remove photo
//		img
//		contzct.getImage delete this

//		CHECK IF OTHRER USER DONT DELTEE HI IS DELETEING IS OWN ID
//		this.contactRepo.delete(contact);

		httpSession.setAttribute("message", new Message("contact delete successfully", "success"));

		return "redirect:/user/show-contact/0";

	}

//	@requestmapping can be used

//	 open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateFrom(@PathVariable("cid") Integer cid, Model model) {

		model.addAttribute("title", "Update contact");
		Contact contact = this.contactRepo.findById(cid).get();

		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

//    if we are using post then the url copied cannot be paste in another browser to look 
//	when url copied that is get method request
//	update form handler

//    @PostMapping("/process-update")
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("proImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {

		try {

//    		old conatct detais
			Contact oldcontact = this.contactRepo.findById(contact.getCid()).get();

			if (!file.isEmpty()) {

//    			delete old photo
//    			
				File deleteFile = new ClassPathResource("static/image").getFile();
				File file1 = new File(deleteFile, oldcontact.getImage());
				file1.delete();

//    			new file upload

				File saveFile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldcontact.getImage());

			}

			User user = this.repo.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepo.save(contact);

			session.setAttribute("message", new Message("your contact is updated", "success"));

		} catch (Exception e) {
			// TODO: handle exception
		}

		System.out.println(contact.getName());
		System.out.println(contact.getCid());

		return "redirect:/user/" + contact.getCid() + "/contact";

//    	return "redirect:/user/"+contact.getCid()+"/contact";
	}

//    admin controller setting

	@GetMapping("/profile")
	public String admin(Model model) {

		model.addAttribute("title", "profile page");

		return "normal/profile";

	}

//    open setting handler

	@GetMapping("/settings")
	public String openSetting() {

		return "/normal/settings";
	}

//    change password

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession httpSession) {

		System.out.println("old password" + oldPassword);
		System.out.println("new password" + newPassword);

//    	get the old passwrood

		String name = principal.getName();
		User currentName = this.repo.getUserByUserName(name);
		System.out.println(currentName.getPassword());

		if (this.bCryptPasswordEncoder.matches(oldPassword, currentName.getPassword())) {

//    		change the password

			currentName.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.repo.save(currentName);

			httpSession.setAttribute("message", new Message("your password  is changed", "success"));

		} else {
//    		error
			httpSession.setAttribute("message", new Message("please enter correct old password", "danger"));

			return "redirect:/user/settings";

		}

		return "redirect:/user/index";

	}

//	creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws RazorpayException {

		System.out.println("hey order function ex");

		System.out.println(data);

		int amt = Integer.parseInt(data.get("amount").toString());
//		java 11 var 

		var Client = new RazorpayClient("rzp_test_2oewnfAt1uEEm2", "a1vPX5Vf4gHr9K9Wxagujw1J");

		JSONObject ob = new JSONObject();
		ob.put("amount", amt * 100);
		ob.put("currency", "INR");
		ob.put("receipt", "txn_23338");

//		 order new creating

		Order order = Client.Orders.create(ob);
		System.out.println(order);

//		 save the order in db

//		 if we want we can save to your database

		MyOrder myOrder = new MyOrder();
		
		myOrder.setAmount(order.get("amount")+"");
		myOrder.setOrderId(order.get("id"));
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setUser(this.repo.getUserByUserName(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));

		this.myOrderRepo.save(myOrder);

		return order.toString();

	}
	
	
//	update_order
	
	@PostMapping("/update_order")
	public ResponseEntity<?>update_order(@RequestBody Map<String ,Object> data){
		
		
		
		MyOrder findByOrderId = this.myOrderRepo.findByOrderId(data.get("order_id").toString());
		
		findByOrderId.setPaymentId(data.get("payment_id").toString());
		findByOrderId.setStatus(data.get("status").toString());
		
		this.myOrderRepo.save(findByOrderId);
		
		
		
		
		System.out.println(data);
		
		return ResponseEntity.ok(Map.of("msg","updated"));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
