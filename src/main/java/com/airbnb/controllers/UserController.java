package com.airbnb.controllers;

// import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @Controller
// @ResponseBody
public class UserController {

	@GetMapping("/")
	public String getHomePage() {
		return "OK";
		// return "hello.html";
	}
}
