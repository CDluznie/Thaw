package fr.umlv.thaw.util.exception;

import java.util.Objects;

public class InvalidValueException extends Exception {

	private static final long serialVersionUID = -7800996613591262741L;

	/**
	 * Construct an invalid value exception.
	 * 
	 * @param 	message the exception message
	 * @throws 	NullPointerException if message is null
	 */
	public InvalidValueException(String message) {
		super(Objects.requireNonNull(message));
	}
	
}
