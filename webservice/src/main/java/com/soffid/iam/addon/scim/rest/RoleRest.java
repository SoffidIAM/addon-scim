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

import com.soffid.iam.addon.scim.json.MetaJSON;
import com.soffid.iam.addon.scim.json.RoleGrantJSON;
import com.soffid.iam.addon.scim.json.RoleJSON;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.response.SCIMResponseList;
import com.soffid.iam.addon.scim.util.PATCHAnnotation;
import com.soffid.iam.addon.scim.util.PaginationUtil;
import com.soffid.iam.api.AsyncList;
import com.soffid.iam.api.Role;
import com.soffid.iam.api.RoleGrant;
import com.soffid.iam.service.ejb.ApplicationService;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim/Role")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class RoleRest {

	static final String RESOURCE = "Role";
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
		AsyncList<Role> l = appService.findRoleByJsonQueryAsync(filter);
		while ( ! end && ! l.isCancelled()) {
			Thread.sleep(50);
			end = l.isDone();
			Iterator<Role> iterator = l.iterator();
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
				Role role = iterator.next();
				r.add(toRoleJSON(role));
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
	public Response create(RoleJSON role, @Context HttpServletRequest request) throws URISyntaxException {
		try {
			Role newRole = appService.create2(role);
			if (newRole != null) {
				RoleJSON ea = toRoleJSON(newRole);
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
		Role role;
		try {
			role = appService.findRoleById(id);
			if (role != null)
				return SCIMResponseBuilder.responseOk(toRoleJSON(role));
			else
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@DELETE
	public Response delete(@PathParam("id") long id) {
		Role role;
		try {
			role = appService.findRoleById(id);
			if (role != null) {
				appService.delete(role);
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NO_CONTENT);
			} else {
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "RoleSvc.roleNotFound", id); //$NON-NLS-1$
			}
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PUT
	public Response update(@PathParam("id") long id, RoleJSON newRole) {
		Role role;
		try {
			role = appService.findRoleById(id);
			if (role == null) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			if (id != newRole.getId())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "RoleSvc.accountNotEquals", id, newRole.getId()); //$NON-NLS-1$

			role = appService.update2(newRole);

			return SCIMResponseBuilder.responseOk(toRoleJSON(role));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PATCHAnnotation
	public Response patch(@PathParam("id") long id, RoleJSON newRole) {
		Role role;
		try {
			role = appService.findRoleById(id);
			if (role == null) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			if (newRole.getId() != null && id != newRole.getId().longValue())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "RoleSvc.accountNotEquals", id, newRole.getId()); //$NON-NLS-1$

			PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(Role.class);
			for ( PropertyDescriptor property: properties)
			{
				if (property.getName().equals("attributes"))
				{
					role.getAttributes().putAll( newRole.getAttributes());
				}
				else
				{
					Object v = PropertyUtils.getProperty(newRole, property.getName());
					if (v != null && property.getWriteMethod() != null)
						PropertyUtils.setProperty(role, property.getName(), v);
				}
			}
			role = appService.update2(role);

			return SCIMResponseBuilder.responseOk(toRoleJSON(role));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	private RoleJSON toRoleJSON(Role role) throws InternalErrorException {
		// Add roles
		RoleJSON roleJ = new RoleJSON(role);
		// Add SCIM tag meta
		MetaJSON meta = roleJ.getMeta();
		meta.setLocation(getClass(), roleJ.getId().toString());
		meta.setResourceType(RESOURCE);
		roleJ.setMeta(meta);
		
		role.setGranteeGroups(toRoleGrantList(role.getGranteeGroups()));
		role.setOwnedRoles(toRoleGrantList(role.getOwnedRoles()));
		role.setOwnerRoles(toRoleGrantList(role.getOwnerRoles()));
		
		return roleJ;
	}

	private Collection<RoleGrant> toRoleGrantList(Collection<RoleGrant> granteeGroups) {
		if (granteeGroups == null)
			return null;
		
		List<RoleGrant> l = new LinkedList<RoleGrant>();
		for ( RoleGrant rg: granteeGroups)
		{
			l.add( new RoleGrantJSON(rg));
		}
		return l;
	}
}

