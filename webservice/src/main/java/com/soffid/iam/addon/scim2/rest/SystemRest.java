package com.soffid.iam.addon.scim2.rest;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/scim2/v1/System")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class SystemRest extends BaseRest<com.soffid.iam.api.System> {

	public SystemRest() {
		super(com.soffid.iam.api.System.class);
	}

}

