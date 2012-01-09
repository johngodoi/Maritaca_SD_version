package br.unifesp.maritaca.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AbortProcessingException;

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
        submod = new SubModuleImpl();
        submod.setTitle("My Answers");
        submod.setComponent("listAnswers");
        mod.addModule(submod);

        submod = new SubModuleImpl();
        submod.setTitle("New Answer");
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
    }
    
    public String getTime(){
    	return Calendar.getInstance().getTimeInMillis() + "";
    }
    
    public void setTime(String x){}
}
