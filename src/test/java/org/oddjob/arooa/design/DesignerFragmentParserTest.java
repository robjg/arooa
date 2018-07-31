package org.oddjob.arooa.design;

import org.junit.Test;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.junit.Assert;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.view.SwingFormFactory;
import org.oddjob.arooa.design.view.SwingFormView;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ElementConfiguration;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaDescriptor;
import org.oddjob.arooa.standard.StandardTools;

public class DesignerFragmentParserTest extends Assert {
	
	private Component view;
	
	public static class Snack {
		
		public String fruit;
		
		public void setFruit(String fruit) {
			this.fruit = fruit;
		}
	}
	
	private class ExistingSession extends MockArooaSession {
		
		@Override
		public ArooaTools getTools() {
			return new StandardTools();
		}
		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return new MockArooaDescriptor() {
				
				@Override
				public ElementMappings getElementMappings() {
					return new MappingsSwitch(new MockElementMappings(), 
							new MockElementMappings() {
							@Override
							public ArooaClass mappingFor(ArooaElement element,
									InstantiationContext propertyContext) {
								if (new ArooaElement("snack").equals(element)) {
									return new SimpleArooaClass(Snack.class);
								}
								fail(element.toString());
								return null;
							}
							
							@Override
							public DesignFactory designFor(ArooaElement element,
									InstantiationContext propertyContext) {
								return null;
							}
						});
				}
				
				@Override
				public ArooaBeanDescriptor getBeanDescriptor(
						ArooaClass classIdentifier, PropertyAccessor accessor) {
					return new StandardArooaDescriptor(
							).getBeanDescriptor(classIdentifier, accessor);
				}
				
				@Override
				public ClassResolver getClassResolver() {
					return new ClassLoaderClassResolver(
							getClass().getClassLoader());
				}
			};
		}
		
	}
	
   @Test
	public void testAttributeProperty() throws ArooaParseException {
		
		ArooaElement element = new ArooaElement("snack");
		element = element.addAttribute("fruit", "${apple}");
		
		ArooaSession session = new ExistingSession();
		
		DesignParser test = new DesignParser(
				session);
		
		test.setArooaType(ArooaType.VALUE);

		test.parse(new ElementConfiguration(element));
		
		DesignInstance design = test.getDesign();
		
		assertNotNull(design);
		
		Form form = design.detail();
		
		SwingFormView formView = SwingFormFactory.create(form);
		
		Component cell = formView.cell();
		
		assertNotNull(cell);
		assertTrue(cell instanceof JButton);

		view = cell;
	}
	
	public static class Complicated {
		
		public void setSimpleSnack(Snack snack) {
			
		}
		
		public void setIndexedSnack(int index, Snack snack) {
			
		}
		
		public void setMappedSnack(String key, Snack snack) {
			
		}
	}
		
	public static void main(String args[]) throws ArooaParseException {
		DesignerFragmentParserTest test = new DesignerFragmentParserTest();
		test.testAttributeProperty();
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(test.view);
		frame.pack();
		frame.setVisible(true);

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}	

	
	
}
