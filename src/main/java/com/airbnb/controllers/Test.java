package com.airbnb.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.airbnb.entities.User;
import com.airbnb.repositories.UserRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class Test {

	private final UserRepository userRepository;

	@GetMapping(value = "hello")
	public ResponseEntity<String> helloWorld() {
		return new ResponseEntity<String>("Hello", HttpStatus.OK);
	}

	@PostMapping(value = "hello")
	public ResponseEntity<?> creatEntity() {
		User user = new User();
		user.setId(1);
		user.setName("vinh");
		user.setEmail("vinh@gmail.com");
		userRepository.save(user);
		return new ResponseEntity<String>("Hello", HttpStatus.OK);
	}

}
