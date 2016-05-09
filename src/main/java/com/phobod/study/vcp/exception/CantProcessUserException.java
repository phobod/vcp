package com.phobod.study.vcp.exception;

public class CantProcessUserException extends ApplicationException{
	private static final long serialVersionUID = -491902158623648039L;

	public CantProcessUserException(String message, Throwable cause) {
		super(message, cause);
	}

	public CantProcessUserException(String message) {
		super(message);
	}

	public CantProcessUserException(Throwable cause) {
		super(cause);
	}

}
