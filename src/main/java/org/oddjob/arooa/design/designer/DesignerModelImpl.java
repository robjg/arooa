/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.*;
import org.oddjob.arooa.design.etc.UnknownComponent;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Observable;

/**
 * Model for a designer session.
 */
public class DesignerModelImpl extends Observable implements DesignerModel {
	private static final Logger logger = LoggerFactory.getLogger(DesignerModelImpl.class);

	/** The tree model */
	private final DesignTreeModelImpl treeModel;

	/** The root component */
	private DesignComponent rootComponent;

	/** The current selected node */
	private DesignTreeNode currentSelection;

	private final PropertyChangeSupport propertySupport =
		new PropertyChangeSupport(this);

	/**
	 * Constructor.
	 *
	 * @param designNotifier The thing that tells us about the root {@link DesignInstance}.
	 */
	public DesignerModelImpl(
			DesignNotifier designNotifier) {

		treeModel = new DesignTreeModelImpl();

		designNotifier.addDesignListener(new DesignListener() {
			public void childAdded(DesignStructureEvent event) {

				final DesignInstance newDoc = event.getChild();
				if (newDoc instanceof DesignComponent) {
					rootComponent = (DesignComponent) event.getChild();
				}
				else {
					rootComponent = new DesignComponent() {
						public void addStructuralListener(DesignListener listener) {}

						public Form detail() {
							return newDoc.detail();
						}

						public ArooaElement element() {
							return newDoc.element();
						}

						public ArooaContext getArooaContext() {
							return newDoc.getArooaContext();
						}

						public void removeStructuralListener(DesignListener listener) {}

						@Override
						public String toString() {
							return newDoc.toString();
						}
						public String getId() {
							throw new UnsupportedOperationException();
						}

						public void setId(String id) {
							throw new UnsupportedOperationException();
						}
					};
				}
				treeModel.setRoot(rootComponent);
			}

			public void childRemoved(DesignStructureEvent event) {
			}
		});

	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(property, listener);
	}

	public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(property, listener);
	}


	/**
	 * Get the root component.
	 * @return The DesignComponent which is the root.
	 */
	public DesignComponent getRootComponent() {
		return rootComponent;
	}

	/**
	 * Get the tree model.
	 *
	 * @return A TreeModel.
	 */
	@Override
	public DesignTreeModel getTreeModel() {
		return treeModel;
	}

	/**
	 * Set the currently selected node.
	 *
	 * @param node The node. May be null.
	 */
	public void setCurrentSelection(DesignTreeNode node) {
		DesignComponent oldComponent = null;
		if (this.currentSelection != null) {
			oldComponent = this.currentSelection.getDesignComponent();
		}

		this.currentSelection = node;
		logger.debug("Current selection is [" + currentSelection + "]");

		propertySupport.firePropertyChange("currentComponent",
				oldComponent,
				getCurrentComponent());

		setChanged();
		notifyObservers();
	}

	/**
	 * Get the currently selected node.
	 *
	 * @return The currently selected node. Null if none.
	 */
	public DesignTreeNode getCurrentSelection() {
		return currentSelection;
	}

	public DesignComponent getCurrentComponent() {
		if (currentSelection == null) {
			return null;
		}
		return currentSelection.getDesignComponent();
	}

    /**
     * Helper to get the configuration of the current selection as XML.
	 *
	 * @return The XML.
	 */
	String getCurrentXML() {
		if (currentSelection == null) {
			throw new IllegalStateException("No Current Selection.");
		}

		DesignComponent component = currentSelection.getDesignComponent();

		XMLArooaParser parser = new XMLArooaParser(
				component.getArooaContext().getPrefixMappings());

		ArooaConfiguration config =
			component.getArooaContext().getConfigurationNode();

		try {
			parser.parse(config);
		}
		catch (ArooaParseException e) {
			throw new RuntimeException(e);
		}
		return parser.getXml();

	}

	public void replaceSelected(ArooaConfiguration config) throws ArooaParseException {

		DesignTreeNode parentNode = currentSelection.getParent();
		int index = -1;

		if (parentNode != null) {

			index = parentNode.getIndex(currentSelection);

			if (index < 0) {
				throw new IllegalStateException("Current selection [" +
						currentSelection + "] is no longer a valid child.");
			}
		}

		ArooaContext currentContext = currentSelection.getDesignComponent().getArooaContext();

		CutAndPasteSupport.replace(
				currentContext.getParent(),
				currentContext,
				config);


		if (parentNode != null) {
			setCurrentSelection(parentNode.getChildAt(index));
		}
		else {
			setCurrentSelection(treeModel.getRoot());
		}
	}

	public void viewSelectedAsXML()
	throws ArooaPropertyException {

		DesignTreeNode parentNode = currentSelection.getParent();
		int index = -1;

		if (parentNode != null) {

			index = parentNode.getIndex(currentSelection);

			if (index < 0) {
				throw new IllegalStateException("Current selection [" +
						currentSelection + "] is no longer a valid child.");
			}
		}

		DesignComponent currentComponent = currentSelection.getDesignComponent();

		ArooaContext currentContext = currentComponent.getArooaContext();

		ArooaContext parentContext = currentContext.getParent();

		// Get XML now because cut looses current selection.
		String xml = getCurrentXML();

 		CutAndPasteSupport.cut(parentContext, currentContext);

		UnknownComponent unknown = new UnknownComponent(
				currentComponent.element(),
				parentContext);

 		parentContext.getConfigurationNode().setInsertPosition(index);

 		parentContext.getConfigurationNode().insertChild(
 					unknown.getArooaContext().getConfigurationNode());

		unknown.getArooaContext().getRuntime().init();

		// Init resets xml so this must be done here.
		unknown.setXml(xml);

		if (parentNode != null) {

			setCurrentSelection(parentNode.getChildAt(index));
		}
		else {
			// Note that we can't rely on the DesignNotifer because
			// we're not using our parent context to create the unknown.
			
			rootComponent = unknown;
			treeModel.setRoot(unknown);
			setCurrentSelection(treeModel.getRoot());
		}
 				
	}
	
	public void delete(DesignTreeNode child) {
		if (child == null) {
			throw new NullPointerException("Child can not be null!");
		}
		
		DesignTreeNode parent = child.getParent();
		
		CutAndPasteSupport cutAndPaste = new CutAndPasteSupport(
				parent.getDesignComponent().getArooaContext());
		
		cutAndPaste.cut(child.getDesignComponent().getArooaContext());
		
	}

	public static int getIndex(DesignTreeNode child) {
		if (child == null) {
			throw new NullPointerException("Child can't be null!");
		}
		return child.getParent().getIndex(child);
	}
}
