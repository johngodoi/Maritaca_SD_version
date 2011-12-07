package br.unifesp.maritaca.web.module;

import java.util.HashSet;
import java.util.Set;

import br.unifesp.maritaca.web.submodule.SubModule;

public class ModuleImpl implements Module {

	private String title;
	private SubModule activeSubModule;

	private Set<SubModule> subModules;
	
	public ModuleImpl() {
		subModules = new HashSet<SubModule>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Set<SubModule> getSubModules() {
		return subModules;
	}

	public void setSubModules(Set<SubModule> subModules) {
		this.subModules = subModules;
	}

	@Override
	public SubModule getActiveSubModule() {
		return activeSubModule;
	}

	@Override
	public void setActiveSubModule(SubModule mod) {
		activeSubModule = mod;
	}

	@Override
	public void addModule(SubModule submod) {
		submod.setParent(this);
		getSubModules().add(submod);
	}
	
	@Override
	public SubModule searchSubModule(String compName){
		for(SubModule sm : getSubModules()){
			if(sm.getComponent().equals(compName)){
				return sm;
			}
		}
		return null;
	}
	
	@Override
	public void setActiveSubModule(String compName){
		setActiveSubModule(searchSubModule(compName));
	}

}
