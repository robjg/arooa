package org.oddjob.arooa.design.view;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.deploy.annotations.ArooaText;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DesignValueBase;
import org.oddjob.arooa.design.SimpleTextProperty;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class TextInputViewTest extends TestCase {

	public static class Thing {
		
		@ArooaText
		public void setText(String text) {}
	}
	
	DesignInstance design;
	
	
	class MyDesignFactory implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new MyDesign(element, parentContext);
		}
	}
	
	class MyDesign extends DesignValueBase {
		
		private final SimpleTextProperty text; 
				
		public MyDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Thing.class), 
					parentContext);
			
			text = new SimpleTextProperty("text");
			
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { text };
		}
		
		public Form detail() {
			return new StandardForm("Stuff", this).addFormItem(
					text.view());
		}
	}
	
	public void testForm() throws ArooaParseException {

		String EOL = System.getProperty("line.separator");
		
		String xml = 
			"<stuff>" +
			" This" + EOL +
			"is" + EOL +
			"many" + EOL +
			"lines" + EOL +
			"</stuff>";
		
		StandardArooaSession session = new StandardArooaSession();
		
		DesignParser parser = new DesignParser(session, new MyDesignFactory());

		parser.parse(new XMLConfiguration("TEST", xml));
		
		design = parser.getDesign();
		
	}

	public static void main(String args[]) throws ArooaParseException {
		final TextInputViewTest test = new TextInputViewTest();
		test.testForm();

		ViewMainHelper view = new ViewMainHelper(test.design);
		view.run();
	}
}
