package com.soffid.iam.addon.scim2.crud;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.api.AsyncList;
import com.soffid.iam.api.CrudHandler;
import com.soffid.iam.api.PagedResult;
import com.soffid.iam.bpm.api.ProcessDefinition;
import com.soffid.iam.bpm.api.TaskInstance;
import com.soffid.iam.bpm.service.ejb.BpmEngine;

import es.caib.seycon.ng.exception.InternalErrorException;

public class CrudTaskInstanceHandler implements CrudHandler<TaskInstance> {

	@Override
	public TaskInstance create(TaskInstance object) throws Exception {
		throw new InternalErrorException("Not supported");
	}

	@Override
	public PagedResult<TaskInstance> read(String text, String filter, Integer start, Integer maxobjects)
			throws Exception {
		BpmEngine engine = EJBLocator.getBpmEngine();
		return engine.findTasksByTextAndJsonQuery(text, filter, start, maxobjects);
	}

	@Override
	public AsyncList<TaskInstance> readAsync(String text, String filter) throws Exception {
		throw new InternalErrorException("Not supported");
	}

	@Override
	public TaskInstance update(TaskInstance object) throws Exception {
		BpmEngine engine = EJBLocator.getBpmEngine();
		engine.update(object);
		return object;
	}

	@Override
	public void delete(TaskInstance object) throws Exception {
		BpmEngine engine = EJBLocator.getBpmEngine();
		engine.cancel(object);
	}

}
