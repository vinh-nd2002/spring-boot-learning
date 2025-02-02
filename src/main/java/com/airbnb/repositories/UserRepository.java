package com.airbnb.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airbnb.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
