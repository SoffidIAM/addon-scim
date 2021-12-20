package com.soffid.iam.addon.scim.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.LogFactory;

import com.soffid.iam.addon.scim.json.AccountJSON;
import com.soffid.iam.addon.scim.json.MetaJSON;
import com.soffid.iam.addon.scim.json.RoleDomainJSON;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.response.SCIMResponseList;
import com.soffid.iam.addon.scim.util.PATCHAnnotation;
import com.soffid.iam.addon.scim.util.PaginationUtil;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.AccountStatus;
import com.soffid.iam.api.DomainValue;
import com.soffid.iam.api.PagedResult;
import com.soffid.iam.api.Role;
import com.soffid.iam.api.RoleAccount;
import com.soffid.iam.api.RoleGrant;
import com.soffid.iam.api.UserData;
import com.soffid.iam.service.ejb.AccountService;
import com.soffid.iam.service.ejb.ApplicationService;
import com.soffid.iam.service.ejb.DispatcherService;
import com.soffid.iam.service.ejb.UserService;

import es.caib.seycon.ng.comu.AccountType;
import es.caib.seycon.ng.exception.AccountAlreadyExistsException;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.exception.NeedsAccountNameException;

@Path("/scim/Account")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class AccountREST {
	org.apache.commons.logging.Log log = LogFactory.getLog(getClass());
	
	static final String RESOURCE = "Account";
	@EJB AccountService accountService;
	@EJB UserService userService;
	@EJB ApplicationService applicationService;
	@EJB DispatcherService dispatcherService;

	@Path("")
	@GET
	public Response list(@QueryParam("filter") @DefaultValue("") String filter,
			@QueryParam("attributes") @DefaultValue("") String attributes, @QueryParam("attributes") String atts,
			@QueryParam("startIndex") @DefaultValue("") String startIndex, @QueryParam("count") @DefaultValue("") String count)
			throws InternalErrorException {

		PaginationUtil p = new PaginationUtil(startIndex, count);
		if (p.getCount() <= 0 || p.getCount() > 1000) p.setCount(1000);
		PagedResult r = accountService.findAccountByJsonQuery(filter, p.getStartIndex(), p.getCount());
		return SCIMResponseBuilder.responseList(new SCIMResponseList(toExtendedAccountList(r.getResources(), attributes), r));
	}

	@Path("")
	@POST
	public Response create(AccountJSON account, @Context HttpServletRequest request) throws URISyntaxException {
		try {
			//check first if the account exits
			Account existingAccount = accountService.findAccount(account.getName(), account.getSystem());
			if (existingAccount!=null)
				return SCIMResponseBuilder.errorCustom(Status.INTERNAL_SERVER_ERROR, "AccountSvc.accountExits");
			// now create the account
			Account newAccount = accountService.createAccount(account.toAccount());
			if (newAccount != null) {
				updateAttributes(account, newAccount, true);
				updateRoles(account, newAccount);
				AccountJSON ea = toExtendedAccount(newAccount, null);
				return SCIMResponseBuilder.responseOk(ea, new URI(ea.getMeta().getLocation()));
			} else
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@GET
	public Response show(@QueryParam("attributes") @DefaultValue("") String attributes, 
			@PathParam("id") long id) {
		Account user;
		try {
			user = accountService.findAccountById(id);
			if (user != null)
				return SCIMResponseBuilder.responseOk(toExtendedAccount(user, attributes));
			else
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@DELETE
	public Response delete(@PathParam("id") long id) {
		Account user;
		try {
			user = accountService.findAccountById(id);
			if (user != null) {
				accountService.removeAccount(user);
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NO_CONTENT);
			} else {
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "AccountSvc.accountNotFound", id); //$NON-NLS-1$
			}
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PUT
	public Response update(@PathParam("id") long id, AccountJSON json) {
		Account account;
		try {
			account = accountService.findAccountById(id);
			if (account == null) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			Account extendedAccount = json.toAccount();
			if (id != extendedAccount.getId())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "AccountSvc.accountNotEquals", id, extendedAccount.getId()); //$NON-NLS-1$

			account.setAccessLevel(extendedAccount.getAccessLevel());
			account.setAttributes(extendedAccount.getAttributes());
			account.setDescription(extendedAccount.getDescription());
			if (extendedAccount.getStatus()!=null) {
				account.setStatus(extendedAccount.getStatus());
			} else {
				if (extendedAccount.isDisabled()) {
					account.setStatus(AccountStatus.DISABLED);
				} else  {
					account.setStatus(AccountStatus.ACTIVE);
				}
			}
			account.setGrantedGroups(extendedAccount.getGrantedGroups());
			account.setGrantedRoles(extendedAccount.getGrantedRoles());
			account.setGrantedUsers(extendedAccount.getGrantedUsers());
			account.setId(extendedAccount.getId());
			account.setInheritNewPermissions(extendedAccount.isInheritNewPermissions());
			account.setLoginUrl(extendedAccount.getLoginUrl());
			account.setManagerGroups(extendedAccount.getManagerGroups());
			account.setManagerRoles(extendedAccount.getManagerRoles());
			account.setManagerUsers(extendedAccount.getManagerUsers());
			account.setName(extendedAccount.getName());
			account.setOwnerGroups(extendedAccount.getOwnerGroups());
			account.setOwnerRoles(extendedAccount.getOwnerRoles());
			account.setPasswordPolicy(extendedAccount.getPasswordPolicy());
			account.setSystem(extendedAccount.getSystem());
			account.setVaultFolder(extendedAccount.getVaultFolder());
			account.setVaultFolderId(extendedAccount.getVaultFolderId());

			account = accountService.updateAccount2(account);
			updateRoles(json, account);
			return SCIMResponseBuilder.responseOk(toExtendedAccount(account, null));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PATCHAnnotation
	public Response patch(@PathParam("id") long id, AccountJSON json) {
		Account account;
		try {
			account = accountService.findAccountById(id);
			if (account == null) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			if (null != json.getId() && id != json.getId().longValue())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "AccountSvc.accountNotEquals", id, json.getId()); //$NON-NLS-1$

			Account extendedAccount;
			extendedAccount = json.toAccount();
			
			if (extendedAccount.getAccessLevel() != null) account.setAccessLevel(extendedAccount.getAccessLevel());
			if (extendedAccount.getDescription() != null) account.setDescription(extendedAccount.getDescription());
			if (extendedAccount.getStatus()!=null) {
				account.setStatus(extendedAccount.getStatus());
			} else {
				if (extendedAccount.isDisabled()!=account.isDisabled()) {
					if (extendedAccount.isDisabled()) {
						account.setStatus(AccountStatus.DISABLED);
					} else  {
						account.setStatus(AccountStatus.ACTIVE);
					}
				}
			}
			if (extendedAccount.getGrantedGroups() != null) account.setGrantedGroups(extendedAccount.getGrantedGroups());
			if (extendedAccount.getGrantedRoles() != null) account.setGrantedRoles(extendedAccount.getGrantedRoles());
			if (extendedAccount.getGrantedUsers() != null) account.setGrantedUsers(extendedAccount.getGrantedUsers());
			if (extendedAccount.isInheritNewPermissions() != account.isInheritNewPermissions())
				account.setInheritNewPermissions(extendedAccount.isInheritNewPermissions());
			if (extendedAccount.getLoginUrl() != null) account.setLoginUrl(extendedAccount.getLoginUrl());
			if (extendedAccount.getManagerGroups() != null) account.setManagerGroups(extendedAccount.getManagerGroups());
			if (extendedAccount.getManagerRoles() != null) account.setManagerRoles(extendedAccount.getManagerRoles());
			if (extendedAccount.getManagerUsers() != null) account.setManagerUsers(extendedAccount.getManagerUsers());
			if (extendedAccount.getName() != null) account.setName(extendedAccount.getName());
			if (extendedAccount.getOwnerGroups() != null) account.setOwnerGroups(extendedAccount.getOwnerGroups());
			if (extendedAccount.getOwnerRoles() != null) account.setOwnerRoles(extendedAccount.getOwnerRoles());
			if (extendedAccount.getPasswordPolicy() != null) account.setPasswordPolicy(extendedAccount.getPasswordPolicy());
			if (extendedAccount.getSystem() != null) account.setSystem(extendedAccount.getSystem());
			if (extendedAccount.getVaultFolder() != null) account.setVaultFolder(extendedAccount.getVaultFolder());
			if (extendedAccount.getVaultFolderId() != null) account.setVaultFolderId(extendedAccount.getVaultFolderId());
			account = accountService.updateAccount2(account);

			if (json.getRoles() != null) updateRoles(json, account);
			return SCIMResponseBuilder.responseOk(toExtendedAccount(account, null));
		} catch (Exception e) {
			log.warn("Error updating account", e);
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	private Collection<Object> toExtendedAccountList(Collection<Account> accountList, String attributes) throws InternalErrorException {
		List<Object> extendedAccountList = new LinkedList<Object>();
		if (null != accountList && !accountList.isEmpty()) {
			for (Account account : accountList) {
				extendedAccountList.add(toExtendedAccount(account, attributes));
			}
		}
		return extendedAccountList;
	}

	private AccountJSON toExtendedAccount(Account acc, String attributes) throws InternalErrorException {
		AccountJSON eacc = new AccountJSON(acc);

		// Add roles
		List<RoleDomainJSON> perms = new LinkedList<RoleDomainJSON>();
		for (RoleAccount data : applicationService.findRoleAccountByAccount(eacc.getId())) {
			RoleDomainJSON perm = new RoleDomainJSON();
			Role role = applicationService.findRoleByNameAndSystem(data.getRoleName(), data.getSystem());
			perm.setId(role.getId());
			perm.setRoleName(data.getRoleName());
			perm.setRoleDescription(data.getRoleDescription());
			perm.setInformationSystemName(data.getInformationSystemName());
			if (data.getDomainValue() != null) perm.setDomainValue(data.getDomainValue().getValue());
			perms.add(perm);
		}
		eacc.setRoles(perms);

		List<RoleDomainJSON> perms2 = new LinkedList<RoleDomainJSON>();
		if (attributes != null && attributes.contains("inheritedRoles")) {
			for (RoleGrant data : applicationService.findEffectiveRoleGrantByAccount(eacc.getId())) {
				RoleDomainJSON perm = new RoleDomainJSON();
				Role role = applicationService.findRoleByNameAndSystem(data.getRoleName(), data.getSystem());
				perm.setId(role.getId());
				perm.setRoleName(data.getRoleName());
				perm.setRoleDescription(role.getDescription());
				perm.setInformationSystemName(role.getInformationSystemName());
				if (data.getDomainValue() != null) perm.setDomainValue(data.getDomainValue());
				perms2.add(perm);
			}
			eacc.setInheritedRoles(perms2);
		}
		// Add SCIM tag meta
		MetaJSON meta = eacc.getMeta();
		meta.setLocation(getClass(), eacc.getId().toString());
		meta.setResourceType(RESOURCE);
		eacc.setMeta(meta);

		return eacc;
	}

	private void updateRoles(AccountJSON src, Account target)
			throws InternalErrorException, NeedsAccountNameException, AccountAlreadyExistsException {
		Collection<RoleAccount> accounts = applicationService.findRoleAccountByAccount(target.getId());
		if (null != src.getRoles()) {
			for (RoleAccount ua : accounts) {
				if (ua.getRuleId() == null) {
					Role role = applicationService.findRoleByNameAndSystem(ua.getRoleName(), ua.getSystem());
					boolean found = false;
					for (RoleDomainJSON ua2 : src.getRoles()) {
						if (found = compareRole(ua2, ua, role))
							break;
					}
					if (!found) {
						applicationService.delete(ua);
					}
				}
			}
			for (RoleDomainJSON ua2 : src.getRoles()) {
				boolean found = false;
				for (RoleAccount ua : accounts) {
					Role role = applicationService.findRoleByNameAndSystem(ua.getRoleName(), ua.getSystem());
					if (found = compareRole(ua2, ua, role))
						break;
				}
				if (!found) {
					Role role = null;
					if (ua2.getId()!=null) {
						role = applicationService.findRoleById(ua2.getId());
						if (role == null)
							throw new InternalErrorException("Cannot find role "+ua2.getId());
					} else {
						role = applicationService.findRoleByNameAndSystem(ua2.getRoleName(), target.getSystem());
						if (role == null)
							throw new InternalErrorException("Cannot find role "+ua2.getRoleName());
					}
					RoleAccount ra = new RoleAccount();
					ra.setAccountName(target.getName());
					ra.setSystem(target.getSystem());
					ra.setRoleName(role.getName());
					ra.setInformationSystemName(role.getInformationSystemName());
					ra.setDomainValue(new DomainValue());
					ra.getDomainValue().setDomainName(role.getDomain());
					ra.getDomainValue().setExternalCodeDomain(role.getInformationSystemName());
					if (ua2.getDomainValue() != null)
					{
						ra.getDomainValue().setValue(ua2.getDomainValue());
						ra.setUserCode(ua2.getUserCode());
					}
					ra.setAccountSystem(target.getSystem());
					applicationService.create(ra);
				}
			}
		}
	}

	private boolean compareRole(RoleDomainJSON src, RoleAccount target, Role role) {
		if (src.getId()!=null) {
			if (src.getId() == role.getId().longValue()) {
				if (compareDomian(src, target))
					return true;
			}
		} else {
			if (src.getRoleName().equals(role.getName())) {
				if (compareDomian(src, target))
					return true;
			}
		}
		return false;
	}

	private boolean compareDomian(RoleDomainJSON src, RoleAccount target) {
		if (src.getDomainValue() == null)
			return true;
		if (src.getDomainValue().trim().isEmpty() ? target.getDomainValue().getValue() == null : src.getDomainValue().equals(target.getDomainValue().getValue()))
			return true;
		return false;
	}

	private void updateAttributes(AccountJSON src, Account target, boolean delete)
			throws InternalErrorException, NeedsAccountNameException, AccountAlreadyExistsException {
		Collection<UserData> atts = accountService.getAccountAttributes(target);
		for (UserData ua : atts) {
			Object value = null;
			if (src.getAttributes()!=null && !src.getAttributes().isEmpty())
				value = src.getAttributes().get(ua.getAttribute());
			if (value == null) {
				if (ua.getId()!=null && (delete || src.getAttributes().containsKey(ua.getAttribute())))
					accountService.createAccountAttribute(ua);
			} else {
				if (value instanceof Date) {
					Calendar c = Calendar.getInstance();
					c.setTime((Date) value);
					ua.setDateValue(c);
				} else
					ua.setValue(value.toString());
				accountService.updateAccountAttribute(ua);
			}
		}
		if (src.getAttributes()!=null && !src.getAttributes().isEmpty()) {
			for (String key : src.getAttributes().keySet()) {
				boolean found = false;
				for (UserData ua : atts) {
					if (ua.getAttribute().equals(key)) {
						found = true;
						break;
					}
				}
				if (!found) {
					UserData data = new UserData();
					data.setAccountName(src.getName());
					data.setSystemName(src.getSystem());
					data.setAttribute(key);
					Object value = src.getAttributes().get(key);
					if (value instanceof Date) {
						Calendar c = Calendar.getInstance();
						c.setTime((Date) value);
						data.setDateValue(c);
					} else
						data.setValue(value.toString());
					accountService.createAccountAttribute(data);
				}
			}
		}
	}
}
