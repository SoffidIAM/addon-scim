package com.soffid.iam.addon.scim2.rest;

import java.io.OutputStreamWriter;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

import com.soffid.iam.addon.scim2.json.JSONBuilder;
import com.soffid.iam.api.Application;
import com.soffid.iam.api.Group;

@Path("/scim2/v1/Group")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class GroupRest extends BaseRest<Group> {

	public GroupRest() {
		super(Group.class);
	}

	@Override
	public String[] jsonAttributesToIgnore() {
		return new String[] {"members", "administrators"};
	}

	@Override
	public void writeObject(OutputStreamWriter w, JSONBuilder builder, Group obj) {
		JSONObject jsonObject = builder.build(obj);

		addReference (builder, jsonObject, "members", "User?filter=primaryGroup+eq+'"+encode(obj.getName())+"'+or secondaryGroup.group.name+eq+'"+encode(obj.getName())+"'");
		
		addReference (builder, jsonObject, "administrators", "RoleAccount?filter=group.name+eq+'"+ encode( obj.getName())+"'");

		jsonObject.write(w);
	}
}

