package com.soffid.iam.addon.scim.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.apache.johnzon.mapper.JohnzonAny;
import org.apache.johnzon.mapper.JohnzonIgnore;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.AccountStatus;
import com.soffid.iam.api.Group;
import com.soffid.iam.api.Role;
import com.soffid.iam.api.User;

import es.caib.seycon.ng.comu.AccountAccessLevelEnum;
import es.caib.seycon.ng.exception.InternalErrorException;

public class AccountJSON {

	private static final long serialVersionUID = 4544784110341469069L;
	MetaJSON meta = new MetaJSON();
	List<RoleDomainJSON> roles = null;
	List<RoleDomainJSON> inheritedRoles = new ArrayList<RoleDomainJSON>();
	String password = null;
	private java.lang.Long id;
	private java.lang.String name;
	private java.lang.String oldName;
	private java.lang.String description;
	private es.caib.seycon.ng.comu.AccountType type;
	private java.util.Collection<com.soffid.iam.api.Group> grantedGroups;
	private java.util.Collection<com.soffid.iam.api.User> grantedUsers;
	private java.util.Collection<com.soffid.iam.api.Role> grantedRoles;
	private java.util.Collection<com.soffid.iam.api.Group> managerGroups;
	private java.util.Collection<com.soffid.iam.api.User> managerUsers;
	private java.util.Collection<com.soffid.iam.api.Role> managerRoles;
	private java.util.Collection<com.soffid.iam.api.Group> ownerGroups;
	private java.util.Collection<com.soffid.iam.api.User> ownerUsers;
	private java.util.Collection<com.soffid.iam.api.Role> ownerRoles;
	private java.lang.String system;
	private java.util.Date created;
	private java.util.Calendar lastLogin;
	private java.util.Calendar lastUpdated;
	private java.util.Calendar lastPasswordSet;
	private java.util.Calendar passwordExpiration;
	private boolean disabled = false;
	private com.soffid.iam.api.AccountStatus status;
	private java.lang.String passwordPolicy;
	private java.util.Map<java.lang.String,java.lang.Object> attributes;
	@JohnzonIgnore private AccountAccessLevelEnum accessLevel;
	private java.lang.Long vaultFolderId;
	private java.lang.String vaultFolder;
	private boolean inheritNewPermissions = false;
	private java.lang.String loginUrl;
	private java.lang.String loginName;
	private com.soffid.iam.api.LaunchType launchType;
	private java.lang.String jumpServerGroup;
	private com.soffid.iam.api.PasswordValidation passwordStatus;
	
	public AccountJSON() {
		attributes = new HashMap<String,Object>();
		setRoles(null);
	}

	public AccountJSON(java.lang.Long id, java.lang.String name, java.lang.String oldName, java.lang.String description, es.caib.seycon.ng.comu.AccountType type, java.util.Collection<com.soffid.iam.api.Group> grantedGroups, java.util.Collection<com.soffid.iam.api.User> grantedUsers, java.util.Collection<com.soffid.iam.api.Role> grantedRoles, java.util.Collection<com.soffid.iam.api.Group> managerGroups, java.util.Collection<com.soffid.iam.api.User> managerUsers, java.util.Collection<com.soffid.iam.api.Role> managerRoles, java.util.Collection<com.soffid.iam.api.Group> ownerGroups, java.util.Collection<com.soffid.iam.api.User> ownerUsers, java.util.Collection<com.soffid.iam.api.Role> ownerRoles, java.lang.String system, java.util.Date created, java.util.Calendar lastLogin, java.util.Calendar lastUpdated, java.util.Calendar lastPasswordSet, java.util.Calendar passwordExpiration, boolean disabled, com.soffid.iam.api.AccountStatus status, java.lang.String passwordPolicy, java.util.Map<java.lang.String,java.lang.Object> attributes, es.caib.seycon.ng.comu.AccountAccessLevelEnum accessLevel, java.lang.Long vaultFolderId, java.lang.String vaultFolder, boolean inheritNewPermissions, java.lang.String loginUrl, java.lang.String loginName, com.soffid.iam.api.LaunchType launchType, java.lang.String jumpServerGroup, com.soffid.iam.api.PasswordValidation passwordStatus)
	{
		super();
		this.id = id;
		this.name = name;
		this.oldName = oldName;
		this.description = description;
		this.type = type;
		this.grantedGroups = grantedGroups;
		this.grantedUsers = grantedUsers;
		this.grantedRoles = grantedRoles;
		this.managerGroups = managerGroups;
		this.managerUsers = managerUsers;
		this.managerRoles = managerRoles;
		this.ownerGroups = ownerGroups;
		this.ownerUsers = ownerUsers;
		this.ownerRoles = ownerRoles;
		this.system = system;
		this.created = created;
		this.lastLogin = lastLogin;
		this.lastUpdated = lastUpdated;
		this.lastPasswordSet = lastPasswordSet;
		this.passwordExpiration = passwordExpiration;
		this.disabled = disabled;
		this.status = status;
		this.passwordPolicy = passwordPolicy;
		this.attributes = attributes;
		this.accessLevel = accessLevel;
		this.vaultFolderId = vaultFolderId;
		this.vaultFolder = vaultFolder;
		this.inheritNewPermissions = inheritNewPermissions;
		this.loginUrl = loginUrl;
		this.loginName = loginName;
		this.launchType = launchType;
		this.jumpServerGroup = jumpServerGroup;
		this.passwordStatus = passwordStatus;
	}

	public AccountJSON(Account otherBean) {
		this(otherBean.getId(), 
				otherBean.getName(), 
				otherBean.getOldName(), 
				otherBean.getDescription(), 
				otherBean.getType(),
				fetchGroups(otherBean.getGrantedGroups()), 
				fetchUsers(otherBean.getGrantedUsers()), 
				fetchRoles(otherBean.getGrantedRoles()), 
				fetchGroups(otherBean.getManagerGroups()), 
				fetchUsers(otherBean.getManagerUsers()), 
				fetchRoles(otherBean.getManagerRoles()), 
				fetchGroups(otherBean.getOwnerGroups()), 
				fetchUsers(otherBean.getOwnerUsers()), 
				fetchRoles(otherBean.getOwnerRoles()), 
				otherBean.getSystem(), 
				otherBean.getCreated(), 
				otherBean.getLastLogin(), 
				otherBean.getLastUpdated(), 
				otherBean.getLastPasswordSet(), 
				otherBean.getPasswordExpiration(), 
				otherBean.isDisabled(), 
				otherBean.getStatus(), 
				otherBean.getPasswordPolicy(), 
				otherBean.getAttributes(), 
				otherBean.getAccessLevel(), 
				otherBean.getVaultFolderId(), 
				otherBean.getVaultFolder(), 
				otherBean.isInheritNewPermissions(), 
				otherBean.getLoginUrl(), 
				otherBean.getLoginName(), 
				otherBean.getLaunchType(), 
				otherBean.getJumpServerGroup(), 
				otherBean.getPasswordStatus());
	}

	private static Collection<Group> fetchGroups(Collection<String> ownerGroups2) {
		Collection<Group> l = null;
		if (ownerGroups2 != null) {
			l = new LinkedList<>();
			for (String g: ownerGroups2) {
				try {
					l.add( EJBLocator.getGroupService().findGroupByGroupName(g) );
				} catch (InternalErrorException | NamingException | CreateException e) {
				}
			}
		}
		return l;
	}

	private static Collection<User> fetchUsers(Collection<String> ownerUsers2) {
		Collection<User> l = null;
		if (ownerUsers2 != null) {
			l = new LinkedList<>();
			for (String g: ownerUsers2) {
				try {
					l.add( EJBLocator.getUserService().findUserByUserName(g) );
				} catch (InternalErrorException | NamingException | CreateException e) {
				}
			}
		}
		return l;
	}

	private static Collection<Role> fetchRoles(Collection<String> ownerRoles2) {
		Collection<Role> l = null;
		if (ownerRoles2 != null) {
			l = new LinkedList<>();
			for (String g: ownerRoles2) {
				try {
					l.add( EJBLocator.getApplicationService().findRoleByShortName(g) );
				} catch (InternalErrorException | NamingException | CreateException e) {
				}
			}
		}
		return l;
	}

	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}

	public List<RoleDomainJSON> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleDomainJSON> roles) {
		this.roles = roles;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * This field is private and it doesn't be managed in the SCIM REST request/responses
	 */

	public List<RoleDomainJSON> getInheritedRoles() {
		return inheritedRoles;
	}

	public void setInheritedRoles(List<RoleDomainJSON> inheritedRoles) {
		this.inheritedRoles = inheritedRoles;
	}

	public java.lang.Long getId() {
		return id;
	}

	public void setId(java.lang.Long id) {
		this.id = id;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getOldName() {
		return oldName;
	}

	public void setOldName(java.lang.String oldName) {
		this.oldName = oldName;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	public es.caib.seycon.ng.comu.AccountType getType() {
		return type;
	}

	public void setType(es.caib.seycon.ng.comu.AccountType type) {
		this.type = type;
	}

	public java.util.Collection<com.soffid.iam.api.Group> getGrantedGroups() {
		return grantedGroups;
	}

	public void setGrantedGroups(java.util.Collection<com.soffid.iam.api.Group> grantedGroups) {
		this.grantedGroups = grantedGroups;
	}

	public java.util.Collection<com.soffid.iam.api.User> getGrantedUsers() {
		return grantedUsers;
	}

	public void setGrantedUsers(java.util.Collection<com.soffid.iam.api.User> grantedUsers) {
		this.grantedUsers = grantedUsers;
	}

	public java.util.Collection<com.soffid.iam.api.Role> getGrantedRoles() {
		return grantedRoles;
	}

	public void setGrantedRoles(java.util.Collection<com.soffid.iam.api.Role> grantedRoles) {
		this.grantedRoles = grantedRoles;
	}

	public java.util.Collection<com.soffid.iam.api.Group> getManagerGroups() {
		return managerGroups;
	}

	public void setManagerGroups(java.util.Collection<com.soffid.iam.api.Group> managerGroups) {
		this.managerGroups = managerGroups;
	}

	public java.util.Collection<com.soffid.iam.api.User> getManagerUsers() {
		return managerUsers;
	}

	public void setManagerUsers(java.util.Collection<com.soffid.iam.api.User> managerUsers) {
		this.managerUsers = managerUsers;
	}

	public java.util.Collection<com.soffid.iam.api.Role> getManagerRoles() {
		return managerRoles;
	}

	public void setManagerRoles(java.util.Collection<com.soffid.iam.api.Role> managerRoles) {
		this.managerRoles = managerRoles;
	}

	public java.util.Collection<com.soffid.iam.api.Group> getOwnerGroups() {
		return ownerGroups;
	}

	public void setOwnerGroups(java.util.Collection<com.soffid.iam.api.Group> ownerGroups) {
		this.ownerGroups = ownerGroups;
	}

	public java.util.Collection<com.soffid.iam.api.User> getOwnerUsers() {
		return ownerUsers;
	}

	public void setOwnerUsers(java.util.Collection<com.soffid.iam.api.User> ownerUsers) {
		this.ownerUsers = ownerUsers;
	}

	public java.util.Collection<com.soffid.iam.api.Role> getOwnerRoles() {
		return ownerRoles;
	}

	public void setOwnerRoles(java.util.Collection<com.soffid.iam.api.Role> ownerRoles) {
		this.ownerRoles = ownerRoles;
	}

	public java.lang.String getSystem() {
		return system;
	}

	public void setSystem(java.lang.String system) {
		this.system = system;
	}

	public java.util.Date getCreated() {
		return created;
	}

	public void setCreated(java.util.Date created) {
		this.created = created;
	}

	public java.util.Calendar getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(java.util.Calendar lastLogin) {
		this.lastLogin = lastLogin;
	}

	public java.util.Calendar getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(java.util.Calendar lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public java.util.Calendar getLastPasswordSet() {
		return lastPasswordSet;
	}

	public void setLastPasswordSet(java.util.Calendar lastPasswordSet) {
		this.lastPasswordSet = lastPasswordSet;
	}

	public java.util.Calendar getPasswordExpiration() {
		return passwordExpiration;
	}

	public void setPasswordExpiration(java.util.Calendar passwordExpiration) {
		this.passwordExpiration = passwordExpiration;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public com.soffid.iam.api.AccountStatus getStatus() {
		return status;
	}

	public void setStatus(com.soffid.iam.api.AccountStatus status) {
		this.status = status;
	}

	public java.lang.String getPasswordPolicy() {
		return passwordPolicy;
	}

	public void setPasswordPolicy(java.lang.String passwordPolicy) {
		this.passwordPolicy = passwordPolicy;
	}

	public java.util.Map<java.lang.String, java.lang.Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(java.util.Map<java.lang.String, java.lang.Object> attributes) {
		this.attributes = attributes;
	}

	public AccountAccessLevelEnum getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(AccountAccessLevelEnum accessLevel) {
		this.accessLevel = accessLevel;
	}

	public java.lang.Long getVaultFolderId() {
		return vaultFolderId;
	}

	public void setVaultFolderId(java.lang.Long vaultFolderId) {
		this.vaultFolderId = vaultFolderId;
	}

	public java.lang.String getVaultFolder() {
		return vaultFolder;
	}

	public void setVaultFolder(java.lang.String vaultFolder) {
		this.vaultFolder = vaultFolder;
	}

	public boolean isInheritNewPermissions() {
		return inheritNewPermissions;
	}

	public void setInheritNewPermissions(boolean inheritNewPermissions) {
		this.inheritNewPermissions = inheritNewPermissions;
	}

	public java.lang.String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(java.lang.String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public java.lang.String getLoginName() {
		return loginName;
	}

	public void setLoginName(java.lang.String loginName) {
		this.loginName = loginName;
	}

	public com.soffid.iam.api.LaunchType getLaunchType() {
		return launchType;
	}

	public void setLaunchType(com.soffid.iam.api.LaunchType launchType) {
		this.launchType = launchType;
	}

	public java.lang.String getJumpServerGroup() {
		return jumpServerGroup;
	}

	public void setJumpServerGroup(java.lang.String jumpServerGroup) {
		this.jumpServerGroup = jumpServerGroup;
	}

	public com.soffid.iam.api.PasswordValidation getPasswordStatus() {
		return passwordStatus;
	}

	public void setPasswordStatus(com.soffid.iam.api.PasswordValidation passwordStatus) {
		this.passwordStatus = passwordStatus;
	}

	public Account toAccount() {
		Account acc = new Account();
		acc.setId(id);
		acc.setName(name);
		acc.setOldName(oldName);
		acc.setDescription(description);
		acc.setType(type);
		acc.setGrantedGroups(groupsToStringList(grantedGroups));
		acc.setGrantedRoles(rolesToStringList(grantedRoles));
		acc.setGrantedUsers(usersToStringList(grantedUsers));
		acc.setManagerGroups(groupsToStringList(managerGroups));
		acc.setManagerRoles(rolesToStringList(managerRoles));
		acc.setManagerUsers(usersToStringList(managerUsers));
		acc.setOwnerGroups(groupsToStringList(ownerGroups));
		acc.setOwnerRoles(rolesToStringList(ownerRoles));
		acc.setOwnerUsers(usersToStringList(ownerUsers));
		acc.setSystem(system);
		acc.setCreated(created);
		acc.setLastLogin(lastLogin);
		acc.setLastUpdated(lastUpdated);
		acc.setPasswordExpiration(passwordExpiration);
		acc.setDisabled(disabled);
		acc.setStatus(status);
		acc.setPasswordPolicy(passwordPolicy);
		acc.setAttributes(attributes);
		acc.setAccessLevel(accessLevel);
		acc.setVaultFolder(vaultFolder);
		acc.setVaultFolderId(vaultFolderId);
		acc.setInheritNewPermissions(inheritNewPermissions);
		acc.setLoginUrl(loginUrl);
		acc.setLoginName(loginName);
		acc.setLaunchType(launchType);
		acc.setJumpServerGroup(jumpServerGroup);
		acc.setPasswordStatus(passwordStatus);
		return acc;
	}

	private Collection<String> usersToStringList(Collection<User> grantedUsers2) {
		Collection<String> l = null;
		if (grantedUsers2 != null) {
			l = new LinkedList<>();
			for (User u: grantedUsers2) {
				l.add(u.getUserName());
			}
		}
		return l;
	}

	private Collection<String> rolesToStringList(Collection<Role> grantedRoles2) {
		Collection<String> l = null;
		if (grantedRoles2 != null) {
			l = new LinkedList<>();
			for (Role u: grantedRoles2) {
				l.add(u.getName()+"@"+u.getSystem());
			}
		}
		return l;
	}

	private Collection<String> groupsToStringList(Collection<Group> grantedGroups2) {
		Collection<String> l = null;
		if (grantedGroups2 != null) {
			l = new LinkedList<>();
			for (Group g: grantedGroups2) {
				l.add(g.getName());
			}
		}
		return l;
	}
	
}
