package com.soffid.iam.addon.scim.json;

import java.util.HashMap;

import com.soffid.iam.api.Domain;
import com.soffid.iam.api.Role;

public class RoleJSON {
	private static final long serialVersionUID = 1L;
	MetaJSON meta = new MetaJSON();
	/**
	 * Attribute name

	 */
	private java.lang.String name;

	/**
	 * Attribute description

	 */
	private java.lang.String description;

	/**
	 * Attribute category

	 */
	private java.lang.String category;

	/**
	 * Attribute enableByDefault

	 */
	private java.lang.Boolean enableByDefault;

	/**
	 * Attribute system

	 */
	private java.lang.String system;

	/**
	 * Attribute password

	 */
	private java.lang.Boolean password;

	/**
	 * Attribute informationSystemName

	 */
	private java.lang.String informationSystemName;

	/**
	 * Attribute id

	 */
	private java.lang.Long id;

	/**
	 * Attribute domain

	 */
	private com.soffid.iam.api.Domain domain;

	/**
	 * Attribute ownerRoles

	 */
	private java.util.Collection<com.soffid.iam.api.RoleGrant> ownerRoles;

	/**
	 * Attribute granteeGroups

	 */
	private java.util.Collection<com.soffid.iam.api.RoleGrant> granteeGroups;

	/**
	 * Attribute indirectAssignment

	 */
	private java.lang.String indirectAssignment;

	/**
	 * Attribute ownedRoles

	 */
	private java.util.Collection<com.soffid.iam.api.RoleGrant> ownedRoles;

	/**
	 * Attribute bpmEnforced

	 */
	private java.lang.Boolean bpmEnforced;

	/**
	 * Attribute approvalStart
 * Last modification date

	 */
	private java.util.Date approvalStart;

	/**
	 * Attribute approvalEnd
 * Approval date

	 */
	private java.util.Date approvalEnd;

	/**
	 * Attribute attributes
 * Role custom attributes

	 */
	private java.util.Map<java.lang.String,java.lang.Object> attributes = new java.util.HashMap<String,Object>();

	public RoleJSON() {
		setAttributes(new HashMap<String,Object>());
	}

	public RoleJSON(Role role) {
		super ();
		approvalEnd = role.getApprovalEnd();
		approvalStart = role.getApprovalStart();
		attributes = role.getAttributes();
		bpmEnforced = role.getBpmEnforced();
		category = role.getCategory();
		description = role.getDescription();
		domain = new Domain();
		if (role.getDomain()==null)
			domain.setName("SENSE_DOMINI");
		else
			domain.setName(role.getDomain());
		domain.setExternalCode(role.getInformationSystemName());
		enableByDefault = role.getEnableByDefault();
		granteeGroups = role.getGranteeGroups();
		id = role.getId();
		indirectAssignment = null;
		informationSystemName = role.getInformationSystemName();
		name = role.getName();
		ownedRoles = role.getOwnedRoles();
		ownerRoles = role.getOwnerRoles();
		password = role.getPassword();
		system = role.getSystem();
	}

	public Role toRole() {
		Role r = new Role();
		r.setApprovalEnd(approvalEnd);
		r.setApprovalStart(approvalStart);
		r.setAttributes(attributes);
		r.setBpmEnforced(bpmEnforced);
		r.setCategory(category);
		r.setDescription(description);
		r.setDomain(domain==null || "SENSE_DOMINI".equals(domain.getName()) ? null : domain.getName());
		r.setEnableByDefault(enableByDefault);
		r.setGranteeGroups(granteeGroups);
		r.setId(id);
		r.setInformationSystemName(informationSystemName);
		r.setName(name);
		r.setOwnedRoles(ownedRoles);
		r.setOwnerRoles(ownerRoles);
		r.setPassword(password);
		r.setSystem(system);
		return r;
	}
	
	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	public java.lang.String getCategory() {
		return category;
	}

	public void setCategory(java.lang.String category) {
		this.category = category;
	}

	public java.lang.Boolean getEnableByDefault() {
		return enableByDefault;
	}

	public void setEnableByDefault(java.lang.Boolean enableByDefault) {
		this.enableByDefault = enableByDefault;
	}

	public java.lang.String getSystem() {
		return system;
	}

	public void setSystem(java.lang.String system) {
		this.system = system;
	}

	public java.lang.Boolean getPassword() {
		return password;
	}

	public void setPassword(java.lang.Boolean password) {
		this.password = password;
	}

	public java.lang.String getInformationSystemName() {
		return informationSystemName;
	}

	public void setInformationSystemName(java.lang.String informationSystemName) {
		this.informationSystemName = informationSystemName;
	}

	public java.lang.Long getId() {
		return id;
	}

	public void setId(java.lang.Long id) {
		this.id = id;
	}

	public com.soffid.iam.api.Domain getDomain() {
		return domain;
	}

	public void setDomain(com.soffid.iam.api.Domain domain) {
		this.domain = domain;
	}

	public java.util.Collection<com.soffid.iam.api.RoleGrant> getOwnerRoles() {
		return ownerRoles;
	}

	public void setOwnerRoles(java.util.Collection<com.soffid.iam.api.RoleGrant> ownerRoles) {
		this.ownerRoles = ownerRoles;
	}

	public java.util.Collection<com.soffid.iam.api.RoleGrant> getGranteeGroups() {
		return granteeGroups;
	}

	public void setGranteeGroups(java.util.Collection<com.soffid.iam.api.RoleGrant> granteeGroups) {
		this.granteeGroups = granteeGroups;
	}

	public java.lang.String getIndirectAssignment() {
		return indirectAssignment;
	}

	public void setIndirectAssignment(java.lang.String indirectAssignment) {
		this.indirectAssignment = indirectAssignment;
	}

	public java.util.Collection<com.soffid.iam.api.RoleGrant> getOwnedRoles() {
		return ownedRoles;
	}

	public void setOwnedRoles(java.util.Collection<com.soffid.iam.api.RoleGrant> ownedRoles) {
		this.ownedRoles = ownedRoles;
	}

	public java.lang.Boolean getBpmEnforced() {
		return bpmEnforced;
	}

	public void setBpmEnforced(java.lang.Boolean bpmEnforced) {
		this.bpmEnforced = bpmEnforced;
	}

	public java.util.Date getApprovalStart() {
		return approvalStart;
	}

	public void setApprovalStart(java.util.Date approvalStart) {
		this.approvalStart = approvalStart;
	}

	public java.util.Date getApprovalEnd() {
		return approvalEnd;
	}

	public void setApprovalEnd(java.util.Date approvalEnd) {
		this.approvalEnd = approvalEnd;
	}

	public java.util.Map<java.lang.String, java.lang.Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(java.util.Map<java.lang.String, java.lang.Object> attributes) {
		this.attributes = attributes;
	}

}
