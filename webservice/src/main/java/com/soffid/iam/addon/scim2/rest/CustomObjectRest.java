package com.soffid.iam.addon.scim2.rest;

import java.util.Collection;
import java.util.LinkedList;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.api.CustomObject;
import com.soffid.iam.api.DataType;
import com.soffid.iam.api.Role;

import es.caib.seycon.ng.exception.InternalErrorException;

@Path("/scim2/v1/CustomObject")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class CustomObjectRest extends BaseRest<CustomObject> {

	public CustomObjectRest() {
		super(CustomObject.class);
	}

	protected Collection<DataType> getMetadata(CustomObject obj) throws InternalErrorException, NamingException, CreateException {
		if (obj == null || obj.getType() == null)
			return new LinkedList<DataType>();
		return EJBLocator.getAdditionalDataService()
			.findDataTypesByObjectTypeAndName(obj.getType(), null);
	}
}

