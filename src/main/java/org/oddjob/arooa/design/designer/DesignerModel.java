/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.DesignComponent;
import org.oddjob.arooa.reflect.ArooaPropertyException;

import java.beans.PropertyChangeListener;
import java.util.Observer;

/**
 * Model for a designer session.
 */
public interface DesignerModel  {

	DesignTreeModel getTreeModel();

	DesignComponent getRootComponent();

	void setCurrentSelection(DesignTreeNode node);

	DesignTreeNode getCurrentSelection();

	void replaceSelected(ArooaConfiguration config) throws ArooaParseException;

	DesignComponent getCurrentComponent();

	void viewSelectedAsXML() throws ArooaPropertyException;

	void addObserver(Observer observer);

	void addPropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);

	void addPropertyChangeListener(String property, PropertyChangeListener listener);

	void removePropertyChangeListener(String property, PropertyChangeListener listener);
}
