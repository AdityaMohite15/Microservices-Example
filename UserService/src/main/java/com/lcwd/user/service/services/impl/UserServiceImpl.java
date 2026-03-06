package com.lcwd.user.service.services.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exceptions.ResourceNotFoundException;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private HotelService hotelService;
	
//	private Logger logger = (Logger) LoggerFactory.getLogger(UserServiceImpl.class);

    private static final Logger logger =
            LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Override
	public User saveUser(User user) {
		//generate user user id
		String randomUserId = UUID.randomUUID().toString();
		user.setUserId(randomUserId);
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUser() {
		
		
		List<User> users = userRepository.findAll();
		return users;
	}

	@Override
	public User getUser(String userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id not found on server !!"+userId));
	
		// fetch rating of the above user from rating service
		//		http://localhost:8083/ratings/users/79c5f4b4-3b3c-418b-9225-1748d5cbbd7e
		
		
		
		Rating[] ratingsOfUser = restTemplate.getForObject("http://RATINGSERVICE/ratings/users/"+user.getUserId(), Rating[].class);
		logger.info("{}",ratingsOfUser);
		
		List<Rating> ratings = Arrays.stream(ratingsOfUser).toList();
		
		List<Rating> ratingList =  ratings.stream().map(rating -> {
			//api call to hotel service to get the hotel S
//			http://localhost:8082/hotels/e3eb2cbd-00e0-4381-a182-e90eb692b038
			
			ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTELSERVICE/hotels/"+rating.getHotelId(), Hotel.class);
			Hotel hotel = forEntity.getBody();
//			Hotel hotel = hotelService.getHotel(rating.getHotelId());
			logger.info("response status code",forEntity.getStatusCode());
			
			// set the hotel to rating 
			rating.setHotel(hotel);
			// return the rating 
			
			return rating;
			
		}).collect(Collectors.toList());
		
		user.setRatings(ratingList);
		
		return user;
	}

	@Override
	public boolean deleteUser(String userId) {
		try {
			userRepository.deleteById(userId);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
	}

}
