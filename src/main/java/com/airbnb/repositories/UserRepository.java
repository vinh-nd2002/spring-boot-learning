package com.airbnb.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.airbnb.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.posts")
	List<User> findAllWithPosts();

	// @Query("SELECT u FROM User u LEFT JOIN FETCH Post p ON u.id = p.user.id")
	// List<User> findAllWithPosts();

	@EntityGraph(value = "user-posts", type = EntityGraph.EntityGraphType.FETCH)
	List<User> findAll();

	Page<User> findAll(Pageable page);

	Page<User> findAll(Specification<User> spec, Pageable page);

	Optional<User> findByEmail(String username);
}
