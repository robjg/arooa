package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.GenericDesignFactory;
import org.oddjob.arooa.design.InstanceSupport;
import org.oddjob.arooa.design.SimpleDesignProperty;
import org.oddjob.arooa.design.model.MockDesignInstance;
import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.QTag;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.standard.StandardTools;

public class TypeSelectionViewTest extends TestCase {

	public static class Fruit {
		
	}
	
	public static class Apple {
		public void setColour(String colour) {}
		
	}
	
	public static class Orange {
		
	}
		
	private Component view;
	
	private class OurMappings extends MockElementMappings {
		
		@Override
		public DesignFactory designFor(ArooaElement element,
				InstantiationContext parentContext) {
			return new GenericDesignFactory(
					new SimpleArooaClass(Object.class));
		}
		
		@Override
		public ArooaElement[] elementsFor(
				InstantiationContext context) {
			try {
				return new ArooaElement[] {
						new ArooaElement(new URI("http://fruit"), "apple"),
						new ArooaElement(new URI("http://fruit"), "orange") };
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	PrefixMappings prefixMappings = new SimplePrefixMappings();
	
	{
		try {
			prefixMappings.put("fruit", new URI("http://fruit"));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private class OurSession extends MockArooaSession {
		
		@Override
		public ArooaTools getTools() {
			return new StandardTools();
		}
		
		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return new MockArooaDescriptor() {
				
				@Override
				public ElementMappings getElementMappings() {
					return new OurMappings();
				}
				
				@Override
				public ArooaBeanDescriptor getBeanDescriptor(
						ArooaClass classIdentifier, PropertyAccessor accessor) {
					return new MockArooaBeanDescriptor() {
						
					};
				}
				
				@Override
				public ClassResolver getClassResolver() {
					return new ClassLoaderClassResolver(
							getClass().getClassLoader());
				}
			};
		}
	}
	
	private class OurContext extends MockArooaContext {
	
		ArooaSession session = new OurSession();
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return prefixMappings;
		}
			
		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
				@Override
				public int insertChild(ConfigurationNode child) {
					return -1;
				}
			};
		}
	}
	
	private class OurDesign extends MockDesignInstance {
		
		@Override
		public ArooaContext getArooaContext() {
			return new OurContext();
		}
	}
	

	public void testView() {
		
		SimpleDesignProperty test = new SimpleDesignProperty(
				"test", Fruit.class, ArooaType.VALUE, new OurDesign());
				
		FormItem viewModel = test.view();
		
		assertNotNull(viewModel);
		
		SwingItemView viewProducer = SwingItemFactory.create(viewModel);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		viewProducer.inline(panel, 0, 0, false);

		assertEquals(3, panel.getComponentCount());

		Component c2 = panel.getComponent(1);
		Component c3 = panel.getComponent(2);
		
		assertEquals(JComboBox.class, c2.getClass());
		assertEquals(JPanel.class, c3.getClass());
		
		JComboBox<?> comboBox = (JComboBox<?>) c2;
		JPanel cell = (JPanel) c3;
		
		assertEquals(TypeSelectionView.NULL_TAG, comboBox.getSelectedItem());
		assertFalse(test.isPopulated());
		assertEquals(1, cell.getComponentCount());
		
		// changeSelection
		comboBox.setSelectedIndex(1);
		
		assertEquals(prefixMappings.getQName(
				new ArooaElement(prefixMappings.getUriFor("fruit"), "apple")), 
						comboBox.getSelectedItem());
				
		assertTrue(test.isPopulated());
		assertEquals(1, cell.getComponentCount());

		// blankSelection
		comboBox.setSelectedIndex(0);
		
		assertEquals(TypeSelectionView.NULL_TAG, comboBox.getSelectedItem());		
		assertFalse(test.isPopulated());

		view = panel;				
	}
	
	public void testViewPropertyPopulated() throws URISyntaxException, ArooaParseException {
		SimpleDesignProperty test = new SimpleDesignProperty(
				"test", Fruit.class, ArooaType.VALUE, new OurDesign());

		InstanceSupport support = new InstanceSupport(test);
		support.insertTag(0, 
				new QTag("fruit", 
						new ArooaElement(new URI("http://fruit"), "apple")));
		
		FormItem viewModel = test.view();
		
		assertNotNull(viewModel);
		
		SwingItemView viewProducer = SwingItemFactory.create(viewModel);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		viewProducer.inline(panel, 0, 0, false);
		
		view = panel;				
	}
	
	public static void main(String args[]) throws URISyntaxException {
		TypeSelectionViewTest test = new TypeSelectionViewTest();
		test.testView();
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(test.view);
		frame.pack();
		frame.setVisible(true);

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	}
}
