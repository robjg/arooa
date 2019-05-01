package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.design.DesignNotifier;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.OkAware;
import org.oddjob.arooa.design.view.SwingFormFactory;
import org.oddjob.arooa.design.view.SwingFormView;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * The Designer dialogue form.
 * 
 * @author rob
 *
 */
public class ArooaDesignerForm implements Form, OkAware {

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

	private final Callable<Boolean> okAction;

	/**
	 * Constructor.
	 * 
	 * @param title The title.
	 * @param designNotifier Will be notified when design changes.
	 * @param okAction Action when ok is pressed.
	 */
	public ArooaDesignerForm(String title,
							 DesignNotifier designNotifier,
							 Callable<Boolean> okAction) {
		this.title = title;
		this.configHelper = designNotifier;
		this.okAction = okAction;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param designNotifier Will be notified when design changes.
	 * @param okAction Action when ok is pressed.
	 */
	public ArooaDesignerForm(DesignNotifier designNotifier,
							 Callable<Boolean> okAction) {
		this(null, designNotifier, okAction);
	}

	/**
	 * Constructor.
	 *
	 * @param designNotifier Will be notified when design changes.
	 */
	public ArooaDesignerForm(DesignNotifier designNotifier) {
		this(null, designNotifier, null);
	}

	public String getTitle() {
		return title;
	}
	
	public DesignNotifier getConfigHelper() {
		return configHelper;
	}

	@Override
	public Callable<Boolean> getOkAction() {
		return Optional.ofNullable(this.okAction)
				.orElse(() -> true);
	}
}
