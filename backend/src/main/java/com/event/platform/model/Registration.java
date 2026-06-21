package com.event.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
	name = "registrations",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"event_id", "attendee_id"})
	}
)
public class Registration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attendee_id", nullable = false)
	private Attendee attendee;

	@Column(name = "registration_date", nullable = false)
	private LocalDateTime registrationDate = LocalDateTime.now();

	public Registration() {
	}

	public Registration(Long id, Event event, Attendee attendee, LocalDateTime registrationDate) {
		this.id = id;
		this.event = event;
		this.attendee = attendee;
		this.registrationDate = registrationDate != null ? registrationDate : LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Attendee getAttendee() {
		return attendee;
	}

	public void setAttendee(Attendee attendee) {
		this.attendee = attendee;
	}

	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	// Simple Builder Pattern implementation
	public static RegistrationBuilder builder() {
		return new RegistrationBuilder();
	}

	public static class RegistrationBuilder {
		private Long id;
		private Event event;
		private Attendee attendee;
		private LocalDateTime registrationDate;

		public RegistrationBuilder id(Long id) {
			this.id = id;
			return this;
		}

		public RegistrationBuilder event(Event event) {
			this.event = event;
			return this;
		}

		public RegistrationBuilder attendee(Attendee attendee) {
			this.attendee = attendee;
			return this;
		}

		public RegistrationBuilder registrationDate(LocalDateTime registrationDate) {
			this.registrationDate = registrationDate;
			return this;
		}

		public Registration build() {
			return new Registration(id, event, attendee, registrationDate);
		}
	}
}
