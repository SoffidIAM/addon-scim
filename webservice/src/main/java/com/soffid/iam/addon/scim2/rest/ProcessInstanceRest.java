package com.soffid.iam.addon.scim2.rest;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addon.scim2.crud.CrudProcessInstanceHandler;
import com.soffid.iam.api.CrudHandler;
import com.soffid.iam.bpm.api.ProcessInstance;
import com.soffid.iam.bpm.service.ejb.BpmEngine;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim2/v1/ProcessInstance")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class ProcessInstanceRest extends BaseRest<ProcessInstance> {

	public ProcessInstanceRest() {
		super(ProcessInstance.class);
	}

	CrudHandler<ProcessInstance> getCrud() throws InternalErrorException, NamingException, CreateException {
		return new CrudProcessInstanceHandler();
	}

	@Override
	public String[] jsonAttributesToIgnore() {
		return new String[] {"transition"};
	}

	@Override
	public ProcessInstance update(JSONObject json, ProcessInstance obj)
			throws Exception, InternalErrorException, NamingException, CreateException {
		ProcessInstance ti = super.update(json, obj);
		if (json.has("transition")) {
			String transition = json.getString("transition");
			BpmEngine engine = EJBLocator.getBpmEngine();
			engine.signal(ti, transition);
		}
		return ti;
	}
}

