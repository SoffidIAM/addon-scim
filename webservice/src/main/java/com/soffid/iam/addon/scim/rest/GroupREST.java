package com.soffid.iam.addon.scim.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
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

import com.soffid.iam.addon.scim.json.GroupJSON;
import com.soffid.iam.addon.scim.json.MetaJSON;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.response.SCIMResponseList;
import com.soffid.iam.addon.scim.util.PATCHAnnotation;
import com.soffid.iam.addon.scim.util.PaginationUtil;
import com.soffid.iam.api.AsyncList;
import com.soffid.iam.api.Group;
import com.soffid.iam.service.ejb.GroupService;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim/Group")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class GroupREST {

	static final String RESOURCE = "Group";
	@EJB GroupService groupService;

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
		AsyncList<Group> l = groupService.findGroupByJsonQueryAsync(filter);
		while ( ! end && ! l.isCancelled()) {
			Thread.sleep(50);
			end = l.isDone();
			Iterator<Group> iterator = l.iterator();
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
				Group group = iterator.next();
				r.add(toExtendedGroup(group));
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

	@Path("/{id}")
	@GET
	public Response show(@PathParam("id") long id) {
		try {
			Group group = groupService.findGroupById(id);
			if (group != null)
				return SCIMResponseBuilder.responseOk(toExtendedGroup(group));
			else
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("")
	@POST
	public Response create(GroupJSON extendedGroup, @Context HttpServletRequest request) throws URISyntaxException {
		try {
			Group group = groupService.create(extendedGroup);
			if (group != null) {
				GroupJSON newExtendedGroup = toExtendedGroup(group);
				return SCIMResponseBuilder.responseOk(newExtendedGroup, new URI(newExtendedGroup.getMeta().getLocation()));
			} else
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
		} catch (EJBException e) {
			return SCIMResponseBuilder.errorCustom(Status.CONFLICT, e);
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@DELETE
	public Response delete(@PathParam("id") long id) {
		try {
			Group group = groupService.findGroupById(id);
			if (group != null) {
				return SCIMResponseBuilder.errorCustom(Status.INTERNAL_SERVER_ERROR, "GroupSvc.deleteNotAllowed"); // $NON-NLS-1$
			} else {
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "GroupSvc.groupNotFound", id); //$NON-NLS-1$
			}
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PATCHAnnotation
	public Response patch(@PathParam("id") long id, GroupJSON extendedGroup) {
		try {
			// Validations
			Group group = groupService.findGroupById(id);
			if (group == null) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			if (null != extendedGroup.getId() && id != extendedGroup.getId())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "GroupSvc.groupNotEquals", id, extendedGroup.getId()); //$NON-NLS-1$

			// Update only the attributes requested
			if (extendedGroup.getObsolete() != null) group.setObsolete(extendedGroup.getObsolete());
			if (extendedGroup.getOrganizational() != null) group.setOrganizational(extendedGroup.getOrganizational());
			if (extendedGroup.getAttributes() != null) group.setAttributes(extendedGroup.getAttributes());
			if (extendedGroup.getDescription() != null) group.setDescription(extendedGroup.getDescription());
			if (extendedGroup.getDriveLetter() != null) group.setDriveLetter(extendedGroup.getDriveLetter());
			if (extendedGroup.getDriveServerName() != null) group.setDriveServerName(extendedGroup.getDriveServerName());
			if (extendedGroup.getName() != null) group.setName(extendedGroup.getName());
			if (extendedGroup.getParentGroup() != null) group.setParentGroup(extendedGroup.getParentGroup());
			if (extendedGroup.getQuota() != null) group.setQuota(extendedGroup.getQuota());
			if (extendedGroup.getSection() != null) group.setSection(extendedGroup.getSection());
			if (extendedGroup.getType() != null) group.setType(extendedGroup.getType());

			// Update the group and return the result
			groupService.update(group);
			return SCIMResponseBuilder.responseOk(toExtendedGroup(group));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PUT
	public Response update(@PathParam("id") long id, GroupJSON extendedGroup) {
		try {
			// Validations
			Group group = groupService.findGroupById(id);
			if (group == null) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			if (null != extendedGroup.getId() && id != extendedGroup.getId())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "GroupSvc.groupNotEquals", id, extendedGroup.getId()); // $NON-NLS-1$

			// Update only the attributes requested
			group.setObsolete(extendedGroup.getObsolete());
			group.setOrganizational(extendedGroup.getOrganizational());
			group.setAttributes(extendedGroup.getAttributes());
			group.setDescription(extendedGroup.getDescription());
			group.setDriveLetter(extendedGroup.getDriveLetter());
			group.setDriveServerName(extendedGroup.getDriveServerName());
			group.setId(extendedGroup.getId());
			group.setName(extendedGroup.getName());
			group.setParentGroup(extendedGroup.getParentGroup());
			group.setQuota(extendedGroup.getQuota());
			group.setSection(extendedGroup.getSection());
			group.setType(extendedGroup.getType());

			// Update the group and return the result
			groupService.update(group);
			return SCIMResponseBuilder.responseOk(toExtendedGroup(group));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	private GroupJSON toExtendedGroup(Group group) throws InternalErrorException {
		GroupJSON extendedGroup = new GroupJSON(group);

		// Include scim meta attributes
		MetaJSON meta = extendedGroup.getMeta();
		meta.setLocation(getClass(), group.getId().toString());
		meta.setResourceType(RESOURCE);
		return extendedGroup;
	}
}
