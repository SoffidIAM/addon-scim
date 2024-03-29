package com.soffid.iam.addon.scim.response;

import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.LogFactory;

import com.soffid.iam.addon.scim.rest.Messages;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.exception.SoffidStackTrace;

public class SCIMResponseBuilder {

	/**
	 * In the case the only the HTTP code status is required
	 */
	public static Response responseOnlyHTTP(Status status) {
		return Response.status(status).build();
	}

	/**
	 * Generic error or unmanaged exception
	 */
	public static Response errorGeneric(Exception e) {
		return Response.status(Status.BAD_REQUEST).entity(new SCIMResponseError(getOriginalMessage(e))).build();
	}

	/**
	 * Custom error
	 */
	public static Response errorCustom(Status status, Exception e) {
		return Response.status(status).entity(new SCIMResponseError(getOriginalMessage(e), status)).build();
	}

	/**
	 * Custom error
	 */
	public static Response errorCustom(Status status, String keyMessage, Object... args) {
		return Response.status(status).entity(new SCIMResponseError(String.format(Messages.getString(keyMessage), args), status)).build();
	}

	/**
	 * Normal response with HTTP 200 and the JSON with data
	 */
	public static Response responseOk(Object obj) {
		return Response.ok().entity(obj).build();
	}

	/**
	 * Normal response with HTTP 200 and URI and the JSON with data
	 */
	public static Response responseOk(Object obj, URI uri) {
		return Response.created(uri).entity(obj).build();
	}

	/**
	 * Object list response
	 */
	public static Response responseList(Object obj) {
		return Response.ok().entity(obj).build();
	}

	/**
	 * Search and return the original message of the list of exceptions
	 */
	private static String getOriginalMessage(Exception e) {
		return SoffidStackTrace.generateShortDescription(e);
	}
}
