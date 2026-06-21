package com.event.platform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attendees")
public class Attendee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Attendee name is required")
	@Size(max = 100, message = "Name must not exceed 100 characters")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Please provide a valid email address")
	@Column(unique = true, nullable = false)
	private String email;

	@OneToMany(mappedBy = "attendee", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Registration> registrations = new ArrayList<>();

	public Attendee() {
	}

	public Attendee(Long id, String name, String email, List<Registration> registrations) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.registrations = registrations != null ? registrations : new ArrayList<>();
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

	public List<Registration> getRegistrations() {
		return registrations;
	}

	public void setRegistrations(List<Registration> registrations) {
		this.registrations = registrations;
	}

	// Simple Builder Pattern implementation
	public static AttendeeBuilder builder() {
		return new AttendeeBuilder();
	}

	public static class AttendeeBuilder {
		private Long id;
		private String name;
		private String email;
		private List<Registration> registrations;

		public AttendeeBuilder id(Long id) {
			this.id = id;
			return this;
		}

		public AttendeeBuilder name(String name) {
			this.name = name;
			return this;
		}

		public AttendeeBuilder email(String email) {
			this.email = email;
			return this;
		}

		public AttendeeBuilder registrations(List<Registration> registrations) {
			this.registrations = registrations;
			return this;
		}

		public Attendee build() {
			return new Attendee(id, name, email, registrations);
		}
	}
}
