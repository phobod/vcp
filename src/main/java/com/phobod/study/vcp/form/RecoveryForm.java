package com.phobod.study.vcp.form;

public class RecoveryForm {
	private String id;
	private String hash;
	private String password;
	
	private RecoveryForm() {
		super();
	}
	
	private RecoveryForm(String id, String hash, String password) {
		super();
		this.id = id;
		this.hash = hash;
		this.password = password;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
