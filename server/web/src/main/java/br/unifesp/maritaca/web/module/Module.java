package br.unifesp.maritaca.web.module;

import java.util.Set;

import javax.faces.event.ActionListener;

import br.unifesp.maritaca.web.submodule.SubModule;

public interface Module extends ActionListener{
	String getTitle();

	void setTitle(String title);

	Set<SubModule> getSubModules();
	
	void setSubModules(Set<SubModule> subModules);
	
	SubModule getActiveSubModule();

	void setActiveSubModule(SubModule mod);

	void addModule(SubModule submod);

	SubModule searchSubModule(String compName);

	void setId(String id);

	String getId();

	void setActiveSubModuleByString(String compName);
}
