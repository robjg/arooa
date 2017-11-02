package org.oddjob.arooa.design;

import org.junit.Test;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.junit.Assert;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockArooaTools;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.view.SwingFormFactory;
import org.oddjob.arooa.design.view.SwingFormView;
import org.oddjob.arooa.life.BaseElementMappings;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.IsType;

public class BaseInstantiatorDesignTest extends Assert {
	
	private Component view;
	
	public static class Snack {
		
		public String fruit;
		
		public void setFruit(String fruit) {
			this.fruit = fruit;
		}
	}
	
	class ExistingSession extends MockArooaSession {
		@Override
		public ArooaTools getTools() {
			return new MockArooaTools() {
				@Override
				public PropertyAccessor getPropertyAccessor() {
					return new BeanUtilsPropertyAccessor();
				}
			};
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
					return null;
				}
			};
		}
	}
	
	class OurConfiguration implements ArooaConfiguration {
		public ConfigurationHandle parse(ArooaContext parentContext)
				throws ArooaParseException {
			
			ArooaElement element = new ArooaElement("snack");
			element = element.addAttribute("fruit", "${apple}");
			
			parentContext.getArooaHandler().onStartElement(element, parentContext);
			
			return null;
		}
	}
	
	public static class Complicated {
		
		public void setSimpleSnack(Snack snack) {
			
		}
		
		public void setIndexedSnack(int index, Snack snack) {
			
		}
		
		public void setMappedSanck(String key, Snack snack) {
			
		}
	}
	
	class OurContext extends MockArooaContext {
		PrefixMappings prefixMappings = new SimplePrefixMappings();
		
		ArooaSession session;
		
		OurContext(ArooaSession session) {
			this.session = session;
		}

		@Override
		public ArooaType getArooaType() {
			return ArooaType.VALUE;
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return null;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public ArooaClass getClassIdentifier() {
					return new SimpleArooaClass(
							Complicated.class);
				}
			};
		}
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return prefixMappings;
		}
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
	}
	
   @Test
	public void testIsDesign() throws ArooaParseException {

		ArooaSession session = new StandardArooaSession();

		ArooaContext seedContext = new OurContext(session);

		DesignFactory factory = new BaseElementMappings().designFor(
				IsType.ELEMENT, new InstantiationContext(
						ArooaType.VALUE, new SimpleArooaClass(
								Complicated.class)));
		
		DesignInstance design = factory.createDesign(
				IsType.ELEMENT, seedContext);
				
		DesignProperty[] children = ((DesignInstanceBase) design).children();
		
		assertEquals(3, children.length);
		
		Form form = design.detail();
		
		SwingFormView formView = SwingFormFactory.create(form);
		
		Component cell = formView.cell();
		
		assertNotNull(cell);
		assertTrue(cell instanceof JButton);

		Component dialog = formView.dialog();
		assertNotNull(dialog);
		assertTrue(dialog instanceof JPanel);
		
		view = dialog;
	}
	
	public static void main(String args[]) throws ArooaParseException {
		BaseInstantiatorDesignTest test = new BaseInstantiatorDesignTest();
		test.testIsDesign();
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(test.view);
		frame.pack();
		frame.setVisible(true);

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}	

}
