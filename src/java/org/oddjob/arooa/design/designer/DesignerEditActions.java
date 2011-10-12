package org.oddjob.arooa.design.designer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.oddjob.arooa.design.DesignComponent;
import org.oddjob.arooa.design.actions.EditActionsContributor;
import org.oddjob.arooa.parsing.CutAndPasteSupport;

/**
 * Adds enabled/disabled functionality to the standard edit actions.
 * 
 * @author rob
 */
public class DesignerEditActions extends EditActionsContributor {

	public DesignerEditActions(final DesignerModel model) {		
		model.addPropertyChangeListener("currentComponent", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateActions(model);
			}
		});
		updateActions(model);
	}
	
	private void updateActions(DesignerModel model) {
		DesignComponent currentComponent = model.getCurrentComponent();
		
		if (currentComponent == null) {
			setCutEnabled(false);
			setCopyEnabled(false);
			setPasteEnabled(false);
			setDeleteEnabled(false);
		}
		else {
			if (currentComponent == model.getRootComponent()) {
				setCutEnabled(false);
				setDeleteEnabled(false);
			} else {
				setCutEnabled(true);
				setDeleteEnabled(true);
			}
			
			setCopyEnabled(true);
			
			CutAndPasteSupport cnpSupport = new CutAndPasteSupport(currentComponent.getArooaContext());
			
			if (cnpSupport.supportsPaste()) {
				setPasteEnabled(true);
			} else {
				setPasteEnabled(false);				
			}
		}
	}
	
}
