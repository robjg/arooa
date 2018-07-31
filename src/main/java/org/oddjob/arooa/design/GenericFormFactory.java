package org.oddjob.arooa.design;

import org.oddjob.arooa.design.screem.BorderedGroup;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.design.screem.NullForm;
import org.oddjob.arooa.design.screem.StandardForm;

/**
 * Utility class that creates a {@link Form} for a 
 * {@link GenericDesignInstance}
 * 
 * @author rob
 *
 */
class GenericFormFactory {

	static Form createForm(DesignInstanceBase designInstance) {
		
		if (designInstance.children().length == 0) {
			return new NullForm();
		}
		
		BorderedGroup group = new BorderedGroup("Properties");
		
		for (DesignProperty property: designInstance.children()) {
			FormItem item = property.view();
			
			if (item == null) {
				throw new NullPointerException("View null.");
			}
			
			group.add(item);
		}
		
		StandardForm form = new StandardForm(
				designInstance.tag().toString(), designInstance);
		
		form.addFormItem(group);
		
		return form;
	}
	
}
