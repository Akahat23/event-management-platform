package com.event.platform.config;

import com.event.platform.model.Role;
import com.event.platform.model.User;
import com.event.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) throws Exception {
		// 1. Seed Admin Account if not exists
		if (!userRepository.existsByEmail("admin@event.com")) {
			User admin = User.builder()
					.name("Admin Account")
					.email("admin@event.com")
					.password(passwordEncoder.encode("admin123"))
					.role(Role.ROLE_ADMIN)
					.build();
			userRepository.save(admin);
			System.out.println("Seeded default administrator: admin@event.com / admin123");
		}

		// 2. Seed Standard User Account if not exists
		if (!userRepository.existsByEmail("user@event.com")) {
			User user = User.builder()
					.name("Akshat Singh")
					.email("user@event.com")
					.password(passwordEncoder.encode("user123"))
					.role(Role.ROLE_USER)
					.build();
			userRepository.save(user);
			System.out.println("Seeded default user: user@event.com / user123");
		}
	}
}
