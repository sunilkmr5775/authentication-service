package com.userservice.app.service;

import org.springframework.stereotype.Service;

import com.userservice.app.models.Users;

@Service
public interface EmailService {

	boolean sendVerificationEmail(Users user, String siteUrl);

	boolean verify(String code);
}
