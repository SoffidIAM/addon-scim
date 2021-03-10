package com.soffid.iam.addon.scim2.rest;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.soffid.iam.api.DomainValue;

@Path("/scim2/v1/DomainValue")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class DomainValueRest extends BaseRest<DomainValue> {

	public DomainValueRest() {
		super(DomainValue.class);
	}

}

