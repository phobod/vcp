package com.phobod.study.vcp.exception;

public class CantProcessPasswordRestoreException extends ApplicationException{
	private static final long serialVersionUID = -785639912561583607L;

	public CantProcessPasswordRestoreException(String message) {
		super(message);
	}

	public CantProcessPasswordRestoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public CantProcessPasswordRestoreException(Throwable cause) {
		super(cause);
	}

}
