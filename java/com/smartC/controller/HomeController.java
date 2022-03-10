package com.smartC.controller;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartC.dao.UserRepository;
import com.smartC.entities.User;
import com.smartC.healper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/home")
	public String home(Model model)
	{
		model.addAttribute("title", "Home - Smart-CM");
		return "home";
	}
	@RequestMapping("/About")
	public String About(Model model)
	{
		model.addAttribute("title", "About - Smart-CM");
		return "About";
	}
	@RequestMapping("/signup")
	public String signup(Model model)
	{
		model.addAttribute("title", "Register - Smart-CM");
		model.addAttribute("user", new User());
		return "signup";
	}
//handler for register user
	@RequestMapping(value= "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result, @RequestParam(value= "agreement",defaultValue="false") boolean agreement, 
			 Model model,HttpSession session)
	{
		try {

			if(!agreement)
			{
				System.out.println("You have not agreed terms & conditions.");
				throw new Exception("you have not agreed the terms & conditions");
			}
			if(result.hasErrors())
			{
				System.out.println("Error" +result.toString());
				model.addAttribute("User" ,user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			System.out.println("Agreement "+ agreement);
			System.out.println("USER "+ user);
			User result1  = this.userRepository.save(user);
			
			model.addAttribute("user", new User());
			
			session.setAttribute("message", new Message("Succesfully Register!!","alert-succes"));
			return "signup";
			
			
			}catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Wrong!!" +e.getMessage(),"alert-danger"));
			return "signup";
		}
	
	}
	//handler for customLogin
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title","Login Page");
		return "login";
	}

}
