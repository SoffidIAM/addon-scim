package com.soffid.iam.addon.scim.json;

import java.util.List;

import com.soffid.iam.api.Account;

public class AccountJSON extends Account {

	private static final long serialVersionUID = 4544784110341469069L;
	MetaJSON meta = new MetaJSON();
	List<RoleDomainJSON> roles = null;
	String password = null;

	public AccountJSON() {}

	public AccountJSON(Account u) {
		super(u);
	}

	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}

	public List<RoleDomainJSON> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleDomainJSON> roles) {
		this.roles = roles;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}