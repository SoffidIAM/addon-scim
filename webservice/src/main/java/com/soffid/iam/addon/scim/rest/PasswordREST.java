package com.soffid.iam.addon.scim.rest;

import java.net.URISyntaxException;
import java.util.Collection;

import javax.ejb.EJB;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.soffid.iam.addon.scim.json.PasswordJSON;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.api.Password;
import com.soffid.iam.api.User;
import com.soffid.iam.api.UserAccount;
import com.soffid.iam.service.ejb.AccountService;
import com.soffid.iam.service.ejb.PasswordService;
import com.soffid.iam.service.ejb.UserService;

@Path("/scim/Password")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class PasswordREST {

	@EJB UserService userService;
	@EJB AccountService accountService;
	@EJB PasswordService passwordService;

	@Path("")
	@PUT
	public Response changePassword(PasswordJSON json, @Context HttpServletRequest request)
			throws URISyntaxException {
		try {
			User u = userService.findUserByUserName(json.getUserName());
			if (u == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			if (json.getMustChange()==null)
				return SCIMResponseBuilder.errorGeneric(new Exception("mustChange obligarori"));

			if (json.getMustChange().booleanValue()) {
				userService.setTemporaryPassword(u.getUserName(), "DEFAULT", new Password(json.getPassword()));
			} else {
				userService.setPassword(u.getUserName(), "DEFAULT", new Password(json.getPassword()));
			}
			return SCIMResponseBuilder.responseOnlyHTTP(Status.OK);

		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}
}
