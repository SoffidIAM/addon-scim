package com.soffid.iam.addon.scim2.crud;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.api.AsyncList;
import com.soffid.iam.api.CrudHandler;
import com.soffid.iam.api.PagedResult;
import com.soffid.iam.bpm.api.ProcessDefinition;
import com.soffid.iam.bpm.service.ejb.BpmEngine;

import es.caib.seycon.ng.exception.InternalErrorException;

public class CrudProcessDefinitionHandler implements CrudHandler<ProcessDefinition> {

	@Override
	public ProcessDefinition create(ProcessDefinition object) throws Exception {
		throw new InternalErrorException("Not supported");
	}

	@Override
	public PagedResult<ProcessDefinition> read(String text, String filter, Integer start, Integer maxobjects)
			throws Exception {
		BpmEngine engine = EJBLocator.getBpmEngine();
		return engine.findProcessDefinitionByTextAndJsonQuery(text, filter, start, maxobjects);
	}

	@Override
	public AsyncList<ProcessDefinition> readAsync(String text, String filter) throws Exception {
		throw new InternalErrorException("Not supported");
	}

	@Override
	public ProcessDefinition update(ProcessDefinition object) throws Exception {
		throw new InternalErrorException("Not supported");
	}

	@Override
	public void delete(ProcessDefinition object) throws Exception {
		throw new InternalErrorException("Not supported");
	}

}
