package com.event.platform.repository;

import com.event.platform.model.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
	Optional<Attendee> findByEmail(String email);
}
