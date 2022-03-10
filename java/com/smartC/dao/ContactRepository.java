package com.smartC.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartC.entities.Contacts;
import com.smartC.entities.User;

public interface ContactRepository extends JpaRepository<Contacts, Integer> {
	
	//pagination ................

	@Query("from Contacts as c where c.user.id =:userId")
	public java.util.List<Contacts> findContactsByUser(@Param("userId")int userId);
   
	//searching method
	public java.util.List<Contacts> findByNameContainingAndUser(String name, User user);	

}
