package org.oddjob.arooa.design;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.design.model.MockDesignInstance;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class SimpleDesignPropertyTest extends XMLTestCase {

	public static class Fruit {
		
	}
	
	private class ParentContext extends MockArooaContext {
		
		ArooaSession session;
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return new SimplePrefixMappings();
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
		ArooaContext context;
		@Override
		public ArooaContext getArooaContext() {
			return context;
		}
	}
	
	public void testAddClassElement() throws ArooaParseException {
		
		ArooaSession standardSession = new StandardArooaSession();
		
		ParentContext parentContext = new ParentContext();
		parentContext.session = standardSession;
		
		OurDesign design = new OurDesign();
		design.context = parentContext;
		
		SimpleDesignProperty test = new SimpleDesignProperty(
				"fruit", Fruit.class, ArooaType.VALUE, design);

		assertEquals(0, test.instanceCount());

		ArooaContext instanceContext = 
			test.getArooaContext().getArooaHandler().onStartElement(
					new ArooaElement("class"), test.getArooaContext());
		
		test.getArooaContext().getConfigurationNode().insertChild(
				instanceContext.getConfigurationNode());
		
		instanceContext.getRuntime().init();
		
		assertEquals(1, test.instanceCount());
	}
		
	private class OurInstance extends DesignInstanceBase {

		DesignProperty prop;
		
		public OurInstance(ArooaElement element, ArooaContext context) {
			super(element, null, context);
		}
	
		public Form detail() {
			throw new RuntimeException("Unexpected.");
		}
	
		@Override
		protected DesignProperty[] children() {
			return new DesignProperty[] { prop };
		}
	}
	
	private class OurDescriptor extends MockArooaDescriptor {

		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass classIdentifier, PropertyAccessor accessor) {
			return new MockArooaBeanDescriptor() {
				@Override
				public String getComponentProperty() {
					return "fruit";
				}
			};
		}	
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(null, 
					new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element,
							InstantiationContext parentContext) {
						assertEquals("idontexist", element.getTag());

						return null;
					}
					@Override
					public DesignFactory designFor(ArooaElement element,
							InstantiationContext parentContext) {
						return null;
					}
					@Override
					public ArooaElement[] elementsFor(InstantiationContext parentContext) {
						return null;
					}
				});
		}
		
		@Override
		public ConversionProvider getConvertletProvider() {
			return null;
		}
	}		

	
	public void testAddRubbishElement() throws Exception {

		ArooaDescriptor descriptor = new OurDescriptor();
		
		ArooaContext context = new DesignSeedContext(
				ArooaType.VALUE,
				new StandardArooaSession(descriptor));
		
		OurInstance instance = new OurInstance(
				new ArooaElement("test"), context);
		
		final SimpleDesignProperty test = new SimpleDesignProperty(
				"fruit", Fruit.class, ArooaType.VALUE, instance);

		instance.prop = test;
		
		CutAndPasteSupport cutAndPaste = new CutAndPasteSupport(instance.getArooaContext());

		String xml = "<idontexist/>" + System.getProperty("line.separator");
		
		cutAndPaste.paste(0, new XMLConfiguration("TEST", xml));
		
		assertEquals(1, test.instanceCount());
		
		Unknown result = (Unknown) test.instanceAt(0);
		
		assertXMLEqual(xml, result.getXml());
	}
	
}
