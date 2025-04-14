package com.airbnb.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
import org.springframework.web.servlet.NoHandlerFoundException;

import com.airbnb.dto.req.CreateUserReq;
import com.airbnb.dto.req.criteria.UserCriteria;
import com.airbnb.dto.res.ResponseDTO;
import com.airbnb.dto.res.UserDTO;
import com.airbnb.dto.res.WithPaginationResponseDTO;
import com.airbnb.entities.User;
import com.airbnb.exception.EntityNotFoundException;
import com.airbnb.exception.TestException;
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

		return ResponseEntity.ok().body(userDTOs);
	}

	@GetMapping(value = "/search")
	public ResponseEntity<List<UserDTO>> getAllUsersWithSearch(
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer size) {
		int currentPage = (page == null || page == 1) ? 0 : (page - 1);

		Pageable pageable = PageRequest.of(currentPage, size);
		Page<User> users = userServiceImpl.getAllUsers(pageable);

		List<UserDTO> userDTOs = users.getContent().stream().map((user) -> {
			UserDTO userDTO = UserDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
			return userDTO;
		}).collect(Collectors.toList());

		return ResponseEntity.ok().body(userDTOs);
	}

	@GetMapping(value = "/search-v2")
	public ResponseEntity<WithPaginationResponseDTO<UserDTO>> getAllUsersWithSearch(
			@RequestParam(required = false) int page,
			@RequestParam(required = false) int size,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) Set<Long> ids) {
		// Optional<String> page, Optional<String> size

		int currentPage = (page == 1 || page == 0) ? 0 : (page - 1);
		int pageSize = (size < 1) ? 10 : size;
		Pageable pageable = PageRequest.of(currentPage, pageSize);
		// Page<User> userPage = userServiceImpl.getAllUsers(pageable);
		// Page<User> userPage = userServiceImpl.getAllUsersV2(name, pageable);
		// Page<User> userPage = userServiceImpl.getAllUsersV2(name, email, pageable);
		Page<User> userPage = userServiceImpl.getAllUsersV2(ids, pageable);

		List<UserDTO> userDTOs = userPage.getContent().stream().map((user) -> {
			UserDTO userDTO = UserDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
			return userDTO;
		}).collect(Collectors.toList());

		WithPaginationResponseDTO<UserDTO> response = WithPaginationResponseDTO.<UserDTO>builder()
				.currentPage(userPage.getNumber() + 1)
				.size(userPage.getSize())
				.totalItems(userPage.getTotalElements())
				.totalPages(userPage.getTotalPages())
				.items(userDTOs)
				.build();
		// return new ResponseEntity<WithPaginationDTO<UserDTO>>(response,
		// HttpStatus.OK);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping(value = "/search-v3")
	public ResponseEntity<ResponseDTO> getAllUsersWithSearchV3(
			Pageable pageable, UserCriteria userCriteria) {
		Page<User> userPage = userServiceImpl.getAllUsersWithSpec(userCriteria, pageable);

		List<UserDTO> userDTOs = userPage.getContent().stream().map((user) -> {
			UserDTO userDTO = UserDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
			return userDTO;
		}).collect(Collectors.toList());

		WithPaginationResponseDTO<UserDTO> withPaginationResponseDTO = WithPaginationResponseDTO.<UserDTO>builder()
				.currentPage(userPage.getNumber() + 1)
				.size(userPage.getSize())
				.totalItems(userPage.getTotalElements())
				.totalPages(userPage.getTotalPages())
				.items(userDTOs)
				.build();

		ResponseDTO response = ResponseDTO.builder().message("Get data success").data(withPaginationResponseDTO)
				.build();
		return ResponseEntity.ok().body(response);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<ResponseDTO> getUserById(@PathVariable(required = true) Integer id) throws Exception {

		User userOpt = userServiceImpl.getUserById(id)
				.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		System.out.println(userOpt.getPosts());

		UserDTO userInfo = UserDTO.builder().id(userOpt.getId()).name(userOpt.getName()).email(userOpt.getEmail())
				.build();

		ResponseDTO response = ResponseDTO.builder().data(userInfo).message("Get data success").build();
		return ResponseEntity.ok().body(response);

	}

	@GetMapping(value = "/test-exception")
	public ResponseEntity<String> testException() {
		throw new TestException("Test exception");

		// return ResponseEntity.status(HttpStatus.OK).body("Test exception");

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

		return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
	}
}
