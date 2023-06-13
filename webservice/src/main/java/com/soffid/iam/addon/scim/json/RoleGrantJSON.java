package com.soffid.iam.addon.scim.json;

import java.util.Date;

import org.apache.johnzon.mapper.JohnzonIgnore;

import com.soffid.iam.api.RoleDependencyStatus;
import com.soffid.iam.api.RoleGrant;

public class RoleGrantJSON extends RoleGrant {
	@JohnzonIgnore
	public com.soffid.iam.api.RoleDependencyStatus getStatus() {
		return super.getStatus();
	}

	public RoleGrantJSON() {
		super();
	}

	public RoleGrantJSON(Long id, Long roleId, String roleName, String roleDescription, String system,
			String informationSystem, boolean hasDomain, String domainValue, String ownerAccountName,
			String ownerSystem, String ownerGroup, Long ownerRole, String ownerRolDomainValue, String ownerRoleName,
			String ownerRoleDescription, String user, Date startDate, Date endDate, boolean enabled, String holderGroup,
			RoleDependencyStatus status, Boolean mandatory) {
		setId( id );
		setRoleId( roleId );
		setRoleName( roleName );
		setRoleDescription( roleDescription );
		setSystem( system );
		setInformationSystem( informationSystem );
		setHasDomain( hasDomain );
		setDomainValue( domainValue );
		setOwnerAccountName( ownerAccountName );
		setOwnerSystem( ownerSystem );
		setOwnerGroup( ownerGroup );
		setOwnerRole( ownerRole );
		setOwnerRolDomainValue( ownerRolDomainValue );
		setOwnerRoleName( ownerRoleName );
		setOwnerRoleDescription( ownerRoleDescription );
		setUser( user );
		setStartDate( startDate );
		setEndDate( endDate );
		setHolderGroup( holderGroup );
		setStatus( status );
		setMandatory( mandatory );
	}

	public RoleGrantJSON(Long roleId, String roleName, String system, boolean hasDomain, boolean enabled) {
		super(roleId, roleName, system, hasDomain, enabled);
	}

	public RoleGrantJSON(RoleGrant otherBean) {
		super(otherBean);
	}

}
