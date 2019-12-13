package com.soffid.iam.addon.scim.json;

import com.soffid.iam.api.Group;

public class GroupJSON extends Group {

	private static final long serialVersionUID = 4200340767670387678L;
	MetaJSON meta = new MetaJSON();

	public GroupJSON() {
		setAttributes(new HashMap<String,Object>());
	}

	public GroupJSON(Group group) {
		super(group);
	}

	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}
}
