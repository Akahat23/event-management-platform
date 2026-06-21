package com.event.platform.controller;

import com.event.platform.dto.EventDTO;
import com.event.platform.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

	private final EventService eventService;

	@Autowired
	public EventController(EventService eventService) {
		this.eventService = eventService;
	}

	@GetMapping
	public ResponseEntity<List<EventDTO>> getAllEvents() {
		List<EventDTO> events = eventService.getAllEvents();
		return ResponseEntity.ok(events);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
		EventDTO event = eventService.getEventById(id);
		return ResponseEntity.ok(event);
	}

	@PostMapping
	public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventDTO eventDTO) {
		EventDTO createdEvent = eventService.createEvent(eventDTO);
		return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @Valid @RequestBody EventDTO eventDTO) {
		EventDTO updatedEvent = eventService.updateEvent(id, eventDTO);
		return ResponseEntity.ok(updatedEvent);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
		eventService.deleteEvent(id);
		return ResponseEntity.noContent().build();
	}
}
