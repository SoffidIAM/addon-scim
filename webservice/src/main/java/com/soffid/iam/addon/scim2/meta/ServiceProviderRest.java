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
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim2.json.JSONBuilder;

@Path("/scim2/v1/ServiceProviderConfig")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class ServiceProviderRest {
	Log log = LogFactory.getLog(getClass());

	static String template =
			"{\n"
			+ "    \"schemas\":\n"
			+ "      [\"urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig\"],\n"
			+ "    \"documentationUri\": \"https://bookstack.soffid.com/book/scim\",\n"
			+ "    \"patch\": {\n"
			+ "      \"supported\":true\n"
			+ "    },\n"
			+ "    \"bulk\": {\n"
			+ "      \"supported\":false\n"
			+ "    },\n"
			+ "    \"filter\": {\n"
			+ "      \"supported\":true,\n"
			+ "      \"maxResults\": 1000\n"
			+ "    },\n"
			+ "    \"changePassword\": {\n"
			+ "      \"supported\":true\n"
			+ "    },\n"
			+ "    \"sort\": {\n"
			+ "      \"supported\":true\n"
			+ "    },\n"
			+ "    \"etag\": {\n"
			+ "      \"supported\":false\n"
			+ "    },\n"
			+ "    \"authenticationSchemes\": [\n"
			+ "		{\n"
			+ "        \"name\": \"HTTP Basic\",\n"
			+ "        \"description\":\n"
			+ "          \"Authentication scheme using the HTTP Basic Standard\",\n"
			+ "        \"specUri\": \"http://www.rfc-editor.org/info/rfc2617\",\n"
			+ "        \"documentationUri\": \"https://bookstack.soffid.com/book/scim\",\n"
			+ "        \"type\": \"httpbasic\"\n"
			+ "       }\n"
			+ "    ],\n"
			+ "    \"meta\": {\n"
			+ "      \"location\": \"\",\n"
			+ "      \"resourceType\": \"ServiceProviderConfig\"\n"
			+ "    }\n"
			+ "  }";
	@Path("")
	@GET
	public Response show(@Context HttpServletRequest request) {
		try {
			JSONBuilder b = new JSONBuilder(request);
			JSONObject root = new JSONObject(template);
			root.getJSONObject("meta").put("location", b.getServer()+"ServiceProvider");
			return Response.ok( (StreamingOutput) output -> {
				OutputStreamWriter w = new OutputStreamWriter(output);
				root.write(w);
				w.close();
				output.close();
			}).build();
		} catch (Exception e) {
			log.warn("Error processing SCIM Request "+request.getRequestURL(), e);
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}
}

