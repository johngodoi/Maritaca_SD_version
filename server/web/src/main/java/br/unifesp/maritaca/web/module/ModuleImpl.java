package br.unifesp.maritaca.web.module;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import br.unifesp.maritaca.web.submodule.SubModule;
import br.unifesp.maritaca.web.utils.StringProcessing;

public class ModuleImpl implements Module {

	private String title;
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
		return StringProcessing.getCompactedVersion(getTitle());
	}

	@Override
	public void setId(String id) {
		// dumb function
	}

	@Override
	public void processAction(ActionEvent event)
			throws AbortProcessingException {
		setActiveSubModuleByString(event.getComponent().getId());
	}
}
