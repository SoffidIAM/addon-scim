package com.soffid.iam.addon.scim.json;

import com.soffid.iam.addon.scim.api1.MailList;

public class MailListJSON extends com.soffid.iam.addon.scim.api1.MailList {

	private static final long serialVersionUID = 1L;
	MetaJSON meta = new MetaJSON();

	public MailListJSON() {
	}

	public MailListJSON(MailList mailList) {
		super (mailList);
	}

	public MailListJSON(com.soffid.iam.api.MailList mailList) {
		super (mailList);
	}


	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}
}
