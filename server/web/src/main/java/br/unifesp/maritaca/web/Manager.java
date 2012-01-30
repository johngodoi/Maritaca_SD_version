package br.unifesp.maritaca.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import org.richfaces.component.UIPanelMenu;
import org.richfaces.event.ItemChangeEvent;
import org.richfaces.event.ItemChangeListener;

import br.unifesp.maritaca.web.module.Module;
import br.unifesp.maritaca.web.module.ModuleImpl;
import br.unifesp.maritaca.web.submodule.SubModule;
import br.unifesp.maritaca.web.submodule.SubModuleImpl;

@ManagedBean
@SessionScoped
public class Manager implements ItemChangeListener, Serializable {
	private static final long serialVersionUID = 1L;
	private List<Module> enabledModules;
	private Module activeModule;

	public Manager() {
		super();
		setEnabledModules(new ArrayList<Module>());

		Module mod = new ModuleImpl();
		mod.setTitle("Forms");
		mod.setId("forms");
		SubModule submod = new SubModuleImpl();
		submod.setTitle("My Forms");
		submod.setComponent("listForms");
		mod.addModule(submod);
		setActiveModule(mod);
		mod.setActiveSubModule(submod);

		submod = new SubModuleImpl();
		submod.setTitle("Editor");
		submod.setComponent("formEditor");
		mod.addModule(submod);

		getEnabledModules().add(mod);

		mod = new ModuleImpl();
		mod.setTitle("Answer");
		mod.setId("answer");
		submod = new SubModuleImpl();
		submod.setTitle("My Answers");
		submod.setComponent("listAnswers");
		mod.addModule(submod);

		submod = new SubModuleImpl();
		submod.setTitle("New Answer");
		submod.setComponent("newAnswer");
		mod.addModule(submod);

		getEnabledModules().add(mod);

		mod = new ModuleImpl();
		mod.setTitle("Groups");
		mod.setId("groups");
		submod = new SubModuleImpl();
		submod.setTitle("Create Group");
		submod.setComponent("createGroup");
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
		if (activeModule != null) {
			this.activeModule = activeModule;
		}
	}

	public void setActiveModuleByString(String modId) {
		setActiveModule(searchModuleByString(modId));
	}

	private Module searchModuleByString(String modId) {
		if (modId != null) {
			for (Module mod : getEnabledModules()) {
				if (mod.getId().equals(modId)) {
					return mod;
				}
			}
		}
		return null;
	}

	@Override
	public void processItemChange(ItemChangeEvent event)
			throws AbortProcessingException {
		setActiveModuleByString(event.getNewItem().getId());
//		FacesContext fc = FacesContext.getCurrentInstance();

//		UIViewRoot uivr = fc.getViewRoot();
//		printtree(uivr, "|");
//		
//		UITabPanel mainTab = (UITabPanel) uivr.findComponent("mainForm:mainTab");
//		mainTab.getChildren().clear();
//		
//		printtree(uivr, "|");
	}

	public void activeModAndSub(String mod, String submod) {
		setActiveModuleByString(mod);
		getActiveModule().setActiveSubModuleByString(submod);

		FacesContext fc = FacesContext.getCurrentInstance();

		UIViewRoot uivr = fc.getViewRoot();
		UIPanelMenu leftMenu = (UIPanelMenu) uivr.findComponent("mainForm:"
				+ getActiveModule().getId() + "_panelLeftMenu");
		if (leftMenu != null) {
			leftMenu.setActiveItem(getActiveModule().getActiveSubModule()
					.getComponent());
		}
	}

	// /**
	// * find a UIComponent from a Parent
	// * @param id of component
	// * @param parent UIComponent
	// * @return UIComponent or null
	// * Temporary function, uiviewroot.findComponent not working...
	// */
	// private UIComponent findComponent(String id, UIComponent parent) {
	// if (parent == null)
	// return null;
	//
	// if (parent.getId().equals(id)) {
	// return parent;
	// } else {
	// for (UIComponent child : parent.getChildren()) {
	// UIComponent uic = findComponent(id, child);
	// if(uic!=null)return uic;
	// }
	// return null;
	// }
	// }

	public String getTime() {
		return Calendar.getInstance().getTimeInMillis() + "";
	}

	public void setTime(String x) {
	}
	
	/**
	 * testing function for development
	 * @param cpnt
	 * @param level
	 */
	@SuppressWarnings("unused")
	private void printtree(UIComponent cpnt, String level){
		if(cpnt == null)return;
		System.out.println(level + " " + cpnt.getId() + " - " + cpnt.getClass().getSimpleName());
		for(UIComponent uichild: cpnt.getChildren()){
			printtree(uichild, level + "-");
		}
		
	}
}
