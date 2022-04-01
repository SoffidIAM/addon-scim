package com.soffid.iam.addon.scim2.crud;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.api.AsyncList;
import com.soffid.iam.api.CrudHandler;
import com.soffid.iam.api.OsType;
import com.soffid.iam.api.PagedResult;
import com.soffid.iam.bpm.service.ejb.BpmEngine;
import com.soffid.iam.service.ejb.NetworkService;

import es.caib.seycon.ng.exception.InternalErrorException;

public class CrudOsTypeHandler implements CrudHandler<OsType> {

	@Override
	public OsType create(OsType object) throws Exception {
		NetworkService engine = EJBLocator.getNetworkService();
		return engine.create(object);
	}

	@Override
	public PagedResult<OsType> read(String text, String filter, Integer start, Integer maxobjects)
			throws Exception {
		NetworkService engine = EJBLocator.getNetworkService();
		PagedResult<OsType> r = new PagedResult<>();
		r.setResources(engine.findAllOSTypes());
		r.setStartIndex(0);
		r.setTotalResults(r.getResources().size());
		r.setItemsPerPage(r.getResources().size());
		return r;
	}

	@Override
	public AsyncList<OsType> readAsync(String text, String filter) throws Exception {
		throw new InternalErrorException("Not supported");
	}

	@Override
	public OsType update(OsType object) throws Exception {
		NetworkService engine = EJBLocator.getNetworkService();
		engine.update(object);
		return object;
	}

	@Override
	public void delete(OsType object) throws Exception {
		NetworkService engine = EJBLocator.getNetworkService();
		engine.delete(object);
	}

}
