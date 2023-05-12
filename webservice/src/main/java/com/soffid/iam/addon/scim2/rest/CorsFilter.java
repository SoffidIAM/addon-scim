package com.soffid.iam.addon.scim2.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.soffid.iam.utils.ConfigurationCache;

@Provider
public class CorsFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		String origin = ConfigurationCache.getProperty("soffid.scim.cors.origin");
		if (origin != null) {
			responseContext.getHeaders().add(
                "Access-Control-Allow-Origin", origin);
            responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials", "true");
            responseContext.getHeaders().add(
               "Access-Control-Allow-Headers",
               "origin, content-type, accept, authorization");
    		String methods = ConfigurationCache.getProperty("soffid.scim.cors.methods");
            responseContext.getHeaders().add(
                "Access-Control-Allow-Methods", 
        		methods == null ? "GET, OPTIONS, HEAD": methods);
		}
	}

}
