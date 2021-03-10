package com.soffid.iam.addon.scim2.crud;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.api.AsyncList;
import com.soffid.iam.api.CrudHandler;
import com.soffid.iam.api.PagedResult;
import com.soffid.iam.bpm.api.ProcessDefinition;
import com.soffid.iam.bpm.api.ProcessInstance;
import com.soffid.iam.bpm.service.ejb.BpmEngine;

import es.caib.seycon.ng.exception.InternalErrorException;

public class CrudProcessInstanceHandler implements CrudHandler<ProcessInstance> {

	@Override
	public ProcessInstance create(ProcessInstance object) throws Exception {
		Long defId = object.getProcessDefinition();
		if (defId == null)
			throw new InternalErrorException("Missing process definition id");
		BpmEngine engine = EJBLocator.getBpmEngine();
		for (ProcessDefinition def: engine.findInitiatorProcessDefinitions()) {
			if (def.getId() == defId.longValue()) {
				ProcessInstance proc = engine.newProcess(def);
				proc.setVariables(object.getVariables());
				proc.setComments(object.getComments());
				engine.update(proc);
				return proc;
			}
		}
		throw new InternalErrorException("Canot find definition id "+defId);
	}

	@Override
	public PagedResult<ProcessInstance> read(String text, String filter, Integer start, Integer maxobjects)
			throws Exception {
		BpmEngine engine = EJBLocator.getBpmEngine();
		return engine.findProcessInstanceByTextAndJsonQuery(text, filter, start, maxobjects);
	}

	@Override
	public AsyncList<ProcessInstance> readAsync(String text, String filter) throws Exception {
		throw new InternalErrorException("Not supported");
	}

	@Override
	public ProcessInstance update(ProcessInstance object) throws Exception {
		BpmEngine engine = EJBLocator.getBpmEngine();
		engine.update(object);
		return object;
	}

	@Override
	public void delete(ProcessInstance object) throws Exception {
		BpmEngine engine = EJBLocator.getBpmEngine();
		engine.cancel(object);
	}

}
