package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.DesignComponent;
import org.oddjob.arooa.design.Unknown;
import org.oddjob.arooa.design.actions.*;
import org.oddjob.arooa.design.etc.UnknownInstance;
import org.oddjob.arooa.parsing.NamespaceMappings;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Provides the designers 'View As XML' and 'View As Component' actions.
 * 
 * @author rob
 *
 */
public class ViewActionsContributor implements ActionContributor {

	private static final Logger logger = LoggerFactory.getLogger(ViewActionsContributor.class);
	
	public static final String VIEW_MENU_ID = "view";
	
	public static final String VIEW_GROUP = "view";

	private final ArooaAction viewAction = new AbstractArooaAction() {
		private static final long serialVersionUID = 2008121800L;

		{
			putValue(NAME, "Toggle View");
			
			putValue(ACCELERATOR_KEY, 
					KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));			
		}
		
		public void actionPerformed(ActionEvent e) {
			delegate.actionPerformed(e);
		}
	};

	private ActionListener delegate;
	
	public ViewActionsContributor(final DesignerModel model) {
		model.addPropertyChangeListener("currentComponent",
				evt -> updateActions(model));
		updateActions(model);
	}
	
	public void contributeTo(ActionRegistry actionRegistry) {
		
		actionRegistry.addMainMenu(
				new ActionMenu(
						VIEW_MENU_ID, 
						"View",
						KeyEvent.VK_V));
		
		actionRegistry.addMenuItem(VIEW_MENU_ID, VIEW_GROUP, viewAction);
		
		actionRegistry.addContextMenuItem(VIEW_GROUP, viewAction);
	}
	
	private void updateActions(final DesignerModel model) {

		DesignComponent currentComponent = model.getCurrentComponent();


		if (currentComponent == null) {
			viewAction.setEnabled(false);
		}
		else {
			viewAction.setEnabled(true);

			if (currentComponent instanceof Unknown) {
				
				viewAction.putValue(Action.NAME, "Component View");
				
				delegate = e -> {

					UnknownInstance unknown = (UnknownInstance) model.getCurrentSelection().getDesignComponent();
					String xml = unknown.getXml();

					NamespaceMappings namespaceMappings =
							currentComponent.getArooaContext().getPrefixMappings();

					// check the xml.
					try {
						new XMLArooaParser(namespaceMappings)
								.parse(unknown.getArooaContext().getConfigurationNode());
					} catch (ArooaParseException ex) {

						logger.error("Failed to parse XML: " + ex.getMessage(),
								ex);

						return;
					}

					try {
						model.replaceSelected(new XMLConfiguration("XML", xml));
					} catch (ArooaParseException ex) {

						logger.error("Failed to Create Component Tree: " + ex.getMessage(),
								ex);
					}

				};
			}
			else {
				
				viewAction.putValue(Action.NAME, "XML View");
				
				delegate = e -> {

					try {
						model.viewSelectedAsXML();
					} catch (Exception ex) {
						logger.error("Failed to view XML: " + ex.getMessage(), ex);
					}
				};
			}
			
		}
	}
	
	public void addKeyStrokes(JComponent component) {

		ActionMap actionMap = component.getActionMap();
		InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		String name = (String) viewAction.getValue(Action.NAME);
		actionMap.put(name, viewAction);
		KeyStroke keyStroke = (KeyStroke) viewAction.getValue(Action.ACCELERATOR_KEY);
			
		inputMap.put(keyStroke, name);
	}
	
}
