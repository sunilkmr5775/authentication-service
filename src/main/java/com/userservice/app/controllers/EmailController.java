package com.userservice.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.app.repository.UserRepository;
import com.userservice.app.service.EmailService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class EmailController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	EmailService emailService;

	@GetMapping("/verify")
	public String verifyUser(@Param("code") String code) {
		if (emailService.verify(code)) {
			return "verify_success";
		} else {
			return "You are account has already been verified.";
		}
	}

}
