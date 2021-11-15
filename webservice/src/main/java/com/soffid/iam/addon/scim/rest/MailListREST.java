package com.soffid.iam.addon.scim.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
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

import com.soffid.iam.addon.scim.json.MailListJSON;
import com.soffid.iam.addon.scim.json.MetaJSON;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.response.SCIMResponseList;
import com.soffid.iam.api.ExternalName;
import com.soffid.iam.api.Group;
import com.soffid.iam.api.MailList;
import com.soffid.iam.api.MailListRelated;
import com.soffid.iam.api.MailListRoleMember;
import com.soffid.iam.api.UserMailList;
import com.soffid.iam.service.ejb.MailListsService;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim/MailList")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class MailListREST {

	static final String RESOURCE = "MailList";
	@EJB MailListsService service;

	/*
	{
		"usersList": "arno.rijpma03, becario, arno.rijpma04",
		"roleMembers": "SOFFID_CISO@soffid, SOFFID_MANAGER@soffid",
		"groupMembers": "CAA0002, CAA0001",
		"lists": "aaa@soffid.com",
		"externalList": "exemple@domini.com",
		
		"name": "test",
		"domainCode": "soffid.com",
		"description": "test-usuarios",
		"id": 673672,
		
		"explodedUsersList": "jvidal, test, becario, arno.rijpma, arno.rijpma04, arno.rijpma03",
		"attributes": {},
		"listsBelong": "",
		"meta": {
		    "location": "http://svives-Lenovo-ideapad-310-15IKB:8080/webservice/scim/MailList/673672",
		    "resourceType": "MailList"
		}
	}
	*/

	@Path("")
	@GET
	public Response list(@QueryParam("filter") @DefaultValue("") String filter, @QueryParam("attributes") String atts)
			throws InternalErrorException {
		Collection<MailList> hl = service.getMailLists();
		return SCIMResponseBuilder.responseList(new SCIMResponseList(toMailListJSONList(hl)));
	}

	@Path("")
	@POST
	public Response create(MailListJSON json, @Context HttpServletRequest request) throws URISyntaxException {
		try {
			MailList ml = service.create(json.toSoffid3Api());
			if (ml != null) {

				// Create usersList
				if (json.getUsersList()!=null && !json.getUsersList().trim().isEmpty()) {
					for (String ul : json.getUsersList().split(",")) {
						UserMailList uml = new UserMailList();
						uml.setUserCode(ul.trim());
						uml.setMailListName(json.getName());
						uml.setDomainCode(json.getDomainCode());
						service.create(uml);
					}
				}

				// Create roleMembers
				if (json.getRoleMembers()!=null && !json.getRoleMembers().trim().isEmpty()) {
					for (String rm : json.getRoleMembers().split(",")) {
						String rmArray[] = rm.trim().split("@");
						String role = rmArray[0];
						String system = rmArray[1];
						MailListRoleMember mlrm = new MailListRoleMember();
						mlrm.setRoleName(role);
						mlrm.setDispatcherName(system);
						service.subscribeRole(json.getName(), json.getDomainCode(), mlrm);
					}
				}

				// Create groupMembers
				if (json.getGroupMembers()!=null && !json.getGroupMembers().trim().isEmpty()) {
					for (String gm : json.getGroupMembers().split(",")) {
						service.subscribeGroup(json.getName(), json.getDomainCode(), gm.trim());
					}
				}

				// Create lists
				if (json.getLists()!=null && !json.getLists().trim().isEmpty()) {
					for (String l : json.getLists().split(",")) {
						String lArray[] = l.trim().split("@");
						String list = lArray[0];
						String domain = lArray[1];
						MailListRelated mlr = new MailListRelated();
						mlr.setMailListNameBelong(list);
						mlr.setMailDomainBelongCode(domain);
						mlr.setMailListNameIncluded(json.getName());
						mlr.setMailDomainAccountCode(json.getDomainCode());
						service.create(mlr);
					}
				}

				// Create externalList
				if (json.getExternalList()!=null && !json.getExternalList().trim().isEmpty()) {
					for (String el : json.getExternalList().split(",")) {
						ExternalName en = new ExternalName();
						en.setMailListName(json.getName());
						en.setDomainCode(json.getDomainCode());
						en.setEmail(el);
						service.create(en);
					}
				}

				// To retrieve the object tu update all references
				ml = service.findMailListByNameAndDomainName(ml.getName(), ml.getDomainCode());
				MailListJSON mljson = toMailListJSON(ml);
				return SCIMResponseBuilder.responseOk(mljson, new URI(mljson.getMeta().getLocation()));	
			} else
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	/*
	@Path("/{id}")
	@GET
	public Response show(@PathParam("id") long id) {
		try {
			LinkedList<MailList> mllist = (LinkedList<MailList>) service.findMailListByJsonQuery("id eq \""+id+"\"");
			if (mllist.size()==0)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			return SCIMResponseBuilder.responseOk(toMailListJSON(mllist.get(0)));
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}
	*/

	@Path("/{id}")
	@GET
	public Response show(@PathParam("id") String id) {
		try {
			LinkedList<MailList> mllist = (LinkedList<MailList>) service.findMailListByJsonQuery("name eq \""+id+"\"");
			if (mllist.size()==0)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			return SCIMResponseBuilder.responseOk(toMailListJSON(mllist.get(0)));
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@DELETE
	public Response delete(@PathParam("id") long id) {
		try {
			LinkedList<MailList> mllist = (LinkedList<MailList>) service.findMailListByJsonQuery("id eq \""+id+"\"");
			if (mllist.size()==0)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			MailList ml = mllist.get(0);

			service.delete(ml);
			return SCIMResponseBuilder.responseOnlyHTTP(Status.NO_CONTENT);
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PUT
	public Response update(@PathParam("id") String id, MailListJSON json) {
		try {
			LinkedList<MailList> mllist = (LinkedList<MailList>) service.findMailListByJsonQuery("id eq \""+id+"\"");
			if (mllist.size()==0)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			MailList ml = mllist.get(0);

			// Update basic attributes
			//
			//
			service.update(json.toSoffid3Api());

			// To retrieve the object tu update all references
			ml = service.findMailListByNameAndDomainName(ml.getName(), ml.getDomainCode());
			MailListJSON mljson = toMailListJSON(ml);
			return SCIMResponseBuilder.responseOk(mljson);
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	/*
	@Path("/{id}")
	@PATCHAnnotation
	public Response patch(@PathParam("id") String id, MailListJSON json) {
		try {
			LinkedList<MailList> mllist = (LinkedList<MailList>) service.findMailListByJsonQuery("id eq \""+id+"\"");
			if (mllist.size()==0)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			service.update(json);
			return SCIMResponseBuilder.responseOk(toMailListJSON(json));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}
	*/

	private Collection<Object> toMailListJSONList(Collection<MailList> mailListList) throws InternalErrorException {
		List<Object> extendedMailListList = new LinkedList<Object>();
		if (null != mailListList && !mailListList.isEmpty()) {
			for (MailList ml : mailListList) {
				extendedMailListList.add(toMailListJSON(ml));
			}
		}
		return extendedMailListList;
	}

	private MailListJSON toMailListJSON(MailList md) throws InternalErrorException {
		MailListJSON mdj = new MailListJSON(md);
		MetaJSON meta = mdj.getMeta();
		meta.setLocation(getClass(), mdj.getId().toString());
		meta.setResourceType(RESOURCE);
		mdj.setMeta(meta);
		return mdj;
	}
}
