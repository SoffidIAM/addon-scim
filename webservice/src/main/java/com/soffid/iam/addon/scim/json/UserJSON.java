package com.soffid.iam.addon.scim.json;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.johnzon.mapper.JohnzonIgnore;

import com.soffid.iam.api.User;

public class UserJSON extends User {

	private static final long serialVersionUID = 7014635707957641008L;
	MetaJSON meta = new MetaJSON();
	List<UserAccountJSON> accounts = new LinkedList<UserAccountJSON>();
	Map<String, Object> attributes = new HashMap<String, Object>();
	List<SecondaryGroupJSON> secondaryGroups = new LinkedList<SecondaryGroupJSON>();
	String password = null;

	public UserJSON() {}

	public UserJSON(User u) {
		super(u);
	}

	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}

	public List<UserAccountJSON> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<UserAccountJSON> accounts) {
		this.accounts = accounts;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public List<SecondaryGroupJSON> getSecondaryGroups() {
		return secondaryGroups;
	}

	public void setSecondaryGroups(List<SecondaryGroupJSON> secondaryGroups) {
		this.secondaryGroups = secondaryGroups;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * This field is deprecated and it doesn't be managed in the SCIM REST
	 * request/responses
	 */
	@JohnzonIgnore private Long passwordMaxAge;
}