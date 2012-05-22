package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.standard.MockPropertyLookup;
import org.oddjob.arooa.standard.StandardArooaSession;

import junit.framework.TestCase;

public class RetainUnexpandedStringsTest extends TestCase {

	public void testLotsOfExpressions() throws ArooaConversionException {
		
		ArooaSession session = new SubstituationPolicySession(
				new StandardArooaSession(), new RetainUnexpandedStrings());
		
		BeanRegistry registry = session.getBeanRegistry();
	
		Object bean = new Object() {
			public String toString() {
				return "A Bean";
			}
		};
		registry.register("my-bean", bean);

		session.getPropertyManager().addPropertyLookup(new MockPropertyLookup() {
			@Override
			public String lookup(String propertyName) {
				if ("favourite.fruit".equals(propertyName)) {
					return "apples";
				}
				else {
					return null;
				}
			}
		});
				
		ExpressionParser parser = session.getTools().getExpressionParser();
		
		assertEquals("apples are my ${favourite.snack}", 
				parser.parse("${favourite.fruit} are my ${favourite.snack}"
						).evaluate(session, String.class));
		
		assertEquals("My Bean is A Bean", 
				parser.parse("My Bean is ${my-bean}"
						).evaluate(session, String.class));
		
		assertEquals("${${that-bean}}", 
				parser.parse("${${that-bean}}"
						).evaluate(session, String.class));

		assertEquals("There's no ${apples}", 
				parser.parse("There's no ${${favourite.fruit}}"
						).evaluate(session, String.class));
		
		assertEquals(bean, 
				parser.parse("${my-bean}"
						).evaluate(session, Object.class));
		
		assertEquals("There's too many ${dollars}", 
				parser.parse("There's too many $${dollars}"
						).evaluate(session, String.class));
		
		assertEquals("More $s and $s", 
				parser.parse("More $$s and $s"
						).evaluate(session, String.class));
	}
	
}
