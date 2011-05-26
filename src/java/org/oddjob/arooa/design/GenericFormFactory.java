package org.oddjob.arooa.design;

import org.oddjob.arooa.design.screem.BorderedGroup;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.design.screem.StandardForm;

class GenericFormFactory {

	static Form createForm(DesignInstanceBase designInstance) {
		
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
