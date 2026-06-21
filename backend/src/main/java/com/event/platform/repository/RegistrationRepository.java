package com.event.platform.repository;

import com.event.platform.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
	boolean existsByEventIdAndAttendeeId(Long eventId, Long attendeeId);
	List<Registration> findByEventId(Long eventId);
}
