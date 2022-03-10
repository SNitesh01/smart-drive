package com.smartC.controller;

import com.razorpay.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.catalina.User;
import org.apache.tomcat.jni.File;
import org.aspectj.bridge.Message;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.ExpressionException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.authentication.PasswordEncoderParser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.smartC.dao.ContactRepository;
import com.smartC.dao.UserRepository;
import com.smartC.entities.Contacts;

import ch.qos.logback.core.FileAppender;
import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	//method or adding common data
	@ModelAttribute
	public void addCommonData(Model model,Principal principal)
	{
		String userName = principal.getName();
	  	System.out.println("USERNAME" + userName);
	  	
	  	//get the user using user name
	  	
	  	// User user = (User) userRepository.getUserByUserName(userName);
		com.smartC.entities.User user =  userRepository.getUserByUserName(userName);
		System.out.println("USER " +user);
        model.addAttribute("user" ,user);
		
	}
	//Dashboard home
	 @RequestMapping("/index")
	 public String dashboard(Model model, Principal principal )
	{
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	//Add form Handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contacts", new Contacts());
		return "normal/add_contact_form";
	}
	
	//processing add-contact form
	@PostMapping("/process-contact")
	public String processcontact(@ModelAttribute Contacts contact,
			@RequestParam("profileImage") 
	        MultipartFile file, Principal principal, HttpSession session)
	{
		try {
		String name = principal.getName();
		com.smartC.entities.User user = this.userRepository.getUserByUserName(name);
		
		contact.setUser(user);
		
		//processing and uploading file
		if(file.isEmpty())
		{
			System.out.println("File is empty");
			contact.setImage("contact.png");
		
		}else {
			//upload file to folder or update name to contact
			contact.setImage(file.getOriginalFilename());
			java.io.File saveFile = new ClassPathResource("static/Img").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath()+java.io.File.separator+file.getOriginalFilename());
			
			
			Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println("img is uploaded");
			
		}
		
		user.getCon().add(contact);
		this.userRepository.save(user);
		
		System.out.println("DATA"+contact);
		System.out.println("Added to database");
		
		//message success...............
		  session.setAttribute("message", new com.smartC.healper.Message("Succesfully added", "success"));
		
		} catch(Exception e) {
			
			System.out.println("Error"+e.getMessage());
			e.printStackTrace();
			//Error message...............
			  session.setAttribute("message", new com.smartC.healper.Message("Something went wrong", "danger"));

		}

		return "normal/add_contact_form";
	}
	//show contacts handler
	@GetMapping("/showContacts")
	public String showContacts(Model m, Principal principal) {
		m.addAttribute("title", "show User Contacts");
		
	//fetching contacts list of users
	    String userName =principal.getName();
		com.smartC.entities.User user = this.userRepository.getUserByUserName(userName);
  
	    List<Contacts> contacts = this.contactRepository.findContactsByUser(user.getId());
	    m.addAttribute("contacts", contacts);
	    
		
		return "normal/showContacts";
	}
	
	//showing specific contact details
	
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model mo, Principal principal)
	{
		System.out.println("cid"+ cId);
		
	    java.util.Optional<Contacts> contactsOptional =this.contactRepository.findById(cId);
	    Contacts contacts = contactsOptional.get();
	    //bug handel
	    String userName = principal.getName();
		com.smartC.entities.User user = this.userRepository.getUserByUserName(userName);
	    
	    if(user.getId() == contacts.getUser().getId())
	    {
		    mo.addAttribute("contacts", contacts);
		    mo.addAttribute("title", contacts.getName());

	    }
	    
		return "normal/contact_details";
	}


  //delete contact handler
  
  @GetMapping("/delete/{cId}") 
  public String deleteContact(@PathVariable("cId")Integer cId, Model mod, HttpSession session)        
  {
  
  java.util.Optional<Contacts> contactsOptional =this.contactRepository.findById(cId); 
  Contacts contacts =contactsOptional.get();
  
  System.out.println("CID" +cId); //check
  System.out.println("Contacts "+contacts.getcId());
   contacts.setUser(null);
  
  this.contactRepository.delete(contacts); 
  
  System.out.println("DELETED");
  session.setAttribute("message", new com.smartC.healper.Message("Contact deleted..", "danger"));                                                   
  
  return "redirect:/user/showContacts"; 
  }
  
  //open update form handler                                                                                       
                                                                                                   
	 @PostMapping("/update-contact/{cId}")                                             
	 public String udateForm(@PathVariable("cId") Integer cId, Model m)
	 {
	  m.addAttribute("title", "Update Contact"); 
	  Object contacts =this.contactRepository.findById(cId).get();        
	  m.addAttribute("contacts", contacts);
	  
	  return "normal/update_Form";                                                                                      
	  
	  }
	 //update contact handler
	 @RequestMapping(value= "/process-update", method= RequestMethod.POST)
	 public String updateHandler(@ModelAttribute Contacts contacts,
			                     @RequestParam("profileImage") MultipartFile file, Model m,
			                     HttpSession session, Principal principal)
	 {
		 try {
			 //old contacts details
			 Contacts oldcontactDetail = this.contactRepository.findById(contacts.getcId()).get();
			 
			 
			 //image............
			 if(!file.isEmpty())
			 {
			    //file working
			    //rewrite
				 //delete old photo
				  java.io.File deleteFile = new ClassPathResource("static/Img").getFile();
                  java.io.File file1 = new java.io.File(deleteFile, oldcontactDetail.getImage());
				  file1.delete();
				 
				 //update new photo
				 java.io.File saveFile = new ClassPathResource("static/Img").getFile();
					
				 Path path = Paths.get(saveFile.getAbsolutePath()+java.io.File.separator+file.getOriginalFilename());
			     Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
			     contacts.setImage(file.getOriginalFilename());
					
			 }else {
				 contacts.setImage(oldcontactDetail.getImage());
			 }
			com.smartC.entities.User user =  this.userRepository.getUserByUserName(principal.getName());
             contacts.setUser(user);
			 this.contactRepository.save(contacts);
			 session.setAttribute("message", new com.smartC.healper.Message("your contact is updated", "success"));
			 
		 }catch (Exception e) {
			 
			 e.printStackTrace();
		 }
		 System.out.println("CONTACT NAME" +contacts.getName());
		 System.out.println("CONTACT Id" +contacts.getcId());		 
		
		 return  "redirect:/user/"+contacts.getcId()+"/contact";
	 }
	 
	 //your profile handler
	 @GetMapping("/profile")
	 public String yourProfile(Model model)
	 {
		 model.addAttribute("title", "Profile Page");
		 
		 return "normal/profile";
	 }
	 @GetMapping("/about")
	 public String aboutProfile(Model model)
	 {
		 model.addAttribute("title", "About Page");
		 
		 return "templates/about";
	 }
	 
	 //search controller
	 @RestController
	 public class SearchController {
		 @Autowired
		 private UserRepository userRepository;
		 @Autowired
		 private ContactRepository contactRepository;
		 
		//search handler
		 @GetMapping("/search/{query}")
		 public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal)
		 {
			 System.out.println(query);
			 com.smartC.entities.User user =  this.userRepository.getUserByUserName(principal.getName());

			 List<Contacts> contacts =this.contactRepository.findByNameContainingAndUser(query, user);
			 
			 return ResponseEntity.ok(contacts);
		 }
		 
	 }
  //open setting handler
	 @GetMapping("/settings")
	 public String openSettings()
	 {
		 
		 return "normal/settings";
	 }
  
  //change paasword handler
	 @PostMapping("/change-password")
	 public String changePassword(@RequestParam("oldpassword") String oldpassword, 
			                      @RequestParam("newpassword") String newpassword,
			                      Principal principal, HttpSession session)
	 {
		 System.out.println("OLD PASSWORD"+ oldpassword);
		 System.out.println("NEW PASSWORD"+ newpassword);
		 String userName = principal.getName();
		 com.smartC.entities.User currentUser = this.userRepository.getUserByUserName(userName);
         
		 System.out.println(currentUser.getPassword());
		 if(this.bCryptPasswordEncoder.matches(oldpassword, currentUser.getPassword()))
		 {
			 //change the password
			 currentUser.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
			 this.userRepository.save(currentUser);
			 session.setAttribute("message", new com.smartC.healper.Message("Password Changed !!", "alert-success"));

		 }else {
			 //error
			  session.setAttribute("message", new com.smartC.healper.Message("old password Wrong !!", "alert-error"));
			  return "redirect:/user/settings";
		 }
		 
		 return "redirect:/user/index";
	 }
	 
	//creating order for payment
	 @PostMapping("/create_order")
	 @ResponseBody
	 public String createOrder(@RequestBody Map<String, Object> data) throws Exception
	 {
		// System.out.println("hey order function works");
		 
		 System.out.println(data);
		 int amt= Integer.parseInt(data.get("amount").toString()); 
		 
		 var client = new RazorpayClient("rzp_test_I3ZIdivcRRZKWF", "4q93NfXOHX7fyG6gK94p1tyb");
		 
		 JSONObject ob=new JSONObject();
		 ob.put("amount", amt*100);
		 ob.put("currency", "INR");
		 ob.put("receipt", "txn_545665");
		 
		 //creating new order
		 Order order = client.Orders.create(ob);
		 System.out.println(order);
		 
		 
		 return order.toString();
	 }
	 
	 
	 
  }
 