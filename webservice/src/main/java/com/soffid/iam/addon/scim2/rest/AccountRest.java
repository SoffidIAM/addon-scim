package com.soffid.iam.addon.scim2.rest;

import java.io.OutputStreamWriter;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONArray;
import org.json.JSONObject;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addon.scim2.json.JSONBuilder;
import com.soffid.iam.addon.scim2.json.JSONParser;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.Password;
import com.soffid.iam.api.User;

import es.caib.seycon.ng.exception.BadPasswordException;
import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim2/v1/Account")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class AccountRest extends BaseRest<Account> {

	public AccountRest() {
		super(Account.class);
	}

	@Override
	public Account create(JSONObject json, Account obj)
			throws Exception, InternalErrorException, NamingException, CreateException {
		Account account = super.create(json, obj);
		updatePassword(json, account);
		return account;
	}

	public void updatePassword(JSONObject json, Account account)
			throws InternalErrorException, NamingException, CreateException {
		if (json.has("password")) {
			JSONObject d = json.getJSONObject("password");
			String value = d.getString("value");
			try {
				if (json.has("expired") && ! json.getBoolean("expired"))
					EJBLocator.getAccountService().setAccountPassword(account, new Password(json.getString("password")));
				else
					EJBLocator.getAccountService().setAccountTemporaryPassword(account, new Password(json.getString("password")));
				nothing(); // Hack to avoid compilation error
			} catch (BadPasswordException e) {
				throw new InternalErrorException("Error setting the password: "+e);
			}
		}
	}

	private void nothing() throws BadPasswordException {
		
	}
	
	@Override
	public Account update(JSONObject json, Account obj, Account old)
			throws Exception, InternalErrorException, NamingException, CreateException {
		Account account = super.update(json, obj, old);
		updatePassword(json, account);
		return account;
	}

	@Override
	public String[] jsonAttributesToIgnore() {
		return new String[] {"password", "users", "roleAccounts", "passwordExpired"};
	}

	@Override
	public void writeObject(OutputStreamWriter w, JSONBuilder builder, Account obj) {
		JSONObject jsonObject = builder.build(obj);

		addReference (builder, jsonObject, "users", "User?filter=accountAccess.account.id+eq+"+obj.getId()+"+or+accounts.account.id+eq+"+obj.getId());
		
		addReference (builder, jsonObject, "roleAccounts", "RoleAccount?filter=account.id+eq+"+obj.getId()+"+and+enabled+eq+true");

		addReference (builder, jsonObject, "briefAudit", "Audit?filter=searchIndex+eq+'ACC%23"+encode(obj.getId().toString())+"'");

		jsonObject.write(w);
	}
}

