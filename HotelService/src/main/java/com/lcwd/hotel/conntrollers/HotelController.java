package com.lcwd.hotel.conntrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lcwd.hotel.entities.Hotel;
import com.lcwd.hotel.services.HotelService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/hotels")
public class HotelController {

	@Autowired
	private HotelService hotelService;
	
	@PostMapping
	public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel){
		Hotel hotel1 = hotelService.create(hotel);
		return new ResponseEntity<>(hotel1,HttpStatus.CREATED);
		
	}
	
	@GetMapping("/{hotelId}")
	public ResponseEntity<Hotel> getHotel(@PathVariable String hotelId){
		Hotel hotel = hotelService.get(hotelId);
		return new ResponseEntity<Hotel>(hotel,HttpStatus.OK);
		
	}
	
	@GetMapping
	public ResponseEntity<List<Hotel>> getAll(){
		List<Hotel> allHotels = hotelService.getAll();
		return new ResponseEntity<List<Hotel>>(allHotels,HttpStatus.OK);
	}
	
}
