package com.soffid.iam.addon.scim.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
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

import com.soffid.iam.addon.scim.json.HostJSON;
import com.soffid.iam.addon.scim.json.MetaJSON;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.response.SCIMResponseList;
import com.soffid.iam.addon.scim.util.PATCHAnnotation;
import com.soffid.iam.api.Host;
import com.soffid.iam.service.ejb.NetworkService;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim/Host")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class HostREST {

	static final String RESOURCE = "Host";
	@EJB NetworkService service;

	@Path("")
	@GET
	public Response list(@QueryParam("filter") @DefaultValue("") String filter, @QueryParam("attributes") String atts)
			throws InternalErrorException {
		Collection<Host> hl = service.findHostByFilter(null, null, null, null, null, null, null, null, null, null, null, false);
		return SCIMResponseBuilder.responseList(new SCIMResponseList(toHostJSONList(hl)));
	}

	@Path("")
	@POST
	public Response create(HostJSON host, @Context HttpServletRequest request) throws URISyntaxException {
		try {
			Host newHost = service.create(host.toHost());
			if (newHost != null) {
				HostJSON hj = toHostJSON(newHost);
				return SCIMResponseBuilder.responseOk(hj, new URI(hj.getMeta().getLocation()));
			} else
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@GET
	public Response show(@PathParam("id") String id) {
		try {
			Host h = service.findHostByName(id);
			if (h == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			return SCIMResponseBuilder.responseOk(toHostJSON(h));
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	/*
	@Path("/{id}")
	@GET
	public Response show(@PathParam("id") long id) {
		try {
			Host h = service.findHostById(id);
			if (h == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			return SCIMResponseBuilder.responseOk(toHostJSON(h));
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}
	*/

	@Path("/{id}")
	@DELETE
	public Response delete(@PathParam("id") long id) {
		try {
			Host h = service.findHostById(id);
			if (h == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			service.delete(h);
			return SCIMResponseBuilder.responseOnlyHTTP(Status.NO_CONTENT);
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PUT
	public Response update(@PathParam("id") long id, HostJSON newHost) {
		try {
			Host h = service.findHostById(id);
			if (h == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			service.update(newHost.toHost());
			return SCIMResponseBuilder.responseOk(toHostJSON(h));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PATCHAnnotation
	public Response patch(@PathParam("id") long id, HostJSON newHost) {
		try {
			Host h = service.findHostById(id);
			if (h == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			h = newHost.toHost();
			service.update(h);
			return SCIMResponseBuilder.responseOk(toHostJSON(h));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	private Collection<Object> toHostJSONList(Collection<Host> hostList) throws InternalErrorException {
		List<Object> extendedHostList = new LinkedList<Object>();
		if (null != hostList && !hostList.isEmpty()) {
			for (Host h : hostList) {
				extendedHostList.add(toHostJSON(h));
			}
		}
		return extendedHostList;
	}

	private HostJSON toHostJSON(Host host) throws InternalErrorException {
		HostJSON hostJ = new HostJSON(host);
		MetaJSON meta = hostJ.getMeta();
		meta.setLocation(getClass(), hostJ.getId().toString());
		meta.setResourceType(RESOURCE);
		hostJ.setMeta(meta);
		return hostJ;
	}
}
