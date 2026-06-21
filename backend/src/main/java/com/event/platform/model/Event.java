package com.event.platform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Event title is required")
	@Size(max = 100, message = "Event title must not exceed 100 characters")
	private String title;

	@Size(max = 1000, message = "Event description must not exceed 1000 characters")
	@Column(length = 1000)
	private String description;

	@NotNull(message = "Event date and time is required")
	@Future(message = "Event date must be in the future")
	private LocalDateTime dateTime;

	@NotBlank(message = "Event location is required")
	private String location;

	@NotNull(message = "Event capacity is required")
	@Min(value = 1, message = "Event capacity must be at least 1")
	private Integer capacity;

	private Integer registeredCount = 0;

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Registration> registrations = new ArrayList<>();

	public Event() {
	}

	public Event(Long id, String title, String description, LocalDateTime dateTime, String location, Integer capacity, Integer registeredCount, List<Registration> registrations) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.dateTime = dateTime;
		this.location = location;
		this.capacity = capacity;
		this.registeredCount = registeredCount != null ? registeredCount : 0;
		this.registrations = registrations != null ? registrations : new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public Integer getRegisteredCount() {
		return registeredCount;
	}

	public void setRegisteredCount(Integer registeredCount) {
		this.registeredCount = registeredCount;
	}

	public List<Registration> getRegistrations() {
		return registrations;
	}

	public void setRegistrations(List<Registration> registrations) {
		this.registrations = registrations;
	}

	// Simple Builder Pattern implementation
	public static EventBuilder builder() {
		return new EventBuilder();
	}

	public static class EventBuilder {
		private Long id;
		private String title;
		private String description;
		private LocalDateTime dateTime;
		private String location;
		private Integer capacity;
		private Integer registeredCount = 0;
		private List<Registration> registrations;

		public EventBuilder id(Long id) {
			this.id = id;
			return this;
		}

		public EventBuilder title(String title) {
			this.title = title;
			return this;
		}

		public EventBuilder description(String description) {
			this.description = description;
			return this;
		}

		public EventBuilder dateTime(LocalDateTime dateTime) {
			this.dateTime = dateTime;
			return this;
		}

		public EventBuilder location(String location) {
			this.location = location;
			return this;
		}

		public EventBuilder capacity(Integer capacity) {
			this.capacity = capacity;
			return this;
		}

		public EventBuilder registeredCount(Integer registeredCount) {
			this.registeredCount = registeredCount;
			return this;
		}

		public EventBuilder registrations(List<Registration> registrations) {
			this.registrations = registrations;
			return this;
		}

		public Event build() {
			return new Event(id, title, description, dateTime, location, capacity, registeredCount, registrations);
		}
	}
}
