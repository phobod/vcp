package com.phobod.study.vcp.exception;

public class CantProcessAccessRecoveryException extends ApplicationException{
	private static final long serialVersionUID = -785639912561583607L;

	public CantProcessAccessRecoveryException(String message) {
		super(message);
	}

	public CantProcessAccessRecoveryException(String message, Throwable cause) {
		super(message, cause);
	}

	public CantProcessAccessRecoveryException(Throwable cause) {
		super(cause);
	}

}
