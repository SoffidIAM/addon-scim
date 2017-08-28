package com.soffid.iam.addon.scim.json;

import java.util.ArrayList;
import java.util.List;

import org.apache.johnzon.mapper.JohnzonIgnore;

import com.soffid.iam.api.Account;

import es.caib.seycon.ng.comu.AccountAccessLevelEnum;

public class AccountJSON extends Account {

	private static final long serialVersionUID = 4544784110341469069L;
	MetaJSON meta = new MetaJSON();
	List<RoleDomainJSON> roles = new ArrayList<RoleDomainJSON>();
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

	/**
	 * This field is private and it doesn't be managed in the SCIM REST request/responses
	 */
	@JohnzonIgnore private AccountAccessLevelEnum accessLevel;
}