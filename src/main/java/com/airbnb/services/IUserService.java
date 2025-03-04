package com.airbnb.services;

import java.util.List;
import java.util.Optional;

import com.airbnb.entities.User;

public interface IUserService {
	public void createUser(User user);

	public List<User> getAllUsers();
	
	public Optional<User> getUserById(Integer id);
	
	public List<User> getAllUsersV2();
}
