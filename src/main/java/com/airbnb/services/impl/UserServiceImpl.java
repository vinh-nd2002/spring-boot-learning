package com.airbnb.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.airbnb.entities.User;
import com.airbnb.entities.User_;
import com.airbnb.repositories.UserRepository;
import com.airbnb.services.IUserService;
import com.airbnb.spec.UserSpec;

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

	public Optional<User> getUserById(Integer id) {
		// this.userRepository.getById(id);
		// this.userRepository.getOne(id);

		return this.userRepository.findById(id);
	}

	public List<User> getAllUsersV2() {
		return userRepository.findAllWithPosts();
	}

	@Override
	public Optional<User> getUserByEmail(String username) {
		return userRepository.findByEmail(username);
	}

	@Override
	public Page<User> getAllUsers(Pageable pageable) {
		return this.userRepository.findAll(pageable);
	}

	@Override
	public Page<User> getAllUsersV2(String name, Pageable pageable) {
		return this.userRepository.findAll(
				Specification.where(UserSpec.likeName(name)),
				pageable);

	}

	@Override
	public Page<User> getAllUsersV2(String name, String email, Pageable pageable) {
		return this.userRepository.findAll(
				Specification.where(UserSpec.likeName(name)).and(UserSpec.likeEmail(email)), pageable);

	}

	@Override
	public Page<User> getAllUsersV2(Set<Long> ids, Pageable pageable) {
		return this.userRepository.findAll(
				Specification.where(UserSpec.byIds(ids)), pageable);
	}
}
