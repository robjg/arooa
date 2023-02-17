package org.oddjob.arooa.design;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.standard.StandardArooaSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class PropertyContextTest {

	public static class Thing {
		
		public void setProp(Object o) {}
	}
	
	static class OurContext extends MockArooaContext {

		DesignInstance result;
		
		ConfigurationNode<ArooaContext> configurationNode =
			new AbstractConfigurationNode<>() {

			public void addText(String text) {
				throw new RuntimeException("Unexpected.");
			}

			public ArooaContext getContext() {
				throw new RuntimeException("Unexpected.");
			}

			public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext) {
				throw new RuntimeException("Unexpected.");
			}
			
		};

		@Override
		public ArooaType getArooaType() {
			return ArooaType.VALUE;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				
				@Override
				public void setIndexedProperty(String name, int index,
						Object value) {
					assertThat(index, is(0));
					result = (DesignInstance) value;
				}
			};
		}
		
		@Override
		public ConfigurationNode<ArooaContext> getConfigurationNode() {
			return configurationNode;
		}
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return new SimplePrefixMappings();
		}
		
		@Override
		public ArooaSession getSession() {
			return new StandardArooaSession();
		}

	}

	
	static class OurDesign extends DesignValueBase {

		IndexedDesignProperty prop;
		
		OurDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Thing.class), parentContext);
			
			prop = new IndexedDesignProperty(
					"prop", String.class, ArooaType.VALUE, this);
		}
		
		@Override
		public DesignProperty[] children() {
			throw new RuntimeException("Unexpected.");
		}
		
		public Form detail() {
			throw new RuntimeException("Unexpected.");
		}
	}
	
   @Test
	public void testHandler() {
		
		OurDesign design = new OurDesign(
				new ArooaElement("stuff"),
				new DesignSeedContext(
						ArooaType.VALUE,
						new StandardArooaSession()));
		
		OurContext captureContext = new OurContext();

		PropertyContext test = new PropertyContext(
				Object.class,
				design.prop,
				ArooaType.VALUE,
				captureContext);
		
		ArooaHandler handler = test.getArooaHandler();
		
		ArooaContext childContext = handler.onStartElement(
				new ArooaElement("idontexist"), captureContext);

		captureContext.getConfigurationNode().insertChild(
				childContext.getConfigurationNode());
		
		childContext.getRuntime().init();
		
		assertThat(captureContext.result, Matchers.instanceOf(Unknown.class));
		
		Unknown unknown = (Unknown) captureContext.result;
		
		String expected = "<idontexist/>" + System.getProperty("line.separator");
		
		assertThat(unknown.getXml(), isSimilarTo(expected));
	}
}
