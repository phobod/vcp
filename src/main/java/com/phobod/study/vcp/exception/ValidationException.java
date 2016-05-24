package com.phobod.study.vcp.exception;

public class ValidationException extends ApplicationException {
	private static final long serialVersionUID = 4312751543977669208L;

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(Throwable cause) {
		super(cause);
	}

}
