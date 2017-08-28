package com.soffid.iam.addon.scim.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
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

import com.soffid.iam.addon.scim.json.AccountJSON;
import com.soffid.iam.addon.scim.json.RoleDomainJSON;
import com.soffid.iam.addon.scim.json.MetaJSON;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.response.SCIMResponseList;
import com.soffid.iam.addon.scim.util.PATCHAnnotation;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.Role;
import com.soffid.iam.api.RoleAccount;
import com.soffid.iam.service.ejb.AccountService;
import com.soffid.iam.service.ejb.ApplicationService;
import com.soffid.iam.service.ejb.DispatcherService;
import com.soffid.iam.service.ejb.UserService;

import es.caib.seycon.ng.exception.AccountAlreadyExistsException;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.exception.NeedsAccountNameException;

@Path("/scim/Account")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class AccountREST {

	static final String RESOURCE = "Account";
	@EJB AccountService accountService;
	@EJB UserService userService;
	@EJB ApplicationService applicationService;
	@EJB DispatcherService dispatcherService;

	@Path("")
	@GET
	public Response list(@QueryParam("filter") @DefaultValue("") String filter, @QueryParam("attributes") String atts)
			throws InternalErrorException {
		return SCIMResponseBuilder.responseList(new SCIMResponseList(toExtendedAccountList(accountService.findAccountByJsonQuery(filter))));
	}

	@Path("")
	@POST
	public Response create(AccountJSON account, @Context HttpServletRequest request) throws URISyntaxException {
		try {
			Account newAccount = accountService.createAccount(account);
			if (newAccount != null) {
				AccountJSON ea = toExtendedAccount(newAccount);
				return SCIMResponseBuilder.responseOk(ea, new URI(ea.getMeta().getLocation()));
			} else
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@GET
	public Response show(@PathParam("id") long id) {
		Account user;
		try {
			user = accountService.findAccountById(id);
			if (user != null)
				return SCIMResponseBuilder.responseOk(toExtendedAccount(user));
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
	public Response update(@PathParam("id") long id, AccountJSON extendedAccount) {
		Account account;
		try {
			account = accountService.findAccountById(id);
			if (account == null) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			if (id != extendedAccount.getId())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "AccountSvc.accountNotEquals", id, extendedAccount.getId()); //$NON-NLS-1$

			account.setAccessLevel(extendedAccount.getAccessLevel());
			account.setAttributes(extendedAccount.getAttributes());
			account.setDescription(extendedAccount.getDescription());
			account.setDisabled(extendedAccount.isDisabled());
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
			account.setOwnerUsers(extendedAccount.getOwnerUsers());
			account.setPasswordPolicy(extendedAccount.getPasswordPolicy());
			account.setSystem(extendedAccount.getSystem());
			account.setVaultFolder(extendedAccount.getVaultFolder());
			account.setVaultFolderId(extendedAccount.getVaultFolderId());
			account.setType(extendedAccount.getType());

			account = accountService.updateAccount(account);
			updateRoles(extendedAccount, account);

			return SCIMResponseBuilder.responseOk(toExtendedAccount(account));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PATCHAnnotation
	public Response patch(@PathParam("id") long id, AccountJSON extendedAccount) {
		Account account;
		try {
			account = accountService.findAccountById(id);
			if (account == null) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			if (null != extendedAccount.getId() && id != extendedAccount.getId())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "AccountSvc.accountNotEquals", id, extendedAccount.getId()); //$NON-NLS-1$

			if (extendedAccount.getAccessLevel() != null) account.setAccessLevel(extendedAccount.getAccessLevel());
			if (extendedAccount.getAttributes() != null) account.setAttributes(extendedAccount.getAttributes());
			if (extendedAccount.getDescription() != null) account.setDescription(extendedAccount.getDescription());
			if (extendedAccount.isDisabled() != account.isDisabled()) account.setDisabled(extendedAccount.isDisabled());
			if (extendedAccount.getGrantedGroups() != null) account.setGrantedGroups(extendedAccount.getGrantedGroups());
			if (extendedAccount.getGrantedRoles() != null) account.setGrantedRoles(extendedAccount.getGrantedRoles());
			if (extendedAccount.getGrantedRoles() != null) account.setGrantedUsers(extendedAccount.getGrantedUsers());
			if (extendedAccount.isInheritNewPermissions() != account.isInheritNewPermissions())
				account.setInheritNewPermissions(extendedAccount.isInheritNewPermissions());
			if (extendedAccount.getLoginUrl() != null) account.setLoginUrl(extendedAccount.getLoginUrl());
			if (extendedAccount.getManagerGroups() != null) account.setManagerGroups(extendedAccount.getManagerGroups());
			if (extendedAccount.getManagerRoles() != null) account.setManagerRoles(extendedAccount.getManagerRoles());
			if (extendedAccount.getManagerUsers() != null) account.setManagerUsers(extendedAccount.getManagerUsers());
			if (extendedAccount.getName() != null) account.setName(extendedAccount.getName());
			if (extendedAccount.getOwnerGroups() != null) account.setOwnerGroups(extendedAccount.getOwnerGroups());
			if (extendedAccount.getOwnerRoles() != null) account.setOwnerRoles(extendedAccount.getOwnerRoles());
			if (extendedAccount.getOwnerUsers() != null) account.setOwnerUsers(extendedAccount.getOwnerUsers());
			if (extendedAccount.getPasswordPolicy() != null) account.setPasswordPolicy(extendedAccount.getPasswordPolicy());
			if (extendedAccount.getSystem() != null) account.setSystem(extendedAccount.getSystem());
			if (extendedAccount.getVaultFolder() != null) account.setVaultFolder(extendedAccount.getVaultFolder());
			if (extendedAccount.getVaultFolderId() != null) account.setVaultFolderId(extendedAccount.getVaultFolderId());
			if (extendedAccount.getType() != null) account.setType(extendedAccount.getType());

			account = accountService.updateAccount(account);
			if (!extendedAccount.getRoles().isEmpty()) updateRoles(extendedAccount, account);

			return SCIMResponseBuilder.responseOk(toExtendedAccount(account));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	private Collection<Object> toExtendedAccountList(Collection<Account> accountList) throws InternalErrorException {
		List<Object> extendedAccountList = new LinkedList<Object>();
		if (null != accountList && !accountList.isEmpty()) {
			for (Account account : accountList) {
				extendedAccountList.add(toExtendedAccount(account));
			}
		}
		return extendedAccountList;
	}

	private AccountJSON toExtendedAccount(Account acc) throws InternalErrorException {
		AccountJSON eacc = new AccountJSON(acc);
		List<RoleDomainJSON> perms = new LinkedList<RoleDomainJSON>();
		for (RoleAccount data : applicationService.findRoleAccountByAccount(acc.getId())) {
			RoleDomainJSON perm = new RoleDomainJSON();
			if (data.getDomainValue() != null) perm.setDomainValue(data.getDomainValue().getValue());
			Role r = applicationService.findRoleByNameAndSystem(data.getRoleName(), data.getSystem());
			perm.setRole(r.getId());
			perms.add(perm);
		}
		eacc.setRoles(perms);

		MetaJSON meta = eacc.getMeta();
		meta.setLocation(getClass(), acc.getId().toString());
		meta.setResourceType(RESOURCE);
		eacc.setMeta(meta);

		return eacc;
	}

	private void updateRoles(AccountJSON src, Account target)
			throws InternalErrorException, NeedsAccountNameException, AccountAlreadyExistsException {
		Collection<RoleAccount> accounts = applicationService.findRoleAccountByAccount(target.getId());
		for (RoleAccount ua : accounts) {
			if (ua.getRuleId() == null) {
				Role role = applicationService.findRoleByNameAndSystem(ua.getRoleName(), ua.getSystem());
				boolean found = false;
				for (RoleDomainJSON ua2 : src.getRoles()) {
					if (ua2.getRole() == role.getId().longValue()) {
						if (ua2.getDomainValue() == null || ua2.getDomainValue().trim().isEmpty() ? ua.getDomainValue().getValue() == null
								: ua2.getDomainValue().equals(ua.getDomainValue().getValue())) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					applicationService.delete(ua);
				}
			}
		}
		if (null != src.getRoles()) {
			for (RoleDomainJSON ua2 : src.getRoles()) {
				boolean found = false;
				for (RoleAccount ua : accounts) {
					Role role = applicationService.findRoleByNameAndSystem(ua.getRoleName(), ua.getSystem());
					if (ua2.getRole() == role.getId().longValue()) {
						if (ua2.getDomainValue() == null || ua2.getDomainValue().trim().isEmpty() ? ua.getDomainValue().getValue() == null
								: ua2.getDomainValue().equals(ua.getDomainValue().getValue())) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					Role role = applicationService.findRoleById(ua2.getRole());
					RoleAccount ra = new RoleAccount();
					ra.setAccountName(target.getName());
					ra.setSystem(target.getSystem());
					ra.setRoleName(role.getName());
					ra.setInformationSystemName(role.getInformationSystemName());
					ra.setAccountSystem(target.getSystem());
				}
			}
		}
	}
}
