package com.soffid.iam.addon.scim2.rest;

import java.io.OutputStreamWriter;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

import com.soffid.iam.addon.scim2.json.JSONBuilder;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.Application;

@Path("/scim2/v1/Application")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class ApplicationRest extends BaseRest<Application> {

	public ApplicationRest() {
		super(Application.class);
	}

	@Override
	public String[] jsonAttributesToIgnore() {
		return new String[] {"roles", "children"};
	}

	@Override
	public void writeObject(OutputStreamWriter w, JSONBuilder builder, Application obj) {
		JSONObject jsonObject = builder.build(obj);

		addReference (builder, jsonObject, "roles", "Role?filter=informationSystemName+eq+'"+encode( obj.getName())+"'");
		
		addReference (builder, jsonObject, "children", "Application?filter=parent.name+eq+'"+encode(obj.getName())+"'");

		jsonObject.write(w);
	}
}

