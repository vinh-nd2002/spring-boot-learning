package com.airbnb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class User extends BaseEntity {

	@Column(name = "name")
	private String name;

//  columnDefinition: allows defining custom SQL data types and constraints
//  updatable: allows the column to be updated or not 
	@Column(name = "email", nullable = false, unique = true)
	private String email;

}
