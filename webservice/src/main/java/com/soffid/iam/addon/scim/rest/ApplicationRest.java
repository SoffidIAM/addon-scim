package com.soffid.iam.addon.scim.rest;

import java.beans.PropertyDescriptor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.http.HttpServletRequest;
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

import org.apache.commons.beanutils.PropertyUtils;

import com.soffid.iam.addon.scim.json.ApplicationJSON;
import com.soffid.iam.addon.scim.json.MetaJSON;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.response.SCIMResponseList;
import com.soffid.iam.addon.scim.util.PATCHAnnotation;
import com.soffid.iam.addon.scim.util.PaginationUtil;
import com.soffid.iam.api.Application;
import com.soffid.iam.api.AsyncList;
import com.soffid.iam.service.ejb.ApplicationService;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim/Application")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class ApplicationRest {

	static final String RESOURCE = "Application";
	@EJB ApplicationService appService;

	@Path("")
	@GET
	public Response list(@QueryParam("filter") @DefaultValue("") String filter, @QueryParam("attributes") String atts,
			@QueryParam("startIndex") @DefaultValue("1") String startIndex, @QueryParam("count") @DefaultValue("1000") String count)
			throws Throwable {

		PaginationUtil p = new PaginationUtil(startIndex, count);
		List<Object> r = new LinkedList<>();
		int index = 1;
		int skip = 0;
		boolean end = false;
		AsyncList<Application> l = appService.findApplicationByJsonQueryAsync(filter);
		while ( ! end && ! l.isCancelled()) {
			Thread.sleep(50);
			end = l.isDone();
			Iterator<Application> iterator = l.iterator();
			for (int i = 0 ; i < skip && iterator.hasNext(); i++)
				iterator.next();

			while (index < p.getStartIndex() && iterator.hasNext()) {
				iterator.next();
				try {
					iterator.remove();
				} catch (Exception e) {
					skip ++;
				}
				index ++;
			}
			while ( iterator.hasNext() && ( !p.isActive() || index < p.getStartIndex() + p.getCount())) {
				Application application = iterator.next();
				r.add(toApplicationJSON(application));
				try {
					iterator.remove();
				} catch (Exception e) {
					skip ++;
				}
				index ++;
			}
			if (end)
			{
				while (iterator.hasNext()) {
					index ++;
					iterator.next();
					try {
						iterator.remove();
					} catch (Exception e) {
						l.cancel();
					}
				}
			}
		}
		if (l.isCancelled() && l.getExceptionToThrow() != null) {
			if (l.getExceptionToThrow() instanceof Exception)
				return SCIMResponseBuilder.errorGeneric((Exception) l.getExceptionToThrow());
			else
				throw l.getExceptionToThrow();
		} else {
			p.setTotalResults(index - 1);
			SCIMResponseList scimResponseList = new SCIMResponseList(r, p);
			if (p.isActive())
				scimResponseList.setItemsPerPage(p.getItemsPerPage());
			else
				scimResponseList.setItemsPerPage(index - 1);
			scimResponseList.setTotalResults(index-1);
			scimResponseList.setStartIndex(p.getStartIndex());
			return SCIMResponseBuilder.responseList(scimResponseList);
		}
	}

	@Path("")
	@POST
	public Response create(ApplicationJSON role, @Context HttpServletRequest request) throws URISyntaxException {
		try {
			Application newApplication = appService.create(role);
			if (newApplication != null) {
				ApplicationJSON ea = toApplicationJSON(newApplication);
				return SCIMResponseBuilder.responseOk(ea, new URI(ea.getMeta().getLocation()));
			} else
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@GET
	public Response show(@PathParam("id") long id) {
		Application role;
		try {
			Collection<Application> apps = appService.findApplicationByJsonQuery("id eq "+id);
			if (apps == null || apps.isEmpty())
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			role = apps.iterator().next();
			return SCIMResponseBuilder.responseOk(toApplicationJSON(role));
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@DELETE
	public Response delete(@PathParam("id") long id) {
		Application role;
		try {
			Collection<Application> apps = appService.findApplicationByJsonQuery("id eq "+id);
			if (apps == null || apps.isEmpty()) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			role = apps.iterator().next();
			if (role != null) {
				appService.delete(role);
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NO_CONTENT);
			} else {
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "ApplicationSvc.accountNotFound", id); //$NON-NLS-1$
			}
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PUT
	public Response update(@PathParam("id") long id, ApplicationJSON newApplication) {
		Application role;
		try {
			Collection<Application> apps = appService.findApplicationByJsonQuery("id eq "+id);
			if (apps == null || apps.isEmpty()) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			role = apps.iterator().next();
			if (id != newApplication.getId())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "ApplicationSvc.accountNotEquals", id, newApplication.getId()); //$NON-NLS-1$

			appService.update(newApplication);

			return SCIMResponseBuilder.responseOk(toApplicationJSON(role));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PATCHAnnotation
	public Response patch(@PathParam("id") long id, ApplicationJSON newApplication) {
		Application role;
		try {
			Collection<Application> apps = appService.findApplicationByJsonQuery("id eq "+id);
			if (apps == null || apps.isEmpty()) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			role = apps.iterator().next();
			if (newApplication.getId() != null && id != newApplication.getId())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "ApplicationSvic.accountNotEquals", id, newApplication.getId()); //$NON-NLS-1$

			PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(Application.class);
			for ( PropertyDescriptor property: properties)
			{
				if (property.getName().equals("attributes"))
				{
					role.getAttributes().putAll( newApplication.getAttributes());
				}
				else
				{
					Object v = PropertyUtils.getProperty(newApplication, property.getName());
					if (v != null && property.getWriteMethod() != null)
						PropertyUtils.setProperty(role, property.getName(), v);
				}
			}
			appService.update(role);

			return SCIMResponseBuilder.responseOk(toApplicationJSON(role));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	private ApplicationJSON toApplicationJSON(Application role) throws InternalErrorException {
		// Add roles
		ApplicationJSON roleJ = new ApplicationJSON(role);
		// Add SCIM tag meta
		MetaJSON meta = roleJ.getMeta();
		meta.setLocation(getClass(), roleJ.getId().toString());
		meta.setResourceType(RESOURCE);
		roleJ.setMeta(meta);
		
		return roleJ;
	}

}
