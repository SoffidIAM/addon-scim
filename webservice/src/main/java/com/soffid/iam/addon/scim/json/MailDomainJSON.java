package com.soffid.iam.addon.scim.json;

import com.soffid.iam.api.MailDomain;

public class MailDomainJSON extends MailDomain {

	private static final long serialVersionUID = 1L;
	MetaJSON meta = new MetaJSON();

	public MailDomainJSON() {
	}

	public MailDomainJSON(MailDomain mailDomain) {
		super (mailDomain);
	}

	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}
}
