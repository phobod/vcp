package com.phobod.study.vcp.exception;

public class ApplicationException extends RuntimeException{
	private static final long serialVersionUID = 7224178428118540495L;

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(Throwable cause) {
		super(cause);
	}

	
}
