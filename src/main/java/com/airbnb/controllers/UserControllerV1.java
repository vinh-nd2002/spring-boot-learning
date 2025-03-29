package com.airbnb.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.airbnb.dto.req.CreateUserReq;
import com.airbnb.dto.res.UserDTO;
import com.airbnb.dto.res.WithPaginationDTO;
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
		System.out.println("Controller: getAllUsers() method called");
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

	@GetMapping(value = "/search")
	public ResponseEntity<List<UserDTO>> getAllUsersWithSearch(
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer limit) {
		int currentPage = (page == null || page == 1) ? 0 : (page - 1);

		Pageable pageable = PageRequest.of(currentPage, limit);
		Page<User> users = userServiceImpl.getAllUsers(pageable);

		List<UserDTO> userDTOs = users.getContent().stream().map((user) -> {
			UserDTO userDTO = UserDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
			return userDTO;
		}).collect(Collectors.toList());

		return new ResponseEntity<List<UserDTO>>(userDTOs, HttpStatus.OK);
	}

	@GetMapping(value = "/search-v2")
	public ResponseEntity<WithPaginationDTO<UserDTO>> getAllUsersWithSearch(
			@RequestParam(required = false) int page,
			@RequestParam(required = false) int limit) {
		// Optional<String> page, Optional<String> limit

		int currentPage = (page == 1 || page == 0) ? 0 : (page - 1);
		int pageSize = (limit < 1) ? 10 : limit;
		Pageable pageable = PageRequest.of(currentPage, pageSize);
		Page<User> userPage = userServiceImpl.getAllUsers(pageable);

		List<UserDTO> userDTOs = userPage.getContent().stream().map((user) -> {
			UserDTO userDTO = UserDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
			return userDTO;
		}).collect(Collectors.toList());

		WithPaginationDTO<UserDTO> response = WithPaginationDTO.<UserDTO>builder()
				.currentPage(userPage.getNumber() + 1)
				.limit(userPage.getSize())
				.totalItems(userPage.getTotalElements())
				.totalPages(userPage.getTotalPages())
				.items(userDTOs)
				.build();
		return new ResponseEntity<WithPaginationDTO<UserDTO>>(response, HttpStatus.OK);
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
		System.out.println("Controller: createNewUser() method called");
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
