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
import com.soffid.iam.api.Role;
import com.soffid.iam.service.ejb.ApplicationService;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim2/v1/Role")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class RoleRest extends BaseRest<Role> {

	public RoleRest() {
		super(Role.class);
	}

	@Override
	public Role create(JSONObject json, Role obj)
			throws Exception, InternalErrorException, NamingException, CreateException {
		Role r = super.create(json, obj);
		synchronize(r);
		return r;
	}

	private void synchronize(Role r) throws Exception {
		ApplicationService svc = EJBLocator.getApplicationService();
		try {
			Method m = svc.getClass().getMethod("synchronizeRole", Role.class);
			m.invoke(svc, r);
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {
			Throwable cause = e.getTargetException();
			if (cause instanceof Exception) throw (Exception) cause;
			else throw new RuntimeException(cause);
		}
	}

	@Override
	public Role update(JSONObject json, Role obj, Role old)
			throws Exception, InternalErrorException, NamingException, CreateException {
		Role r = super.update(json, obj, old);
		synchronize(r);
		return r;
	}

	@Override
	public void delete(Role obj) throws Exception, InternalErrorException, NamingException, CreateException {
		synchronize(obj);
		super.delete(obj);
	}

}

