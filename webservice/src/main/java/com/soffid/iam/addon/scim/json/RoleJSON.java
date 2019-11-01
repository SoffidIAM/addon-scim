package com.soffid.iam.addon.scim.json;

import java.util.ArrayList;
import java.util.List;

import org.apache.johnzon.mapper.JohnzonIgnore;

import com.soffid.iam.api.Role;

import es.caib.seycon.ng.comu.AccountAccessLevelEnum;

public class RoleJSON extends Role {
	private static final long serialVersionUID = 1L;
	MetaJSON meta = new MetaJSON();

	public RoleJSON() {
	}

	public RoleJSON(Role role) {
		super (role);
	}

	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}

	/**
	 * This field is private and it doesn't be managed in the SCIM REST request/responses
	 */
	@JohnzonIgnore private java.util.Collection<com.soffid.iam.api.Group> ownerGroups;

}