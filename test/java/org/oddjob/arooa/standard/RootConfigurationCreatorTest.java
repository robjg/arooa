package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.LinkedDescriptor;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.MockComponentPool;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class RootConfigurationCreatorTest extends TestCase {
	
	private class OurDescriptor extends MockArooaDescriptor {

		@Override
		public ConversionProvider getConvertletProvider() {
			return null;
		}
				
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass forClass, PropertyAccessor accessor) {
			return new MockArooaBeanDescriptor() {
				@Override
				public ParsingInterceptor getParsingInterceptor() {
					return null;
				}
			};
		}
		
		@Override
		public ElementMappings getElementMappings() {
			return null;
		}
	}
	
	private class OurSession extends MockArooaSession {
		
		String id;
		Object component;
		Object proxy;
		ArooaContext context;
		
		ArooaDescriptor descriptor;
		ArooaTools tools;
		
		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return descriptor;
		}
		
		@Override
		public ComponentPool getComponentPool() {
			return new MockComponentPool() {
				@Override
				public void registerComponent(ComponentTrinity trinity,
						String id) {
					OurSession.this.id = id;
					OurSession.this.component = trinity.getTheComponent();
					OurSession.this.proxy = trinity.getTheProxy();
					OurSession.this.context = trinity.getTheContext();
				}
			};
		}
		
		@Override
		public ArooaTools getTools() {
			return tools;
		}
		
		@Override
		public ComponentProxyResolver getComponentProxyResolver() {
			return new ComponentProxyResolver() {
				@Override
				public Object resolve(Object object, ArooaSession session) {
					assertTrue(object instanceof RootComponent); 
					return new OurProxy();
				}
				@Override
				public Object restore(Object proxy, ArooaSession session) {
					return null;
				}
			};
		}
	}
		
	private class OurContext extends MockArooaContext {
	
		ArooaSession session;
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return null;
		}
	}
	
	private class NextContext extends MockArooaContext {
		ArooaSession session;
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
	
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public ArooaClass getClassIdentifier() {
					return new SimpleArooaClass(RootComponent.class);
				}
			};
		}
	}
	
	public static class RootComponent {
		
	}

	public static class OurProxy {
		
	}
	
	/**
	 * Test creating a root ArooaRuntime from what would
	 * be a document element.
	 *
	 */
	public void testCreateRootRuntime() {
		
		MutableAttributes atts = new MutableAttributes();
		atts.set("id", "x");
		ArooaElement element = new ArooaElement("a", atts);
		
		OurSession session = new OurSession();
		
		OurDescriptor descriptor = new OurDescriptor();

		session.descriptor = new LinkedDescriptor(
				descriptor,
					new StandardArooaDescriptor());
		session.tools = new ExtendedTools(new StandardTools(),
				session.descriptor);
		
		OurContext context = new OurContext();
		context.session = session;

		RootComponent root = new RootComponent(); 
		
		RootConfigurationCreator test = new RootConfigurationCreator(
				root, true);
		
		InstanceConfiguration result = test.onElement(
				element, 
				context);

		assertNotNull(result);
		assertTrue(result instanceof ComponentConfiguration);
		
		RootRuntime wrapper = new RootRuntime(result, context);
		
		NextContext nextContext = new NextContext();
		nextContext.session = session;
		
		wrapper.setContext(nextContext);
		
		wrapper.init();
		
		assertEquals("x", session.id);
		assertEquals("The component", root, session.component);
		assertTrue("The Proxy", session.proxy instanceof OurProxy);
		assertNotNull("The context", session.context);
	}
}
