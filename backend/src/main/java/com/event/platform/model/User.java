package com.event.platform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Name is required")
	@Size(max = 100, message = "Name must not exceed 100 characters")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Please provide a valid email address")
	@Column(unique = true, nullable = false)
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;

	@NotNull(message = "Role is required")
	@Enumerated(EnumType.STRING)
	private Role role;

	public User() {
	}

	public User(Long id, String name, String email, String password, Role role) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	// Static Builder Pattern for consistency
	public static UserBuilder builder() {
		return new UserBuilder();
	}

	public static class UserBuilder {
		private Long id;
		private String name;
		private String email;
		private String password;
		private Role role;

		public UserBuilder id(Long id) {
			this.id = id;
			return this;
		}

		public UserBuilder name(String name) {
			this.name = name;
			return this;
		}

		public UserBuilder email(String email) {
			this.email = email;
			return this;
		}

		public UserBuilder password(String password) {
			this.password = password;
			return this;
		}

		public UserBuilder role(Role role) {
			this.role = role;
			return this;
		}

		public User build() {
			return new User(id, name, email, password, role);
		}
	}
}
