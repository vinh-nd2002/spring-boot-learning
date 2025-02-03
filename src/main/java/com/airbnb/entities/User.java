package com.airbnb.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor

//Annotation này tạo ra contructor với tất cả property
@AllArgsConstructor

// Annotation này chỉ tạo ra contructor với những property nào là final hoặc là @NonNull
//@RequiredArgsConstructor

// Áp dụng Builder Pattern vào để tạo đối tượng, không cần lo về thứ tự các
// property, không cần tạo nhiều contructor
@Builder
public class User {

	@Id
//	If strategy is AUTO, it always generate <entity>_seq table
//	https://www.truiton.com/2024/02/spring-boot-hibernate-jpa-generationtype-table-vs-sequence-strategy-with-mysql/#Custom-SequenceGenerator
//	https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc#implicit-identifier-sequence-and-table-name
	
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment
	private int id;
	
	@Column(name = "name")
	private String name;

//  columnDefinition: allows defining custom SQL data types and constraints
//  updatable: allows the column to be updated or not 
//	@Column(name = "email", nullable = false, unique = true)
	@Column(name = "email", nullable = false)
	private String email;
	
	@Column(name = "created_at")
	@CreationTimestamp
	private Instant createdAt;
	
	@Column(name = "updated_at")
	@CreationTimestamp
	private Instant updatedAt;

}
