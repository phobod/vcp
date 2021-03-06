package com.phobod.study.vcp.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.phobod.study.vcp.Constants.Role;

@Document
public class User implements Serializable {
	private static final long serialVersionUID = -6804101990746982838L;

	@Id
	private String id;
	private String name;
	private String surname;
	@Indexed(unique = true)
	private String login;
	private String password;
	@Indexed(unique = true)
	private String email;
	@DBRef
	private Company company;
	private Role role;
	@Field("avatar-url")
	private String avatarUrl;
	private String hash;

	public User() {
		super();
	}

	public User(String name, String surname, String login, String password, String email, Company company, Role role, String avatarUrl) {
		super();
		this.name = name;
		this.surname = surname;
		this.login = login;
		this.password = password;
		this.email = email;
		this.company = company;
		this.role = role;
		this.avatarUrl = avatarUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		return String.format("User [id=%s, name=%s, surname=%s, login=%s]", id, name, surname, login);
	}

}
