package com.soffid.iam.addon.scim.json;

public class PasswordJSON {

	private String userName = null;
	private String password = null;
	private Boolean mustChange = null;

	public PasswordJSON() {
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getMustChange() {
		return mustChange;
	}

	public void setMustChange(Boolean mustChange) {
		this.mustChange = mustChange;
	}
}
