package com.soffid.iam.addon.scim2.rest;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.soffid.iam.addon.scim2.crud.CrudOsTypeHandler;
import com.soffid.iam.addon.scim2.crud.CrudTaskInstanceHandler;
import com.soffid.iam.api.CrudHandler;
import com.soffid.iam.api.Host;
import com.soffid.iam.api.Network;
import com.soffid.iam.api.OsType;
import com.soffid.iam.bpm.api.TaskInstance;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim2/v1/OsType")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class OsTypeRest extends BaseRest<OsType> {

	public OsTypeRest() {
		super(OsType.class);
	}

	CrudHandler<OsType> getCrud() throws InternalErrorException, NamingException, CreateException {
		return new CrudOsTypeHandler();
	}

}

