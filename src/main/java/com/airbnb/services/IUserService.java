package com.airbnb.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.airbnb.dto.req.criteria.UserCriteria;
import com.airbnb.entities.User;

public interface IUserService {
	public void createUser(User user);

	public List<User> getAllUsers();

	public Page<User> getAllUsers(Pageable pageable);

	public Page<User> getAllUsersV2(String name, Pageable pageable);

	public Page<User> getAllUsersV2(String name, String email, Pageable pageable);

	public Page<User> getAllUsersV2(Set<Long> ids, Pageable pageable);

	public Page<User> getAllUsersWithSpec(UserCriteria userCriteria, Pageable pageable);

	public Optional<User> getUserById(Integer id);

	public List<User> getAllUsersV2();

	public Optional<User> getUserByEmail(String username);
}
