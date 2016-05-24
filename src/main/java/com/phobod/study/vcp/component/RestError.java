package com.phobod.study.vcp.component;

public class RestError {
	private int status;
	private String message;
	private String description;

	public RestError() {
		super();
	}

	public RestError(int status, String message, String description) {
		super();
		this.status = status;
		this.message = message;
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
