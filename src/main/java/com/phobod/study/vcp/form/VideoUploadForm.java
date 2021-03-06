package com.phobod.study.vcp.form;

import org.springframework.web.multipart.MultipartFile;

public class VideoUploadForm {
	private String title;
	private String description;
	private MultipartFile file;

	public VideoUploadForm() {
		super();
	}

	public VideoUploadForm(String title, String description, MultipartFile file) {
		super();
		this.title = title;
		this.description = description;
		this.file = file;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}
