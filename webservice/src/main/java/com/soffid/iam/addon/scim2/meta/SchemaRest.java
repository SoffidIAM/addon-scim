package com.soffid.iam.addon.scim2.meta;

import java.io.OutputStreamWriter;

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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim2.json.JSONBuilder;
import com.soffid.iam.addon.scim2.json.ResourceTypeGenerator;
import com.soffid.iam.api.PagedResult;

@Path("/scim2/v1/Schemas")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class SchemaRest {
	Log log = LogFactory.getLog(getClass());

	@GET
	public Response list(@QueryParam("filter") @DefaultValue("") String filter, 
			@Context HttpServletRequest request) {
		try {
			if (filter != null && !filter.trim().isEmpty()) 
				return SCIMResponseBuilder.errorCustom(Status.FORBIDDEN, new Exception("Cannot filter resource types"));
			else
			{
				JSONBuilder b = new JSONBuilder(request);
				JSONArray info = new ResourceTypeGenerator().generateSchema(b.getServer());
				return Response.ok( (StreamingOutput) output -> {
					OutputStreamWriter w = new OutputStreamWriter(output);
					JSONWriter jsonWriter = new JSONWriter(w);
					jsonWriter.object();
					jsonWriter.key("schemas");
					{
						jsonWriter.array();
						jsonWriter.value("urn:ietf:params:scim:api:messages:2.0:ListResponse");
						jsonWriter.endArray();
					}
					jsonWriter.key("totalResults"); jsonWriter.value(info.length());
					jsonWriter.key("startIndex"); jsonWriter.value(1);
					jsonWriter.key("Resources");
					jsonWriter.array();
					{
						for (int i = 0; i < info.length(); i++) {
							if (i > 0) w.append(",");
							info.getJSONObject(i).write(w);
						}
					}
					jsonWriter.endArray();
					jsonWriter.endObject();
					w.close();
					output.close();
				}).build();
			}
		} catch (Exception e) {
			log.warn("Error processing SCIM Request "+request.getRequestURL(), e);
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}
	
	@Path("/{id}")
	@GET
	public Response show(@PathParam("id") String id,
			@Context HttpServletRequest request) {
		try {
			JSONBuilder b = new JSONBuilder(request);
			JSONObject info = new ResourceTypeGenerator().generateSchema(b.getServer(), id);
			if (info == null)
					return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			else 
				return Response.ok( (StreamingOutput) output -> {
						OutputStreamWriter w = new OutputStreamWriter(output);
						info.write(w);
						w.close();
						output.close();
					}).build();
		} catch (Exception e) {
			log.warn("Error processing SCIM Request "+request.getRequestURL(), e);
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

}

