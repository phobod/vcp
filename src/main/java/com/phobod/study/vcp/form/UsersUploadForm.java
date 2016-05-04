package com.phobod.study.vcp.form;

import javax.annotation.Nullable;

import org.springframework.web.multipart.MultipartFile;

import com.phobod.study.vcp.Constants.Role;

public class UsersUploadForm {
	private String name;
	private String surname;
	private String login;
	private String password;
	private String email;
	private String companyId;
	private String role;
	@Nullable
	private MultipartFile avatar;

	public UsersUploadForm() {
		super();
	}

	public UsersUploadForm(String name, String surname, String login, String password, String email, String companyId,
			String role, MultipartFile avatar) {
		super();
		this.name = name;
		this.surname = surname;
		this.login = login;
		this.password = password;
		this.email = email;
		this.companyId = companyId;
		this.role = role;
		this.avatar = avatar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public Role getRole() {
		return Role.valueOf(role);
	}

	public void setRole(String role) {
		this.role = role;
	}

	public MultipartFile getAvatar() {
		return avatar;
	}

	public void setAvatar(MultipartFile avatar) {
		this.avatar = avatar;
	}

	
}
