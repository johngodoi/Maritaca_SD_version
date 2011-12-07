package br.unifesp.maritaca.web;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.unifesp.maritaca.web.module.Module;
import br.unifesp.maritaca.web.module.ModuleImpl;
import br.unifesp.maritaca.web.submodule.SubModule;
import br.unifesp.maritaca.web.submodule.SubModuleImpl;

@ManagedBean
@SessionScoped
public class Manager {
	private List<Module> enabledModules;
	private Module activeModule;
	
	public Manager() {
		setEnabledModules(new ArrayList<Module>());		

		Module mod = new ModuleImpl();
		mod.setTitle("Forms");
		SubModule submod = new SubModuleImpl();
		submod.setTitle("MyForms");
		submod.setComponent("listForms");
		mod.addModule(submod);
		setActiveModule(mod);
		mod.setActiveSubModule(submod);
		
		submod = new SubModuleImpl();
		submod.setTitle("Editor");
		submod.setComponent("formEditor");
		mod.addModule(submod);
		
		submod = new SubModuleImpl();
		submod.setTitle("NewForm");
		submod.setComponent("newForm");
		mod.addModule(submod);
		
		submod = new SubModuleImpl();
		submod.setTitle("ViewForm");
		submod.setComponent("viewForm");
		mod.addModule(submod);
		
		getEnabledModules().add(mod);
		
		mod = new ModuleImpl();
		mod.setTitle("Answer");
		submod = new SubModuleImpl();
		submod.setTitle("MyAnswers");
		submod.setComponent("listAnswers");
		mod.addModule(submod);
		
		submod = new SubModuleImpl();
		submod.setTitle("Newanswer");
		submod.setComponent("newAnswer");
		mod.addModule(submod);
		
		getEnabledModules().add(mod);
				
	}

	public List<Module> getEnabledModules() {
		return enabledModules;
	}

	public void setEnabledModules(List<Module> enabledModules) {
		this.enabledModules = enabledModules;
	}

	public Module getActiveModule() {
		return activeModule;
	}

	public void setActiveModule(Module activeModule) {
		this.activeModule = activeModule;
	}
	
	
}
