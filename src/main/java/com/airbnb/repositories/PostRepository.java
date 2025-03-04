package com.airbnb.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airbnb.entities.Post;

public interface PostRepository extends JpaRepository<Post, Integer>{

}
