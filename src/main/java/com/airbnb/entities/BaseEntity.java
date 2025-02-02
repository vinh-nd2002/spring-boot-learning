package com.airbnb.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public class BaseEntity {
	
	@Id
	private int id;
	
	@Column(name = "created_at")
	@CreationTimestamp
	private Instant createdAt;
	
	@Column(name = "updated_at")
	@CreationTimestamp
	private Instant updatedAt;
}
