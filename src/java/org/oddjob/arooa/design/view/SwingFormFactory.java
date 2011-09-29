/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.view;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.arooa.design.screem.FileSelection;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.NullForm;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.design.screem.TextInput;
import org.oddjob.arooa.design.screem.TextPseudoForm;

/**
 * Creates {@link SwingFormView}s from {@link Form}s
 */
abstract public class SwingFormFactory<T extends Form> {

	static class Mapping {

		private final Map<Class<? extends Form>, SwingFormFactory<? extends Form>> factories = 
			new HashMap<Class<? extends Form>, SwingFormFactory<? extends Form>>(); 
		
		
		@SuppressWarnings("unchecked")
		<X extends Form> SwingFormFactory<X > get(Class<X> forClass) {
			return (SwingFormFactory<X>) factories.get(forClass);
		}
		
		<X extends Form> void put(Class<X> forClass, SwingFormFactory<X> factory) {
			factories.put(forClass, factory);
		}
	}
	
	
	private static final Mapping FACTORIES = 
		new Mapping(); 
	
	static {

		FACTORIES.put(StandardForm.class, 
				new SwingFormFactory<StandardForm>() {
			public SwingFormView onCreate(StandardForm form) {
				return new StandardFormView(form);
			}	
		});
		
		FACTORIES.put(NullForm.class, 
				new SwingFormFactory<NullForm>() {
			public SwingFormView onCreate(NullForm form) {
				return new NullFormView();
			}	
		});
	
		FACTORIES.put(TextPseudoForm.class, 
				new SwingFormFactory<TextPseudoForm>() {
			public SwingFormView onCreate(TextPseudoForm form) {
				return new TextPsudoFormView(form);
			}	
		});
	
		FACTORIES.put(TextInput.class, 
				new SwingFormFactory<TextInput>() {
			public SwingFormView onCreate(TextInput form) {
				return new TextInputView(form);
			}	
		});
		
		FACTORIES.put(FileSelection.class, 
				new SwingFormFactory<FileSelection>() {
			public SwingFormView onCreate(FileSelection form) {
				return new FileSelectionView(form);
			}	
		});
	}
	
	public abstract SwingFormView onCreate(T form);

	public static <Y extends Form> void register(Class<Y> cl, SwingFormFactory<Y> factory) {
		FACTORIES.put(cl, factory);
	}
	
	/**
	 * Create a {@link SwingFormView} from a {@Form}.
	 * 
	 * @param <Y> The type of Form.
	 * @param form The form.
	 * 
	 * @return A SwingFormView. Never null.
	 */
	@SuppressWarnings("unchecked")
	public static <Y extends Form> SwingFormView create(Y form) {
		Class<Y> cl = (Class<Y>) form.getClass();
		SwingFormFactory<Y> factory = FACTORIES.get(cl);
		if (factory == null) {
			throw new NullPointerException("No SwingFormFactory for " + 
					cl.getName());
		}
		
		return factory.onCreate(form);
	}
}
