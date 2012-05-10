package org.oddjob.arooa.design.screem;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DynamicDesignInstance;
import org.oddjob.arooa.design.InstanceSupport;
import org.oddjob.arooa.design.view.BeanFormView;
import org.oddjob.arooa.design.view.SwingFormFactory;
import org.oddjob.arooa.design.view.SwingFormView;
import org.oddjob.arooa.types.BeanType;

/**
 * The form used by a {@link BeanType}. This form consists of a field
 * for the class name and a sub form that is the properties of the 
 * given class or a blank message form if the class is not found.
 * 
 * @author rob
 *
 */
public class BeanForm 
implements Form {

	/**
	 * Register the sub forms.
	 */
	static {
		SwingFormFactory.register(ClassNotFoundForm.class, 
				new SwingFormFactory<ClassNotFoundForm>() {
			@Override
			public SwingFormView onCreate(ClassNotFoundForm form) {
				return new BeanFormView.ClassNotFoundView(form);
			}
		}); 
		SwingFormFactory.register(PropertiesForm.class, 
				new SwingFormFactory<PropertiesForm>() {
			@Override
			public SwingFormView onCreate(PropertiesForm form) {
				return new BeanFormView.PropertiesView(form);
			}
		}); 
	}
	
	/** This form notifies when the subform changes. */
	private final PropertyChangeSupport propertyChangeSupport = 
			new PropertyChangeSupport(this);

	public static final String SUBFORM_PROPERTY = "subForm";

	/** The design instance creating the form. */
	private final DynamicDesignInstance design;
	
	/** The form title. */
	private final String title;
	
	/** The sub form. */
	private Form subForm;

	/**
	 * Constructor.
	 * 
	 * @param design The instance this form belongs to.
	 */
	public BeanForm(DynamicDesignInstance design) {
		this.design = design;
		this.title = InstanceSupport.tagFor(design).toString();
		createSubForm();
	}
	
	/**
	 * Add a property listener. For sub form change.
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public synchronized void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Remove a property listener.
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public synchronized void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	/**
	 * Fire property change events.
	 * 
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	protected void fire(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}

	/**
	 * Get the design instance that created this form.
	 * 
	 * @return
	 */
	public DynamicDesignInstance getDesign() {
		return design;
	}
	
	/**
	 * Set the class name. Called by the view to change the sub form.
	 * 
	 * @param className
	 */
	public void setClassName(String className) {
		design.setClassName(className);
		createSubForm();
	}

	/**
	 * Internal method to create the sub form based on the class name.
	 */
	private void createSubForm() {
		String className = design.getClassName();

		if (className == null || className.trim().length() == 0) {
			setSubForm(new ClassNotFoundForm("No Class Name"));
		}
		else {			
			DesignProperty[] subDesign = design.getBeanProperties();
				
			if (subDesign == null) {
				setSubForm(new ClassNotFoundForm(
						"Class Not Found " + className));
			}
			else {
				Form subForm = new PropertiesForm(subDesign);
				setSubForm(subForm);
			}
		}
	}
	
	/**
	 * Set the sub form and fire a property change event.
	 * 
	 * @param subForm
	 */
	public void setSubForm(Form subForm) {
		Form oldSubForm = this.subForm;
		this.subForm = subForm;
		fire(SUBFORM_PROPERTY, oldSubForm, subForm);
	}
	
	/**
	 * Getter for the sub form.
	 * @return
	 */
	public Form getSubForm() {
		return subForm;
	}

	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * Form for when the class is missing or not found.
	 * 
	 */
	public static class ClassNotFoundForm implements Form {
		
		private final String message;
		
		public ClassNotFoundForm(String message) {
			this.message = message;
		}
		
		@Override
		public String getTitle() {
			return "Class Not Found";
		}
		
		public String getMessage() {
			return message;
		}
	}
	
	/**
	 * Form for the properties of the class when it is found.
	 * 
	 */
	public static class PropertiesForm implements Form {
		
		private final FormItem[] items;
		
		public PropertiesForm(DesignProperty[] properties) {
			items = new FormItem[properties.length];
			for (int i = 0; i < items.length; ++i) {
				items[i] = properties[i].view();
			}
		}
		
		@Override
		public String getTitle() {
			return "Properties Form";
		}

		public int size() {
			return items.length;
		}
		
		
		public FormItem getFormItem(int index) {
			return items[index];
		}
		
	}
}
