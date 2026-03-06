package com.lcwd.user.service.services;

import java.util.List;

import com.lcwd.user.service.entities.User;

public interface UserService {
	
	//Create
	User saveUser(User user);
	
	//get all users
	List<User> getAllUser();
	
	//get single user 
	User getUser(String userId);
	
	//delete user
	boolean deleteUser(String userId);

}
