package com.soffid.iam.addon.scim.json;

import com.soffid.iam.api.Host;

public class HostJSON extends Host {

	private static final long serialVersionUID = 1L;
	MetaJSON meta = new MetaJSON();

	public HostJSON() {
	}

	public HostJSON(Host host) {
		super (host);
	}

	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}
}
