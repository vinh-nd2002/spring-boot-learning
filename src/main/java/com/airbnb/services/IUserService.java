package com.airbnb.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.airbnb.entities.User;

public interface IUserService {
	public void createUser(User user);

	public List<User> getAllUsers();

	public Page<User> getAllUsers(Pageable pageable);

	public Optional<User> getUserById(Integer id);

	public List<User> getAllUsersV2();

	public Optional<User> getUserByEmail(String username);
}
