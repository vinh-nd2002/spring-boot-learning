package com.airbnb.services;

import java.util.List;

import com.airbnb.entities.User;

public interface IUserService {
	public void createUser(User user);

	public List<User> getAllUsers();
}
