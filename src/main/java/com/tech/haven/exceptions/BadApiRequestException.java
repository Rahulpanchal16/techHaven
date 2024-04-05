package com.tech.haven.exceptions;

public class BadApiRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadApiRequestException() {
		super("Bad Request");
	}

	public BadApiRequestException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
