package com.airbnb.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class UserControllerV1 {
	private final UserServiceImpl userServiceImpl;
	private final PasswordEncoder passwordEncoder;

	public UserControllerV1(UserServiceImpl userServiceImpl, PasswordEncoder passwordEncoder) {
		this.userServiceImpl = userServiceImpl;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping(value = "")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<User> users1 = userServiceImpl.getAllUsers();

		// List<User> users2 = userServiceImpl.getAllUsersV2();
		List<UserDTO> userDTOs = new ArrayList<>();
		for (User user : users1) {
			UserDTO dto = new UserDTO();
			// System.out.println(user.getPosts());
			dto.setId(user.getId());
			dto.setName(user.getName());
			dto.setEmail(user.getEmail());
			userDTOs.add(dto);
		}

		// System.out.println("----------------------------\n");
		// for (User user : users2) {
		// System.out.println(user.getPosts());
		// }

		// List<UserDTO> userDTOs = users.stream().map(user -> {
		// UserDTO userDTO = new UserDTO();
		// BeanUtils.copyProperties(user, userDTO);
		// return userDTO;
		// }).toList();

		return new ResponseEntity<List<UserDTO>>(userDTOs, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable(required = true) Integer id) throws Exception {

		User userOpt = userServiceImpl.getUserById(id).orElseThrow(() -> new Exception("User not found"));
		System.out.println(userOpt.getPosts());

		UserDTO userInfo = UserDTO.builder().id(userOpt.getId()).name(userOpt.getName()).email(userOpt.getEmail())
				.build();

		return new ResponseEntity<UserDTO>(userInfo, HttpStatus.OK);

	}

	@PostMapping(value = "")
	public ResponseEntity<String> createNewUser(@RequestBody CreateUserReq createUserReq) {
		// TODO: Validate req

		// User user = new User();
		// BeanUtils.copyProperties(createUserReq, user);
		String hashedPassword = passwordEncoder.encode(createUserReq.getPassword());
		User newUser = User.builder().email(createUserReq.getEmail()).name(createUserReq.getName())
				.password(hashedPassword).build();
		userServiceImpl.createUser(newUser);

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}
}
