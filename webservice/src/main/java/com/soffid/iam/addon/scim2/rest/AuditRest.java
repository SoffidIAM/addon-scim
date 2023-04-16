package com.soffid.iam.addon.scim2.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.api.Audit;
import com.soffid.iam.api.Role;
import com.soffid.iam.service.ejb.ApplicationService;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim2/v1/Audit")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class AuditRest extends BaseRest<Audit> {

	public AuditRest() {
		super(Audit.class);
	}

}

