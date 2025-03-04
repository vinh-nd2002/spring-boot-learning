package com.airbnb.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Post {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment
	private Integer id; // Recommend using wrapper classes instead of primitive types
	
	@Column(name = "title", unique = true, nullable = false)
	private String title;
	
	@Column(name = "description", nullable = false)
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
	private User user;
	
	@Column(name = "created_at")
	@CreationTimestamp
	private Instant createdAt;
	
	@Column(name = "updated_at")
	@CreationTimestamp
	private Instant updatedAt;
	
}
