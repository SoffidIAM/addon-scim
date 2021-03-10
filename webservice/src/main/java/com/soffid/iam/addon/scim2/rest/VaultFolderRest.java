package com.soffid.iam.addon.scim2.rest;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/scim2/v1/VaultFolder")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class VaultFolderRest extends BaseRest<com.soffid.iam.api.VaultFolder> {

	public VaultFolderRest() {
		super(com.soffid.iam.api.VaultFolder.class);
	}

}

