package com.event.platform.service;

import com.event.platform.dto.EventDTO;
import com.event.platform.model.Event;
import com.event.platform.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {

	private final EventRepository eventRepository;

	@Autowired
	public EventService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@Transactional(readOnly = true)
	public List<EventDTO> getAllEvents() {
		return eventRepository.findAll().stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public EventDTO getEventById(Long id) {
		Event event = eventRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Event not found with ID: " + id));
		return convertToDTO(event);
	}

	public EventDTO createEvent(EventDTO eventDTO) {
		Event event = Event.builder()
				.title(eventDTO.getTitle())
				.description(eventDTO.getDescription())
				.dateTime(eventDTO.getDateTime())
				.location(eventDTO.getLocation())
				.capacity(eventDTO.getCapacity())
				.registeredCount(0)
				.build();

		Event savedEvent = eventRepository.save(event);
		return convertToDTO(savedEvent);
	}

	public EventDTO updateEvent(Long id, EventDTO eventDTO) {
		Event event = eventRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Event not found with ID: " + id));

		event.setTitle(eventDTO.getTitle());
		event.setDescription(eventDTO.getDescription());
		event.setDateTime(eventDTO.getDateTime());
		event.setLocation(eventDTO.getLocation());
		event.setCapacity(eventDTO.getCapacity());

		// Ensure capacity isn't set below registered count
		if (event.getCapacity() < event.getRegisteredCount()) {
			throw new RuntimeException("New capacity cannot be less than the current number of registered attendees (" + event.getRegisteredCount() + ")");
		}

		Event updatedEvent = eventRepository.save(event);
		return convertToDTO(updatedEvent);
	}

	public void deleteEvent(Long id) {
		if (!eventRepository.existsById(id)) {
			throw new RuntimeException("Event not found with ID: " + id);
		}
		eventRepository.deleteById(id);
	}

	public EventDTO convertToDTO(Event event) {
		EventDTO dto = new EventDTO();
		dto.setId(event.getId());
		dto.setTitle(event.getTitle());
		dto.setDescription(event.getDescription());
		dto.setDateTime(event.getDateTime());
		dto.setLocation(event.getLocation());
		dto.setCapacity(event.getCapacity());
		dto.setRegisteredCount(event.getRegisteredCount());
		return dto;
	}
}
