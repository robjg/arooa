package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockArooaTools;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.DefaultConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.reflect.MockPropertyAccessor;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.SimpleBeanRegistry;
import org.oddjob.arooa.runtime.Evaluator;
import org.oddjob.arooa.runtime.ParsedExpression;
import org.oddjob.arooa.runtime.PropertyFirstEvaluator;
import org.oddjob.arooa.runtime.PropertyManager;

/**
 * class to look at how we expand properties
 */
public class StandardPropertyHelperTest extends TestCase {
	
	private class NestedPropPropertyAccessor extends MockPropertyAccessor {
		@Override
		public Object getProperty(Object bean, String property)
				throws ArooaException {
			assertEquals(NestedProp.class, bean.getClass());
			if ("ba".equals(property)) {
				return "Ba";
			}
			if ("ba2.ba".equals(property)) {
				return "Ba";
			}
			throw new ArooaException("Doesn't Exist");
		}
	}
	
	ArooaConverter converter;
	
	SimpleBeanRegistry beanRegistry;
	
	@Override
	protected void setUp() throws Exception {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new OurConvertletProvider().registerWith(registry);
		converter = new DefaultConverter(registry);
		
		beanRegistry = new SimpleBeanRegistry(
				new NestedPropPropertyAccessor(),
				converter);
	}
	
    /**
     * run through the test cases of expansion
     * @throws ArooaConversionException 
     */
    public void testPropertyExpansion() throws ArooaConversionException {
        assertExpandsTo("","");
        assertExpandsTo("$","$");
        assertExpandsTo("$$-","$-");
        assertExpandsTo("$$","$");
        beanRegistry.register("expanded","EXPANDED");
        assertExpandsTo("a${expanded}b","aEXPANDEDb");
        assertExpandsTo("${expanded}${expanded}","EXPANDEDEXPANDED");
        assertExpandsTo("$$$","$$");
        assertExpandsTo("$$$$-","$$-");
        assertExpandsTo("","");
        assertExpandsTo("Class$$subclass","Class$subclass");    
        assertExpandsTo("Class$subclass", "Class$subclass");    
    }
    
    /**
     * new things we want
     * @throws ArooaConversionException 
     */
    public void testDollarPassthru() throws ArooaConversionException {
        assertExpandsTo("$-","$-");    
        assertExpandsTo("Class$subclass","Class$subclass");    
        assertExpandsTo("$$$-","$$-");
        assertExpandsTo("$$$$$","$$$");
        assertExpandsTo("a${unassigned-property}","a");
        assertExpandsTo("a$b","a$b");
        assertExpandsTo("$}}","$}}");
    }

	/**
	 * Object replacement
	 * @throws ArooaConversionException 
	 */
	public void testObjectReplacement() throws ArooaConversionException {
		beanRegistry.register("an-int",new Integer(2));
		assertExpandsTo("${an-int}", new Integer(2));
		assertExpandsTo("${unassigned}", null);
		assertExpandsTo("${unassigned.property}", null);
	}
    
	public void testProxyExpansion() throws ArooaConversionException {
		beanRegistry.register("fruit", new ProxyType());
		assertExpandsTo("${fruit}s and pairs", "apples and pairs");
	}
	
	public void testNullExpansionInString() throws ArooaConversionException {
		assertExpandsTo("apples and ${missing}", "apples and ");
	}
	
    /**
     * old things we dont want; not a test no more
     * @throws ArooaConversionException 
     */
    public void oldtestQuirkyLegacyBehavior() throws ArooaConversionException {
        assertExpandsTo("Class$subclass","Classsubclass");    
        assertExpandsTo("$$$-","$-");
        assertExpandsTo("a$b","ab");
        assertExpandsTo("$}}","}}");
    }

	/**
	 * Test isConstant method.
	 *
	 */
	public void testisConstant() {
		
		StandardPropertyHelper ph = new StandardPropertyHelper();
		
		ParsedExpression evaluator1 = ph.parse("abc");
		assertTrue(evaluator1.isConstant());
		
		ParsedExpression evaluator2 = ph.parse("${abc}");
		assertFalse(evaluator2.isConstant());
	}

	/**
	 * Test nested exapansion
	 * @throws ArooaConversionException 
	 *
	 */
	public void testNestedExpansion() throws ArooaConversionException {
		
		NestedProp foo = new NestedProp();
		beanRegistry.register("foo", foo);
		
		assertExpandsTo("${foo.ba}", "Ba");
		
		try {
			assertExpandsTo("${foo.Ba}", null);
			fail("Ba doesn't exist");
		} catch (ArooaException expected) { }
		
		assertExpandsTo("${foo.ba2.ba}", "Ba");
	}

	private class OurConvertletProvider implements ConversionProvider {
		public void registerWith(ConversionRegistry registry) {
			new DefaultConversionProvider().registerWith(registry);
			registry.register(ProxyType.class, String.class, 
					new Convertlet<ProxyType, String>() {
				public String convert(ProxyType from)
						throws ConvertletException {
					return from.value;
				}
			});
		}
	}
	
	private class OurSession extends MockArooaSession {

		@Override
		public BeanRegistry getBeanRegistry() {
			return beanRegistry;
		}
		
		@Override
		public ArooaTools getTools() {
			return new MockArooaTools() {
				@Override
				public ArooaConverter getArooaConverter() {
					return converter;
				}
				@Override
				public Evaluator getEvaluator() {
					return new PropertyFirstEvaluator();
				}
			};
		}
		
		@Override
		public PropertyManager getPropertyManager() {
			return new StandardPropertyManager();
		}
	}
	
    /**
     * little helper method to validate stuff
     * @throws ArooaConversionException 
     */
    private void assertExpandsTo(String source, Object expected) throws ArooaConversionException {

    	StandardPropertyHelper ph = new StandardPropertyHelper();
		
    	ParsedExpression evaluator = ph.parse(source);
    	
        Object actual = evaluator.evaluate(
        		new OurSession(), Object.class);
        
        assertEquals(source,expected,actual);
    }

    public class NestedProp {

    }
    
    public static class ProxyType implements ArooaValue {
    	String value = "apple";
    }
    
}
