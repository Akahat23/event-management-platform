package com.event.platform.exception;

public class DuplicateRegistrationException extends RuntimeException {
	public DuplicateRegistrationException(String message) {
		super(message);
	}
}
