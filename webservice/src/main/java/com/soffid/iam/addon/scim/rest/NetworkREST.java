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

import com.soffid.iam.addon.scim.json.MetaJSON;
import com.soffid.iam.addon.scim.json.NetworkJSON;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.response.SCIMResponseList;
import com.soffid.iam.addon.scim.util.PATCHAnnotation;
import com.soffid.iam.api.Network;
import com.soffid.iam.api.NetworkAuthorization;
import com.soffid.iam.service.ejb.NetworkService;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim/Network")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class NetworkREST {

	static final String RESOURCE = "Network";
	@EJB NetworkService service;

	@Path("")
	@GET
	public Response list(@QueryParam("filter") @DefaultValue("") String filter, @QueryParam("attributes") String atts)
			throws InternalErrorException {
		Collection<Network> ln = service.getNetworks();
		return SCIMResponseBuilder.responseList(new SCIMResponseList(toNetworkJSONList(ln)));
	}

	@Path("")
	@POST
	public Response create(NetworkJSON network, @Context HttpServletRequest request) throws URISyntaxException {
		try {
			// First create the network
			Network newNetwork = service.create(network.toNetwork());
			if (newNetwork != null) {
				// Second create de acls
				for (NetworkAuthorization acl : network.getAcls()) {
					service.create(acl);
				}
				NetworkJSON nj = toNetworkJSON(newNetwork);
				return SCIMResponseBuilder.responseOk(nj, new URI(nj.getMeta().getLocation()));
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
			Network n = service.findNetworkByName(id);
			if (n == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			return SCIMResponseBuilder.responseOk(toNetworkJSON(n));
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@DELETE
	public Response delete(@PathParam("id") String id) {
		try {
			Network n = service.findNetworkByName(id);
			if (n == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			service.delete(n);
			return SCIMResponseBuilder.responseOnlyHTTP(Status.NO_CONTENT);
		} catch (InternalErrorException e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PUT
	public Response update(@PathParam("id") String id, NetworkJSON newNetwork) {
		try {
			Network n = service.findNetworkByName(id);
			if (n == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			Network n2 = newNetwork.toNetwork();
			service.update(n2);
			// Create or update new acls
			HashMap<String,NetworkAuthorization> hmACL = new HashMap<String,NetworkAuthorization>();
			for (NetworkAuthorization acl : newNetwork.getAcls()) {
				hmACL.put(acl.getIdentity().getUserCode(), acl);
				NetworkAuthorization soffidACL = service.findNetworkAuthorizationsByNetworkNameAndIdentityName(newNetwork.getCode(), acl.getIdentity().getUserCode());
				if (soffidACL==null)
					service.create(acl);
				else
					service.update(acl);
			}
			// Delete old soffid acls
			Collection<NetworkAuthorization> lna = service.getACL(n2);
			for (NetworkAuthorization soffidACL : lna) {
				if (hmACL.get(soffidACL.getIdentity().getUserCode())==null)
					service.delete(soffidACL);
			}
			// Response
			return SCIMResponseBuilder.responseOk(toNetworkJSON(n2));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PATCHAnnotation
	public Response patch(@PathParam("id") String id, NetworkJSON newNetwork) {
		try {
			Network n = service.findNetworkByName(id);
			if (n == null)
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			Network n2 = newNetwork.toNetwork();
			service.update(n2);
			// Create or update new acls
			HashMap<String,NetworkAuthorization> hmACL = new HashMap<String,NetworkAuthorization>();
			for (NetworkAuthorization acl : newNetwork.getAcls()) {
				hmACL.put(acl.getIdentity().getUserCode(), acl);
				NetworkAuthorization soffidACL = service.findNetworkAuthorizationsByNetworkNameAndIdentityName(newNetwork.getCode(), acl.getIdentity().getUserCode());
				if (soffidACL==null)
					service.create(acl);
				else
					service.update(acl);
			}
			// Delete old soffid acls
			Collection<NetworkAuthorization> lna = service.getACL(n2);
			for (NetworkAuthorization soffidACL : lna) {
				if (hmACL.get(soffidACL.getIdentity().getUserCode())==null)
					service.delete(soffidACL);
			}
			// Response
			return SCIMResponseBuilder.responseOk(toNetworkJSON(n2));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	private Collection<Object> toNetworkJSONList(Collection<Network> networkList) throws InternalErrorException {
		List<Object> extendedNetworkList = new LinkedList<Object>();
		if (null != networkList && !networkList.isEmpty()) {
			for (Network h : networkList) {
				extendedNetworkList.add(toNetworkJSON(h));
			}
		}
		return extendedNetworkList;
	}

	private NetworkJSON toNetworkJSON(Network network) throws InternalErrorException {
		
		// Final user to include in the response
		NetworkJSON networkJ = new NetworkJSON(network);

		// Include meta attribute
		MetaJSON meta = networkJ.getMeta();
		meta.setLocation(getClass(), networkJ.getId().toString());
		meta.setResourceType(RESOURCE);
		networkJ.setMeta(meta);
		
		// Include acls
		Collection<NetworkAuthorization> la = service.getACL(network);
		networkJ.setAcls(la);
		
		return networkJ;
	}
}
