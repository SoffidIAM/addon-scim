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
			MailList ml = service.create(json);
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

			// Delete usersList
			if (ml.getUsersList()!=null && !ml.getUsersList().trim().isEmpty()) {
				for (String ul : ml.getUsersList().split(",")) {
					UserMailList uml = service.findUserMailListByListNameAndDomainNameAndUserName(ml.getName(), ml.getDomainCode(), ul.trim());
					service.deleteUserMailList(uml);
				}
			}

			// Delete roleMembers
			if (ml.getRoleMembers()!=null && !ml.getRoleMembers().trim().isEmpty()) {
				for (String rm : ml.getRoleMembers().split(",")) {
					String rmArray[] = rm.trim().split("@");
					String role = rmArray[0];
					String system = rmArray[1];
					MailListRoleMember mlrm = new MailListRoleMember();
					mlrm.setRoleName(role);
					mlrm.setDispatcherName(system);
					service.unsubscribeRole(ml.getName(), ml.getDomainCode(), mlrm);
				}
			}

			// Delete groupMembers
			if (ml.getGroupMembers()!=null && !ml.getGroupMembers().trim().isEmpty()) {
				for (String gm : ml.getGroupMembers().split(",")) {
					service.unsubscribeGroup(ml.getName(), ml.getDomainCode(), gm.trim());
				}
			}

			// Delete lisks
			Collection<MailListRelated>	lmlr = service.findRelationsMailListByNameContainsMailListAndDomainName(ml.getName(), ml.getDomainCode());
			for (MailListRelated mlr : lmlr) {
				service.delete(mlr);
			}

			// Delete externalList
			Collection<ExternalName> len = service.findExternalMailsByNameListAndDomainName(ml.getName(), ml.getDomainCode());
			for (ExternalName en : len) {
				service.delete(en);
			}

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
			service.update(json);

			// Create or delete usersList
			//
			//
			HashMap<String,String> hmCorrectos = new HashMap<String,String>();
			if (json.getUsersList()!=null && !json.getUsersList().trim().isEmpty()) {
				for (String ul : json.getUsersList().split(",")) {
					hmCorrectos.put(ml.getName()+ml.getDomainCode()+ul.trim(), "");
					UserMailList uml = service.findUserMailListByListNameAndDomainNameAndUserName(ml.getName(), ml.getDomainCode(), ul.trim());
					if (uml==null) {
						uml = new UserMailList();
						uml.setUserCode(ul.trim());
						uml.setMailListName(ml.getName());
						uml.setDomainCode(ml.getDomainCode());
						service.create(uml);
					}
				}
			}
			Collection<UserMailList> luml = service.findUserMailListByListNameAndDomainName(ml.getName(), ml.getDomainCode());
			for (UserMailList uml : luml) {
				if (!hmCorrectos.containsKey(uml.getMailListName()+uml.getDomainCode()+uml.getUserCode()))
					service.deleteUserMailList(uml);
			}
			
			// Delete or create roleMembers
			//
			//
			hmCorrectos = new HashMap<String,String>();
			if (json.getRoleMembers()!=null && !json.getRoleMembers().trim().isEmpty()) {
				for (String rm : json.getRoleMembers().split(",")) {
					hmCorrectos.put(rm.trim(), "");
				}
			}
			Collection<MailListRoleMember> lmlrm = service.findRoleMembers(ml.getName(), ml.getDomainCode());
			for (MailListRoleMember mlrm : lmlrm) {
				if (!hmCorrectos.containsKey(mlrm.getRoleName()+"@"+mlrm.getDispatcherName())) {
					service.unsubscribeRole(ml.getName(), ml.getDomainCode(), mlrm);
				} else {
					hmCorrectos.remove(mlrm.getRoleName()+"@"+mlrm.getDispatcherName());
				}
			}
			for (String rm : hmCorrectos.keySet()) {
				String rmArray[] = rm.trim().split("@");
				String role = rmArray[0];
				String system = rmArray[1];
				MailListRoleMember mlrm = new MailListRoleMember();
				mlrm.setRoleName(role);
				mlrm.setDispatcherName(system);
				service.subscribeRole(ml.getName(), ml.getDomainCode(), mlrm);
			}

			// Delete or create groupMembers
			//
			//
			hmCorrectos = new HashMap<String,String>();
			if (json.getGroupMembers()!=null && !json.getGroupMembers().trim().isEmpty()) {
				for (String rm : json.getGroupMembers().split(",")) {
					hmCorrectos.put(rm.trim(), "");
				}
			}
			Collection<Group> lg = service.findGroupMembers(ml.getName(), ml.getDomainCode());
			for (Group g : lg) {
				if (!hmCorrectos.containsKey(g.getName())) {
					service.unsubscribeGroup(ml.getName(), ml.getDomainCode(), g.getName());
				} else {
					hmCorrectos.remove(g.getName());
				}
			}
			for (String rm : hmCorrectos.keySet()) {
				service.subscribeGroup(ml.getName(), ml.getDomainCode(), rm);
			}

			// Create or delete lists
			//
			//
			hmCorrectos = new HashMap<String,String>();
			if (json.getLists()!=null && !json.getLists().trim().isEmpty()) {
				for (String rm : json.getLists().split(",")) {
					hmCorrectos.put(rm.trim(), "");
				}
			}
			Collection<MailListRelated>	lmlr = service.findRelationsMailListByNameContainsMailListAndDomainName(ml.getName(), ml.getDomainCode());
			for (MailListRelated mlr : lmlr) {
				if (!hmCorrectos.containsKey(mlr.getMailListNameIncluded()+"@"+mlr.getMailDomainAccountCode())) {
					service.delete(mlr);
				} else {
					hmCorrectos.remove(mlr.getMailListNameIncluded()+"@"+mlr.getMailDomainAccountCode());
				}
			}
			for (String l : hmCorrectos.keySet()) {
				String lArray[] = l.trim().split("@");
				String list = lArray[0];
				String domain = lArray[1];
				MailListRelated mlr = new MailListRelated();
				mlr.setMailListNameBelong(list);
				mlr.setMailDomainBelongCode(domain);
				mlr.setMailListNameIncluded(ml.getName());
				mlr.setMailDomainAccountCode(ml.getDomainCode());
				service.create(mlr);
			}

			// Create or delete externalList
			//
			//
			hmCorrectos = new HashMap<String,String>();
			if (json.getExternalList()!=null && !json.getExternalList().trim().isEmpty()) {
				for (String el : json.getExternalList().split(",")) {
					hmCorrectos.put(el.trim(), "");
				}
			}
			Collection<ExternalName> len = service.findExternalMailsByNameListAndDomainName(ml.getName(), ml.getDomainCode());
			for (ExternalName en : len) {
				if (!hmCorrectos.containsKey(en.getEmail())) {
					service.delete(en);
				} else {
					hmCorrectos.remove(en.getEmail());
				}
			}
			for (String l : hmCorrectos.keySet()) {
				ExternalName en = new ExternalName();
				en.setMailListName(ml.getName());
				en.setDomainCode(ml.getDomainCode());
				en.setEmail(l);
				service.create(en);
			}

			// To retrieve the object tu update all references
			ml = service.findMailListByNameAndDomainName(ml.getName(), ml.getDomainCode());
			MailListJSON mljson = toMailListJSON(ml);
			return SCIMResponseBuilder.responseOk(toMailListJSON(mljson));
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
