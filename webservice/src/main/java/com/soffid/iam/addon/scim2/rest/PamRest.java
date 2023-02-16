package com.soffid.iam.addon.scim2.rest;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.util.PATCHAnnotation;
import com.soffid.iam.addon.scim.util.PaginationUtil;
import com.soffid.iam.addon.scim2.json.JSONBuilder;
import com.soffid.iam.addon.scim2.json.JSONParser;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.CrudHandler;
import com.soffid.iam.api.DataType;
import com.soffid.iam.api.Group;
import com.soffid.iam.api.NewPamSession;
import com.soffid.iam.api.PagedResult;
import com.soffid.iam.api.Password;
import com.soffid.iam.common.security.SoffidPrincipal;
import com.soffid.iam.utils.Security;

@Path("/scim2/v1/PamSession")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class PamRest {

	public PamRest() {
	}


	@Path("")
	@POST
	public Response create(String data, @Context HttpServletRequest request) throws URISyntaxException {
		Security.nestedLogin( (SoffidPrincipal) ((HttpServletRequest) request).getUserPrincipal());
		try {
			JSONObject o = new JSONObject(data);
			Account account = EJBLocator.getAccountService().findAccount(o.getString("name"), o.getString("system"));
			if (account == null)
			{
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "Account not found");
			}
			else {
				String desc = o.optString("descriptor", null);
				NewPamSession s ;
				if (desc != null)
					s = EJBLocator.getPamSessionService().createJumpServerSession(account, desc);
				else
					s = EJBLocator.getPamSessionService().createJumpServerSession(account);
				
				JSONObject json = new JSONBuilder(request).build(s);
				return SCIMResponseBuilder.responseOk(json.toString());
			}
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		} finally {
			Security.nestedLogoff();
		}
	}
}
