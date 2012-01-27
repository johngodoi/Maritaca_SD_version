package br.unifesp.maritaca.web.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import br.unifesp.maritaca.web.submodule.SubModule;
import br.unifesp.maritaca.web.utils.Utils;

public class ModuleImpl implements Module, Serializable {

	private static final long serialVersionUID = 1L;
	
	private String title;
	private String id;
	private SubModule activeSubModule;
	private List<SubModule> subModules;

	public ModuleImpl() {
		subModules = new ArrayList<SubModule>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<SubModule> getSubModules() {
		return subModules;
	}

	public void setSubModules(List<SubModule> subModules) {
		this.subModules = subModules;
	}

	@Override
	public SubModule getActiveSubModule() {
		if (activeSubModule == null) {
			if (getSubModules() != null) {
				return getSubModules().get(0);
			}
		}
		return activeSubModule;
	}

	@Override
	public void setActiveSubModule(SubModule submod) {
		if (submod != null) {
			activeSubModule = submod;
		}
	}

	@Override
	public void addModule(SubModule submod) {
		submod.setParent(this);
		getSubModules().add(submod);
	}

	@Override
	public SubModule searchSubModule(String submodId) {
		if (submodId != null) {
			for (SubModule sm : getSubModules()) {
				if (sm.getId().equals(submodId)) {
					return sm;
				}
			}
		}
		return null;
	}

	@Override
	public void setActiveSubModuleByString(String submodId) {
		setActiveSubModule(searchSubModule(submodId));
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void processAction(ActionEvent event)
			throws AbortProcessingException {
		setActiveSubModuleByString(event.getComponent().getId());
	}
}
