package com.soffid.iam.addon.scim2.rest;

import java.io.OutputStreamWriter;

import javax.ejb.CreateException;
import javax.json.JsonArray;
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
import com.soffid.iam.api.Account;
import com.soffid.iam.api.Password;
import com.soffid.iam.api.System;
import com.soffid.iam.api.User;

import es.caib.seycon.ng.exception.BadPasswordException;
import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim2/v1/User")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class UserRest extends BaseRest<User> {

	public UserRest() {
		super(User.class);
	}

	@Override
	public User create(JSONObject json, User obj)
			throws Exception, InternalErrorException, NamingException, CreateException {
		User user = super.create(json, obj);
		updatePassword(json, user);
		return user;
	}

	@Override
	public User update(JSONObject json, User obj)
			throws Exception, InternalErrorException, NamingException, CreateException {
		User user = super.update(json, obj);
		updatePassword(json, user);
		return user;
	}

	public void updatePassword(JSONObject json, User user)
			throws InternalErrorException, NamingException, CreateException, BadPasswordException {
		if (json.has("password")) {
			JSONArray a = json.getJSONArray("password");
			for (int i = 0; i < a.length(); i++) {
				JSONObject d = a.getJSONObject(i);
				String domain;
				if (d.has("domain")) domain = d.getString("domain");
				else domain = "DEFAULT";
				String value = d.getString("value");
				try {
					if (json.has("passwordExpired") && ! json.getBoolean("passwordExpired"))
						EJBLocator.getUserService().setPassword(user.getUserName(), domain, new Password(value));
					else
						EJBLocator.getUserService().setTemporaryPassword(user.getUserName(), domain, new Password(value));
				} catch (BadPasswordException e) {
					throw new InternalErrorException("Error setting the password: "+e);
				}
			}
		}
	}

	@Override
	public String[] jsonAttributesToIgnore() {
		return new String[] {"password", "roles", "accounts", "groups"};
	}

	@Override
	public void writeObject(OutputStreamWriter w, JSONBuilder builder, User obj) {
		JSONObject jsonObject = builder.build(obj);

		addReference (builder, jsonObject, "accounts", "Account?filter=type+eq+U+and+users.user.userName+eq+'"+encode(obj.getUserName())+"'");
		
		addReference (builder, jsonObject, "roleAccounts", "RoleAccount?filter=userCode+eq+'"+encode(obj.getUserName())+"'+and+enabled+eq+true");

		addReference (builder, jsonObject, "groupUsers", "GroupUser?filter=user+eq+'"+encode(obj.getUserName())+"'+and+disabled+eq+false");

		jsonObject.write(w);
	}

}

