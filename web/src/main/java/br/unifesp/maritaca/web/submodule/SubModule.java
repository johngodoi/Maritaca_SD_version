package br.unifesp.maritaca.web.submodule;

import br.unifesp.maritaca.web.module.Module;

public interface SubModule {
	String getTitle();

	void setTitle(String title);
	
	String getComponent();
	
	void setComponent(String comp);

	Module getParent();

	void setParent(Module parent);

	String getId();

	void setId(String id);
}
