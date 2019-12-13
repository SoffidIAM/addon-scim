package com.soffid.iam.addon.scim.json;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.apache.johnzon.mapper.JohnzonIgnore;

import com.soffid.iam.api.Role;

public class RoleJSON extends Role {
	private static final long serialVersionUID = 1L;
	MetaJSON meta = new MetaJSON();

	public RoleJSON() {
		setAttributes(new HashMap<String,Object>());
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

	@JohnzonIgnore private java.util.Collection<com.soffid.iam.api.Group> ownerGroups;

}
