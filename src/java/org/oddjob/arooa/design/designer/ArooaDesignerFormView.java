package org.oddjob.arooa.design.designer;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.oddjob.arooa.design.DesignNotifier;
import org.oddjob.arooa.design.actions.ActionContributor;
import org.oddjob.arooa.design.actions.ConfigurableMenus;
import org.oddjob.arooa.design.view.SwingFormView;
import org.oddjob.arooa.design.view.ViewHelper;
import org.oddjob.arooa.logging.Appender;
import org.oddjob.arooa.logging.LoggerAdapter;
import org.oddjob.arooa.logging.LogLevel;
import org.oddjob.arooa.logging.LoggingEvent;

/**
 * The Swing GUI designer dialogue for Oddjob.
 * 
 * @author Rob Gordon
 */

public class ArooaDesignerFormView 
implements SwingFormView {

	private final JComponent component;	
	
	private final Component cell;
	
	private final MenuProvider menus;
	
	/**
	 * Constructor
	 * 
	 * @param designerForm The underlying form.
	 */
	public ArooaDesignerFormView(ArooaDesignerForm designerForm) {
		this(designerForm, false);
	}
	
	/**
	 * Constructor.
	 *
	 */
	public ArooaDesignerFormView(ArooaDesignerForm designerForm,
			boolean noErrorDialg) {
		
		DesignNotifier designerNotifier = designerForm.getConfigHelper();		
		
		DesignerModel designerModel = new DesignerModel(designerNotifier);
		
		ConfigurableMenus menus = new ConfigurableMenus();	
		
		new DesignerEditActions(designerModel).contributeTo(menus);
		
		ActionContributor viewActions = new ViewActionsContributor(
				designerModel);
		viewActions.contributeTo(menus);
		
		this.menus = menus;
		
	    component = new DesignerPanel(designerModel, menus);

		viewActions.addKeyStrokes(component);
		
		cell = ViewHelper.createDetailButton(designerForm);

		if (!noErrorDialg) {
			final Appender errorListener = 
				new Appender() {
				
				@Override
				public void append(LoggingEvent event) {
	
					if (!event.getLevel().isLessThan(LogLevel.ERROR)) {
					
						JOptionPane.showMessageDialog(
								component, 
								event.getMessage(),
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}	
				}				
			};
			
			component.addAncestorListener(new AncestorListener() {
				public void ancestorAdded(AncestorEvent event) {
					LoggerAdapter
							.appenderAdapterFor("org.oddjob.arooa.design")
							.addAppender(errorListener);				
				}
				
				public void ancestorMoved(AncestorEvent event) {
				}
				
				public void ancestorRemoved(AncestorEvent event) {
					LoggerAdapter
							.appenderAdapterFor("org.oddjob.arooa.design")
							.removeAppender(errorListener);
				}
			});
		}	
		
	}

	public Component cell() {
		return cell;
	}
	
	public Component dialog() {
		return component;
	}
	
	public MenuProvider getMenus() {
		return menus;
	}
}
