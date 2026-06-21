package com.event.platform.exception;

public class SeatLimitExceededException extends RuntimeException {
	public SeatLimitExceededException(String message) {
		super(message);
	}
}
