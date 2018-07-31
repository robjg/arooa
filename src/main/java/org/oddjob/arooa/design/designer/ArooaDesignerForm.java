package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.design.DesignNotifier;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.view.SwingFormFactory;
import org.oddjob.arooa.design.view.SwingFormView;

/**
 * The Designer dialogue form.
 * 
 * @author rob
 *
 */
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
	
	/**
	 * Constructor.
	 * 
	 * @param title The title.
	 * @param designNotifier Will be notifier when design changes.
	 */
	public ArooaDesignerForm(String title, DesignNotifier designNotifier) {
		this.title = title;
		this.configHelper = designNotifier;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param designNotfier
	 */
	public ArooaDesignerForm(DesignNotifier designNotfier) {
		this(null, designNotfier);
	}
	
	public String getTitle() {
		return title;
	}
	
	public DesignNotifier getConfigHelper() {
		return configHelper;
	}
}
