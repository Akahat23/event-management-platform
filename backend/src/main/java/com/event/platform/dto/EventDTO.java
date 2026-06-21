package com.event.platform.dto;

import java.time.LocalDateTime;

public class EventDTO {
	private Long id;
	private String title;
	private String description;
	private LocalDateTime dateTime;
	private String location;
	private Integer capacity;
	private Integer registeredCount;

	public EventDTO() {
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
}
