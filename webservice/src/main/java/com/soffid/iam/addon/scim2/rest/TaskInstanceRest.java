package com.soffid.iam.addon.scim2.rest;

import java.io.OutputStreamWriter;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addon.scim2.crud.CrudTaskInstanceHandler;
import com.soffid.iam.addon.scim2.json.JSONBuilder;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.CrudHandler;
import com.soffid.iam.bpm.api.TaskInstance;
import com.soffid.iam.bpm.service.ejb.BpmEngine;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim2/v1/TaskInstance")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class TaskInstanceRest extends BaseRest<TaskInstance> {

	public TaskInstanceRest() {
		super(TaskInstance.class);
	}

	CrudHandler<TaskInstance> getCrud() throws InternalErrorException, NamingException, CreateException {
		return new CrudTaskInstanceHandler();
	}

	@Override
	public TaskInstance update(JSONObject json, TaskInstance obj, TaskInstance old)
			throws Exception, InternalErrorException, NamingException, CreateException {
		TaskInstance ti = super.update(json, obj, old);
		BpmEngine engine = EJBLocator.getBpmEngine();
		if (json.has("comment")) {
			engine.addComment(ti, json.getString("comment"));
		}
		if (json.has("transition")) {
			String transition = json.getString("transition");
			ti = engine.executeTask(ti, transition);
		}
		return ti;
	}

	@Override
	public String[] jsonAttributesToIgnore() {
		return new String[] {"transition", "comment"};
	}

}

