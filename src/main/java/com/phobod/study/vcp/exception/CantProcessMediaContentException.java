package com.phobod.study.vcp.exception;

public class CantProcessMediaContentException extends ApplicationException{
	private static final long serialVersionUID = -3827593786540376025L;

	public CantProcessMediaContentException(String message) {
		super(message);
	}

	public CantProcessMediaContentException(Throwable cause) {
		super(cause);
	}

	public CantProcessMediaContentException(String message, Throwable cause) {
		super(message, cause);
	}

}
