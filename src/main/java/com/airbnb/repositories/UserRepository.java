package com.airbnb.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.airbnb.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

//	@Query("SELECT u FROM User u LEFT JOIN FETCH Post p ON u.id = p.user.id")
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.posts")
	List<User> findAllWithPosts();
	
}
