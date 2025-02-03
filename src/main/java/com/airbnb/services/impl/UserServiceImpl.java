package com.airbnb.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.airbnb.entities.User;
import com.airbnb.repositories.UserRepository;
import com.airbnb.services.IUserService;

@Service
public class UserServiceImpl implements IUserService {

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public void createUser(User user) {
		userRepository.save(user);
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

}
