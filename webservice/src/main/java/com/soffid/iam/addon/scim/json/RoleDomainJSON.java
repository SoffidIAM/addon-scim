package com.soffid.iam.addon.scim.json;

public class RoleDomainJSON {

	Long id = null;
	String roleName = null;
	String roleDescription = null;
	String informationSystemName = null;
	String domainValue = null;
	String userCode = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public String getInformationSystemName() {
		return informationSystemName;
	}

	public void setInformationSystemName(String informationSystemName) {
		this.informationSystemName = informationSystemName;
	}

	public String getDomainValue() {
		return domainValue;
	}

	public void setDomainValue(String domainValue) {
		this.domainValue = domainValue;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
}
