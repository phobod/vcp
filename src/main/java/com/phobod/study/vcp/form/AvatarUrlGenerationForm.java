package com.phobod.study.vcp.form;

public class AvatarUrlGenerationForm {
	private String email;
	private String url;

	private AvatarUrlGenerationForm() {
		super();
	}

	private AvatarUrlGenerationForm(String email, String url) {
		super();
		this.email = email;
		this.url = url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
