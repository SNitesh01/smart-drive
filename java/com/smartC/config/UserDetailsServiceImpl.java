package com.smartC.config;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartC.dao.UserRepository;

 public class UserDetailsServiceImpl  implements UserDetailsService {
   
	@Autowired
	private UserRepository userRepository;
	
		@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//Fetching user from databases
			com.smartC.entities.User user =  userRepository.getUserByUserName(username);
      
		if(user==null) 
		{
			throw new UsernameNotFoundException("could not found");
		}
      
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		return  customUserDetails;
	}

 }
