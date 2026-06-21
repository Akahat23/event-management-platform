package com.event.platform.controller;

import com.event.platform.dto.AttendeeDTO;
import com.event.platform.model.Attendee;
import com.event.platform.repository.AttendeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendees")
@CrossOrigin(origins = "*")
public class AttendeeController {

	private final AttendeeRepository attendeeRepository;

	@Autowired
	public AttendeeController(AttendeeRepository attendeeRepository) {
		this.attendeeRepository = attendeeRepository;
	}

	@GetMapping
	public ResponseEntity<List<AttendeeDTO>> getAllAttendees() {
		List<AttendeeDTO> attendees = attendeeRepository.findAll().stream()
				.map(attendee -> {
					AttendeeDTO dto = new AttendeeDTO();
					dto.setId(attendee.getId());
					dto.setName(attendee.getName());
					dto.setEmail(attendee.getEmail());
					return dto;
				})
				.collect(Collectors.toList());
		return ResponseEntity.ok(attendees);
	}
}
