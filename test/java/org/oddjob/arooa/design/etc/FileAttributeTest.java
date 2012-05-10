package org.oddjob.arooa.design.etc;

import java.io.File;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DesignValueBase;
import org.oddjob.arooa.design.screem.BorderedGroup;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class FileAttributeTest extends TestCase {

	public static class Thing {
		
		@ArooaAttribute
		public void setFile(File file) {}
		
	}
	
	DesignInstance design;
	
	
	class MyDesignFactory implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new MyDesign(element, parentContext);
		}
	}
	
	class MyDesign extends DesignValueBase {
		
		private final FileAttribute file; 
		
		public MyDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Thing.class), parentContext);
			
			file = new FileAttribute("file", this);			
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { file };
		}
		
		public Form detail() {
			return new StandardForm("Stuff", this).addFormItem(
				new BorderedGroup()					
						.add(file.view()
			));
		}
	}
	
	public void testForm() throws ArooaParseException {

		String xml = 
			"<stuff file='a.txt'/>";
		
		StandardArooaSession session = new StandardArooaSession();
		
		DesignParser parser = new DesignParser(session, new MyDesignFactory());

		parser.parse(new XMLConfiguration("TEST", xml));
		
		design = parser.getDesign();
		
	}

	public static void main(String args[]) throws ArooaParseException {
		final FileAttributeTest test = new FileAttributeTest();
		test.testForm();

		ViewMainHelper view = new ViewMainHelper(test.design);
		view.run();
	}
}
