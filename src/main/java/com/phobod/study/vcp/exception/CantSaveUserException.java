package com.phobod.study.vcp.exception;

public class CantSaveUserException extends ApplicationException{
	private static final long serialVersionUID = -491902158623648039L;

	public CantSaveUserException(String message, Throwable cause) {
		super(message, cause);
	}

	public CantSaveUserException(String message) {
		super(message);
	}

	public CantSaveUserException(Throwable cause) {
		super(cause);
	}

}
