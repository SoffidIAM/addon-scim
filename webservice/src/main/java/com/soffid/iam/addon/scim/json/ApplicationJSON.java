package com.soffid.iam.addon.scim.json;

import com.soffid.iam.api.Application;

public class ApplicationJSON extends Application {
	private static final long serialVersionUID = 1L;
	MetaJSON meta = new MetaJSON();

	public ApplicationJSON() {
	}

	public ApplicationJSON(Application role) {
		super (role);
	}

	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}

}