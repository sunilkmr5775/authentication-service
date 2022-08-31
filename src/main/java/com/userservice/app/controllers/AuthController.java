package com.userservice.app.controllers;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.app.constant.StatusConstant;
import com.userservice.app.exception.NoDataFoundException;
import com.userservice.app.jwt.JwtUtils;
import com.userservice.app.models.ERole;
import com.userservice.app.models.Role;
import com.userservice.app.models.Users;
import com.userservice.app.repository.RoleRepository;
import com.userservice.app.repository.UserRepository;
import com.userservice.app.request.LoginRequest;
import com.userservice.app.request.SignupRequest;
import com.userservice.app.response.JwtResponse;
import com.userservice.app.response.MessageResponse;
import com.userservice.app.service.EmailService;
import com.userservice.app.services.impl.UserDetailsImpl;

import net.bytebuddy.utility.RandomString;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Component
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private EmailService emailService;
	
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(
				new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(// @Valid
			@RequestBody SignupRequest signUpRequest, HttpServletRequest request
	// ,@RequestPart(value = "file" , required=true) MultipartFile file
	) throws NoDataFoundException, UnsupportedEncodingException
	// , MessagingException
	{
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		Users user = new Users();
		user.setCaptcha("");
		user.setContactNumber(signUpRequest.getContactNumber());
		user.setCreatedBy("sunilkmr5775");
		user.setCreatedDate(LocalDateTime.now());
		user.setDateOfBirth(signUpRequest.getDd() + "-" + signUpRequest.getMm() + "-" + signUpRequest.getYyyy());
		user.setEmail(signUpRequest.getEmail());
		user.setFirstName(signUpRequest.getFirstName());
		user.setMiddleName(signUpRequest.getMiddleName());
		user.setLastName(signUpRequest.getLastName());
//		user.setPanNumber(signUpRequest.getPanNumber());
		user.setPassword(encoder.encode(signUpRequest.getPassword()));
		user.setStatus(StatusConstant.STATUS_INACTIVE);
		user.setGender(signUpRequest.getGender());
		user.setUsername(signUpRequest.getUsername());

		Set<String> strRoles = new HashSet();// signUpRequest.getRole();
		strRoles.add("ROLE_ADMIN");
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByRoleName(ERole.ROLE_NORMAL)
					.orElseThrow(() -> new RuntimeException(ERole.ROLE_NORMAL + " Error: Role is not found in DB."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role == null ? "ROLE_NORMAL" : role) {
				case "ROLE_ADMIN":
					Role adminRole = roleRepository.findByRoleName(ERole.ROLE_ADMIN).orElseThrow(
							() -> new NoDataFoundException("Error:  Role " + role + " is not found in DB."));

					roles.add(adminRole);

					break;
				case "ROLE_MODERATOR":
					Role modRole = roleRepository.findByRoleName(ERole.ROLE_MODERATOR).orElseThrow(
							() -> new NoDataFoundException("Error: Role " + role + " is not found in DB."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByRoleName(ERole.ROLE_NORMAL).orElseThrow(
							() -> new NoDataFoundException("Error: Role " + role + " is not found in DB."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);

		String randomCode = RandomString.make(64);
		user.setVerificationCode(randomCode);
		user.setEmailVerified(false);

//		Long id = userRepository.save(user).getId();
		userRepository.save(user);
		emailService.sendVerificationEmail(user, getSiteURL(request));

		return ResponseEntity
				.ok(new MessageResponse("Username " + signUpRequest.getUsername() + " registered successfully!"));
	}
	
//	 Returns the details of current user
	@GetMapping(value = "/current-user")
	public Users getCurrentUser(Principal principal) {
		try {
		System.out.println("username in controller: " + principal.getName());
		return ((Users) this.userDetailsService.loadUserByUsername(principal.getName()));
		} catch(Exception ex) {
			System.out.println("Error occured at line no. 177: "+ex.getMessage());
			throw new NullPointerException();
		}
	}
	
	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}
}
