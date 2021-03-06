package br.unifesp.maritaca.web.submodule;

import br.unifesp.maritaca.web.module.Module;
import br.unifesp.maritaca.web.utils.UtilsWeb;

public class SubModuleImpl implements SubModule {

	private String title;
	private String component;
	private Module parent;

	public SubModuleImpl() {
		setComponent("");
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getComponent() {
		return component;
	}
	
	public void setComponent(String component) {
		this.component = component;
	}
	
	@Override
	public Module getParent() {
		return parent;
	}
	
	@Override
	public void setParent(Module parent) {
		this.parent = parent;
	}
	
	@Override
	public String getId(){
		return UtilsWeb.getCompactedVersion(getComponent());
	}
	
	@Override
	public void setId(String id){
		//dumb function
	}

}
