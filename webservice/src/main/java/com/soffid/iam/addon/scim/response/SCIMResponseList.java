package com.soffid.iam.addon.scim.response;

import java.util.Collection;

public class SCIMResponseList {

	static final String[] SCHEMAS = new String[] {"urn:ietf:params:scim:api:messages:2.0:ListResponse"};
	int totalResults = 0;
	Collection<Object> resources = null;

	public SCIMResponseList(Collection<Object> list) {
		this.resources = list;
		this.totalResults = this.resources.size();
	}

	public String[] getSchemas() {
		return SCHEMAS;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public Collection<Object> getResources() {
		return resources;
	}

	public void setResources(Collection<Object> resources) {
		this.resources = resources;
	}
}