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

import com.soffid.iam.addon.scim.json.MailDomainJSON;
import com.soffid.iam.addon.scim.json.MetaJSON;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.response.SCIMResponseList;
import com.soffid.iam.addon.scim.util.PATCHAnnotation;
import com.soffid.iam.api.MailDomain;
import com.soffid.iam.service.ejb.MailListsService;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim/MailDomain")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class MailDomainREST {

	static final String RESOURCE = "MailDomain";
	@EJB MailListsService service;

	@Path("")
	@GET
	public Response list(@QueryParam("filter") @DefaultValue("") String filter, @QueryParam("attributes") String atts)
			throws InternalErrorException {
		Collection<MailDomain> hl = service.getDomainMails();
		return SCIMResponseBuilder.responseList(new SCIMResponseList(toMailDomainJSONList(hl)));
	}

	@Path("")
	@POST
	public Response create(MailDomainJSON json, @Context HttpServletRequest request) throws URISyntaxException {
		try {
			MailDomain md = service.create(json);
			if (md != null) {
				MailDomainJSON mdj = toMailDomainJSON(md);
				return SCIMResponseBuilder.responseOk(mdj, new URI(mdj.getMeta().getLocation()));
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
			MailDomain md = service.findMailDomainByName(id);
			if (md == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			return SCIMResponseBuilder.responseOk(toMailDomainJSON(md));
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@DELETE
	public Response delete(@PathParam("id") String id) {
		try {
			MailDomain md = service.findMailDomainByName(id);
			if (md == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			service.delete(md);
			return SCIMResponseBuilder.responseOnlyHTTP(Status.NO_CONTENT);
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PUT
	public Response update(@PathParam("id") String id, MailDomainJSON json) {
		try {
			MailDomain md = service.findMailDomainByName(id);
			if (md == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			service.update(json);
			return SCIMResponseBuilder.responseOk(toMailDomainJSON(json));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PATCHAnnotation
	public Response patch(@PathParam("id") String id, MailDomainJSON json) {
		try {
			MailDomain md = service.findMailDomainByName(id);
			if (md == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			service.update(json);
			return SCIMResponseBuilder.responseOk(toMailDomainJSON(json));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	private Collection<Object> toMailDomainJSONList(Collection<MailDomain> mailDomainList) throws InternalErrorException {
		List<Object> extendedMailDomainList = new LinkedList<Object>();
		if (null != mailDomainList && !mailDomainList.isEmpty()) {
			for (MailDomain md : mailDomainList) {
				extendedMailDomainList.add(toMailDomainJSON(md));
			}
		}
		return extendedMailDomainList;
	}

	private MailDomainJSON toMailDomainJSON(MailDomain md) throws InternalErrorException {
		MailDomainJSON mdj = new MailDomainJSON(md);
		MetaJSON meta = mdj.getMeta();
		meta.setLocation(getClass(), mdj.getId().toString());
		meta.setResourceType(RESOURCE);
		mdj.setMeta(meta);
		return mdj;
	}
}
