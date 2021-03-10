package com.soffid.iam.addon.scim2.meta;

import java.io.OutputStreamWriter;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim2.json.JSONBuilder;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.System;
import com.soffid.iam.api.User;
import com.soffid.iam.utils.Security;

@Path("/scim2/v1/Me")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class MeRest{
	Log log = LogFactory.getLog(getClass());
	
	@Path("")
	@GET
	public Response show(@Context HttpServletRequest request) {
		JSONBuilder b = new JSONBuilder(request);
		try {
			String user = Security.getCurrentUser();
			if (user != null) {
				User u = EJBLocator.getUserService().getCurrentUser();
				if (u == null)
					return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
				return Response.ok( (StreamingOutput) output -> {
					OutputStreamWriter w = new OutputStreamWriter(output);
					JSONObject jsonObject = b.build(u);
					jsonObject.write(w);
					w.close();
					output.close();
				}).build();
			} else {
				System system = EJBLocator.getDispatcherService().findSoffidDispatcher();
				Account account = EJBLocator.getAccountService().findAccount(Security.getCurrentAccount(), system.getName());
				if (account == null)
					return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
				return Response.ok( (StreamingOutput) output -> {
					OutputStreamWriter w = new OutputStreamWriter(output);
					JSONObject jsonObject = b.build(account);
					jsonObject.write(w);
					w.close();
					output.close();
				}).build();
			}
		} catch (Exception e) {
			log.warn("Error processing SCIM Request "+request.getRequestURL(), e);
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}
}

