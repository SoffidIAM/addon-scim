package com.soffid.iam.addon.scim2.rest;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.soffid.iam.api.CustomObject;
import com.soffid.iam.api.Role;

@Path("/scim2/v1/CustomObject")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class CustomObjectRest extends BaseRest<CustomObject> {

	public CustomObjectRest() {
		super(CustomObject.class);
	}

}

