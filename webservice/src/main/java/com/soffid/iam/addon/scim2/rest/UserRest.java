package com.soffid.iam.addon.scim2.rest;

import java.io.OutputStreamWriter;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.json.JsonArray;
import javax.naming.NamingException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.util.PaginationUtil;
import com.soffid.iam.addon.scim2.json.JSONBuilder;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.PagedResult;
import com.soffid.iam.api.Password;
import com.soffid.iam.api.RoleGrant;
import com.soffid.iam.api.System;
import com.soffid.iam.api.User;
import com.soffid.iam.common.security.SoffidPrincipal;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.exception.BadPasswordException;
import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim2/v1/User")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class UserRest extends BaseRest<User> {

	public UserRest() {
		super(User.class);
	}

	@Override
	public User create(JSONObject json, User obj)
			throws Exception, InternalErrorException, NamingException, CreateException {
		User user = super.create(json, obj);
		updatePassword(json, user);
		return user;
	}

	@Override
	public User update(JSONObject json, User obj, User old)
			throws Exception, InternalErrorException, NamingException, CreateException {
		User user = super.update(json, obj, old);
		updatePassword(json, user);
		return user;
	}

	public void updatePassword(JSONObject json, User user)
			throws InternalErrorException, NamingException, CreateException, BadPasswordException {
		if (json.has("password")) {
			JSONArray a = json.getJSONArray("password");
			for (int i = 0; i < a.length(); i++) {
				JSONObject d = a.getJSONObject(i);
				String domain;
				if (d.has("domain")) domain = d.getString("domain");
				else domain = "DEFAULT";
				String value = d.getString("value");
				try {
					if (d.has("passwordExpired") && ! d.getBoolean("passwordExpired"))
						EJBLocator.getUserService().setPassword(user.getUserName(), domain, new Password(value));
					else
						EJBLocator.getUserService().setTemporaryPassword(user.getUserName(), domain, new Password(value));
				} catch (BadPasswordException e) {
					throw new InternalErrorException("Error setting the password: "+e);
				}
			}
		}
	}

	@Override
	public String[] jsonAttributesToIgnore() {
		return new String[] {"password", "roles", "accounts", "groups"};
	}

	@Override
	public void writeObject(OutputStreamWriter w, JSONBuilder builder, User obj) {
		JSONObject jsonObject = builder.build(obj);

		addReference (builder, jsonObject, "accounts", "Account?filter=type+eq+U+and+users.user.userName+eq+'"+encode(obj.getUserName())+"'");
		
		addReference (builder, jsonObject, "roleAccounts", "RoleAccount?filter=userCode+eq+'"+encode(obj.getUserName())+"'+and+enabled+eq+true");

		addReference (builder, jsonObject, "groupUsers", "GroupUser?filter=user+eq+'"+encode(obj.getUserName())+"'+and+disabled+eq+false");

		addReference (builder, jsonObject, "issues", "Issue?filter=user.userName+eq+'"+encode(obj.getUserName())+"'");
		
		addReference (builder, jsonObject, "effectiveGrants", "User/"+encode(obj.getId().toString())+"/effectiveGrants");

		jsonObject.write(w);
	}


	@Path("/{id}/effectiveGrants")
	@GET
	public Response list(@PathParam("id") long id, 
			@Context HttpServletRequest request)
			throws Throwable {
		JSONBuilder b = new JSONBuilder(request);
		Security.nestedLogin( (SoffidPrincipal) ((HttpServletRequest) request).getUserPrincipal());
		try {
			Collection<RoleGrant> r = EJBLocator.getApplicationService().findEffectiveRoleGrantByUser(id);
			
			return Response.ok( (StreamingOutput) output -> {
				OutputStreamWriter w = new OutputStreamWriter(output, "UTF-8");
				JSONWriter jsonWriter = new JSONWriter(w);
				jsonWriter.object();
				jsonWriter.key("schemas");
				{
					jsonWriter.array();
					jsonWriter.value("urn:ietf:params:scim:api:messages:2.0:ListResponse");
					jsonWriter.endArray();
				}
				jsonWriter.key("totalResults"); jsonWriter.value(r.size());
				jsonWriter.key("startIndex"); jsonWriter.value(1);
				jsonWriter.key("Resources");
				jsonWriter.array();
				{
					boolean first = true;
					for (RoleGrant grant: r) {
						if (first) first = false;
						else w.append(",");
						JSONObject jsonObject = b.build(grant);
						jsonObject.write(w);
					}
				}
				jsonWriter.endArray();
				jsonWriter.endObject();
				w.close();
				output.close();
			}).build();
		} catch (Exception e) {
			log.warn("Error processing SCIM Request "+request.getRequestURL(), e);
			return SCIMResponseBuilder.errorGeneric(e);
		} finally {
			Security.nestedLogoff();
		}
		
	}

}

