package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.design.DesignNotifier;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.view.SwingFormFactory;
import org.oddjob.arooa.design.view.SwingFormView;

public class ArooaDesignerForm implements Form {

	static {
		
		SwingFormFactory.register(ArooaDesignerForm.class, 
				new SwingFormFactory<ArooaDesignerForm>() {
			public SwingFormView onCreate(ArooaDesignerForm form) {
				return new ArooaDesignerFormView(form);
			}
		});
	}
	
	private final String title;
	
	private final DesignNotifier configHelper;
	
	public ArooaDesignerForm(String title, DesignNotifier configHelper) {
		this.title = title;
		this.configHelper = configHelper;
	}
	
	public ArooaDesignerForm(DesignNotifier configHelper) {
		this(null, configHelper);
	}
	
	public String getTitle() {
		return title;
	}
	
	public DesignNotifier getConfigHelper() {
		return configHelper;
	}
}
