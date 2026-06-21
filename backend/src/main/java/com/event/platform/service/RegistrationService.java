package com.event.platform.service;

import com.event.platform.dto.RegistrationDTO;
import com.event.platform.dto.RegistrationRequest;
import com.event.platform.exception.DuplicateRegistrationException;
import com.event.platform.exception.SeatLimitExceededException;
import com.event.platform.model.Attendee;
import com.event.platform.model.Event;
import com.event.platform.model.Registration;
import com.event.platform.repository.AttendeeRepository;
import com.event.platform.repository.EventRepository;
import com.event.platform.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegistrationService {

	private final RegistrationRepository registrationRepository;
	private final EventRepository eventRepository;
	private final AttendeeRepository attendeeRepository;

	@Autowired
	public RegistrationService(
			RegistrationRepository registrationRepository,
			EventRepository eventRepository,
			AttendeeRepository attendeeRepository) {
		this.registrationRepository = registrationRepository;
		this.eventRepository = eventRepository;
		this.attendeeRepository = attendeeRepository;
	}

	public synchronized RegistrationDTO registerAttendee(RegistrationRequest request) {
		// 1. Fetch Event
		Event event = eventRepository.findById(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found with ID: " + request.getEventId()));

		// 2. Fetch or create Attendee by email
		Attendee attendee = attendeeRepository.findByEmail(request.getAttendeeEmail())
				.orElseGet(() -> {
					Attendee newAttendee = Attendee.builder()
							.name(request.getAttendeeName())
							.email(request.getAttendeeEmail())
							.build();
					return attendeeRepository.save(newAttendee);
				});

		// 3. Check for Duplicate Registration
		if (registrationRepository.existsByEventIdAndAttendeeId(event.getId(), attendee.getId())) {
			throw new DuplicateRegistrationException(
					"Attendee '" + attendee.getEmail() + "' is already registered for event ID: " + event.getId()
			);
		}

		// 4. Check Seat Limits (Capacity)
		if (event.getRegisteredCount() >= event.getCapacity()) {
			throw new SeatLimitExceededException(
					"Cannot register. Event '" + event.getTitle() + "' is fully booked (Capacity: " + event.getCapacity() + ")."
			);
		}

		// 5. Create and Save Registration
		Registration registration = Registration.builder()
				.event(event)
				.attendee(attendee)
				.registrationDate(LocalDateTime.now())
				.build();

		Registration savedRegistration = registrationRepository.save(registration);

		// 6. Update Event Registered Count
		event.setRegisteredCount(event.getRegisteredCount() + 1);
		eventRepository.save(event);

		return convertToDTO(savedRegistration);
	}

	@Transactional(readOnly = true)
	public List<RegistrationDTO> getRegistrationsByEvent(Long eventId) {
		return registrationRepository.findByEventId(eventId).stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<RegistrationDTO> getAllRegistrations() {
		return registrationRepository.findAll().stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	private RegistrationDTO convertToDTO(Registration reg) {
		RegistrationDTO dto = new RegistrationDTO();
		dto.setId(reg.getId());
		dto.setEventId(reg.getEvent().getId());
		dto.setEventTitle(reg.getEvent().getTitle());
		dto.setAttendeeId(reg.getAttendee().getId());
		dto.setAttendeeName(reg.getAttendee().getName());
		dto.setAttendeeEmail(reg.getAttendee().getEmail());
		dto.setRegistrationDate(reg.getRegistrationDate());
		return dto;
	}
}
