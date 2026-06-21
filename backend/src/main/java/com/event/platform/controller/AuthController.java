package com.event.platform.controller;

import com.event.platform.dto.AuthResponse;
import com.event.platform.dto.LoginRequest;
import com.event.platform.dto.RegisterRequest;
import com.event.platform.model.Role;
import com.event.platform.model.User;
import com.event.platform.repository.UserRepository;
import com.event.platform.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;
	private final UserDetailsService userDetailsService;

	@Autowired
	public AuthController(
			AuthenticationManager authenticationManager,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			JwtUtils jwtUtils,
			UserDetailsService userDetailsService) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtils = jwtUtils;
		this.userDetailsService = userDetailsService;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email address is already in use: " + request.getEmail());
		}

		User user = User.builder()
				.name(request.getName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(Role.ROLE_USER)
				.build();

		userRepository.save(user);

		final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		final String token = jwtUtils.generateToken(userDetails);

		return new ResponseEntity<>(
				new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name()),
				HttpStatus.CREATED
		);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
		);

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found: " + request.getEmail()));

		final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		final String token = jwtUtils.generateToken(userDetails);

		return ResponseEntity.ok(
				new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name())
		);
	}
}
