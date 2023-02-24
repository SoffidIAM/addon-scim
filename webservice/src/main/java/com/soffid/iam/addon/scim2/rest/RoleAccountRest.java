package com.soffid.iam.addon.scim2.rest;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

import com.soffid.iam.api.Role;
import com.soffid.iam.api.RoleAccount;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim2/v1/RoleAccount")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class RoleAccountRest extends BaseRest<RoleAccount> {

	public RoleAccountRest() {
		super(RoleAccount.class);
	}

}

