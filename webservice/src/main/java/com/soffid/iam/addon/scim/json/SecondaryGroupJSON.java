package com.soffid.iam.addon.scim.json;

import com.soffid.iam.api.GroupUser;

public class SecondaryGroupJSON {

	private Long id = null;
	private String group = null;
	private String groupDescription = null;

	public SecondaryGroupJSON(GroupUser secondaryGroup) {
		this.id = secondaryGroup.getId();
		this.group = secondaryGroup.getGroup();
		this.groupDescription = secondaryGroup.getGroupDescription();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}
}
