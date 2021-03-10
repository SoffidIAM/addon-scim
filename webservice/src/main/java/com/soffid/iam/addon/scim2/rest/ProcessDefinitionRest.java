package com.soffid.iam.addon.scim2.rest;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.soffid.iam.addon.scim2.crud.CrudProcessDefinitionHandler;
import com.soffid.iam.api.CrudHandler;
import com.soffid.iam.bpm.api.ProcessDefinition;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim2/v1/ProcessDefinition")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class ProcessDefinitionRest extends BaseRest<ProcessDefinition> {

	public ProcessDefinitionRest() {
		super(ProcessDefinition.class);
	}

	CrudHandler<ProcessDefinition> getCrud() throws InternalErrorException, NamingException, CreateException {
		return new CrudProcessDefinitionHandler();
	}
}

