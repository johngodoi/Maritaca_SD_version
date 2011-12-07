package br.unifesp.maritaca.web.module;

import java.util.Set;

import br.unifesp.maritaca.web.submodule.SubModule;

public interface Module {
	String getTitle();

	void setTitle(String title);

	Set<SubModule> getSubModules();
	
	void setSubModules(Set<SubModule> subModules);
	
	SubModule getActiveSubModule();

	void setActiveSubModule(SubModule mod);

	void addModule(SubModule submod);

	SubModule searchSubModule(String compName);

	void setActiveSubModule(String compName);
}
