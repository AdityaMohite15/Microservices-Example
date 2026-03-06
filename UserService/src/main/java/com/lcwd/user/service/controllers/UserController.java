package com.lcwd.user.service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.services.UserService;

import ch.qos.logback.classic.Logger;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user){
		User user1 = userService.saveUser(user);
		return new ResponseEntity<User>(user1,HttpStatus.CREATED);
		
	}
	
	int retryCount = 1;
	// Getting single user
	
	@GetMapping("/{userId}")
	@CircuitBreaker(name = "ratingHotelBreaker",fallbackMethod = "ratingHotelFallback")
//	@Retry(name = "ratingHotelService",fallbackMethod = "ratingHotelFallback")
	public ResponseEntity<User> getSingleUser(@PathVariable String userId){
//		System.out.println("Retry Count : {}" + retryCount);
//		retryCount++;
		User user = userService.getUser(userId);
		return new ResponseEntity<User>(user,HttpStatus.OK);
	}
	 
	// creating fallback method for circuit breaker
	
	public ResponseEntity<User> ratingHotelFallback(String userId,Exception ex){

		User user = User.builder()
			.email("dummy@gmail.com")
			.name("Dummy")
			.about("This user is created dummy because some service is down ")
			.userId("87134")
			.build();
		return new ResponseEntity<User>(user, HttpStatus.OK);
		
	}
	
	@GetMapping
	public ResponseEntity<List<User>> getAllUser(){
		List<User> allUser = userService.getAllUser();
		return new ResponseEntity<List<User>>(allUser,HttpStatus.OK);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable String userId){
		boolean deleted = userService.deleteUser(userId);
		if(deleted) {
			return new ResponseEntity<>("Deletedd the User Successfully",HttpStatus.OK);
		}
		return new ResponseEntity<>("Not deleted",HttpStatus.NOT_FOUND);
		
		
	}
}
