package com.event.platform.controller;

import com.event.platform.dto.RegistrationDTO;
import com.event.platform.dto.RegistrationRequest;
import com.event.platform.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {

	private final RegistrationService registrationService;

	@Autowired
	public RegistrationController(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@PostMapping
	public ResponseEntity<RegistrationDTO> registerAttendee(@Valid @RequestBody RegistrationRequest request) {
		RegistrationDTO registration = registrationService.registerAttendee(request);
		return new ResponseEntity<>(registration, HttpStatus.CREATED);
	}

	@GetMapping("/event/{eventId}")
	public ResponseEntity<List<RegistrationDTO>> getRegistrationsByEvent(@PathVariable Long eventId) {
		List<RegistrationDTO> registrations = registrationService.getRegistrationsByEvent(eventId);
		return ResponseEntity.ok(registrations);
	}

	@GetMapping
	public ResponseEntity<List<RegistrationDTO>> getAllRegistrations() {
		List<RegistrationDTO> registrations = registrationService.getAllRegistrations();
		return ResponseEntity.ok(registrations);
	}
}
