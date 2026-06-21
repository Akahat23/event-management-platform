package com.event.platform.dto;

import jakarta.validation.constraints.*;

public class RegistrationRequest {

	@NotNull(message = "Event ID is required")
	private Long eventId;

	@NotBlank(message = "Attendee name is required")
	@Size(max = 100, message = "Attendee name must not exceed 100 characters")
	private String attendeeName;

	@NotBlank(message = "Attendee email is required")
	@Email(message = "Please provide a valid email address")
	private String attendeeEmail;

	public RegistrationRequest() {
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getAttendeeName() {
		return attendeeName;
	}

	public void setAttendeeName(String attendeeName) {
		this.attendeeName = attendeeName;
	}

	public String getAttendeeEmail() {
		return attendeeEmail;
	}

	public void setAttendeeEmail(String attendeeEmail) {
		this.attendeeEmail = attendeeEmail;
	}
}
