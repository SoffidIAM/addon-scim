package com.soffid.iam.addon.scim.json;

import com.soffid.iam.api.MailList;

public class MailListJSON extends MailList {

	private static final long serialVersionUID = 1L;
	MetaJSON meta = new MetaJSON();

	public MailListJSON() {
	}

	public MailListJSON(MailList mailList) {
		super (mailList);
	}

	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}
}
