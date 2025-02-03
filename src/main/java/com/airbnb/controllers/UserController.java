package com.airbnb.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.airbnb.dto.req.CreateUserReq;
import com.airbnb.dto.res.UserDTO;
import com.airbnb.entities.User;
import com.airbnb.services.impl.UserServiceImpl;

@RestController
@RequestMapping(value = "/users")
public class UserController {
	private final UserServiceImpl userServiceImpl;

	public UserController(UserServiceImpl userServiceImpl) {
		this.userServiceImpl = userServiceImpl;
	}

	@GetMapping(value = "")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<User> users = userServiceImpl.getAllUsers();

		List<UserDTO> userDTOs = new ArrayList<>();
		for (User user : users) {
			UserDTO dto = new UserDTO();
			BeanUtils.copyProperties(user, dto);
			userDTOs.add(dto);
		}

//		List<UserDTO> userDTOs = users.stream().map(user -> {
//			UserDTO userDTO = new UserDTO();
//			BeanUtils.copyProperties(user, userDTO);
//			return userDTO;
//		}).toList();

		return new ResponseEntity<List<UserDTO>>(userDTOs, HttpStatus.OK);
	}

	@PostMapping(value = "")
	public ResponseEntity<String> createNewUser(@RequestBody CreateUserReq createUserReq) {
		User user = new User();

		BeanUtils.copyProperties(createUserReq, user);
		userServiceImpl.createUser(user);

		return new ResponseEntity<String>("Hello", HttpStatus.OK);
	}
}
