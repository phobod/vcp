package com.phobod.study.vcp.exception;

public class CantProcessStatisticsException extends ApplicationException{
	private static final long serialVersionUID = -2805047660947122072L;

	public CantProcessStatisticsException(String message, Throwable cause) {
		super(message, cause);
	}

	public CantProcessStatisticsException(String message) {
		super(message);
	}

	public CantProcessStatisticsException(Throwable cause) {
		super(cause);
	}

}
