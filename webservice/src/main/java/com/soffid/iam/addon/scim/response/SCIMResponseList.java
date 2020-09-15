package com.soffid.iam.addon.scim.response;

import java.util.Collection;

import com.soffid.iam.addon.scim.util.PaginationUtil;

public class SCIMResponseList {

	static final String[] SCHEMAS = new String[] {"urn:ietf:params:scim:api:messages:2.0:ListResponse"};
	int totalResults = 0;
	Collection<Object> resources = null;

	int itemsPerPage = 0;
	int startIndex = 0;

	public SCIMResponseList(Collection<Object> list) {
		this.resources = list;
		this.totalResults = this.resources.size();
		this.itemsPerPage = this.resources.size();
		this.startIndex = 1;
	}

	public SCIMResponseList(Collection<Object> list, PaginationUtil p) {
		if (p.isActive()) {
			this.resources = list;
			this.totalResults = p.getTotalResults();
			this.itemsPerPage = p.getItemsPerPage();
			this.startIndex = p.getStartIndex();
		} else {
			this.resources = list;
			this.totalResults = this.resources.size();
			this.itemsPerPage = this.resources.size();
			this.startIndex = 1;
		}
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