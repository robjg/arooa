package org.oddjob.arooa.design.view;

import org.junit.Test;

import java.io.File;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DesignValueBase;
import org.oddjob.arooa.design.etc.FileAttribute;
import org.oddjob.arooa.design.screem.FileSelection;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class FileSelectionViewTest extends Assert {

	public static class Thing {
		
		@ArooaAttribute
		public void setOne(File one) {}
		
	}
	
	DesignInstance design;
	
	
	class MyDesignFactory implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new MyDesign(element, parentContext);
		}
	}
	
	class MyDesign extends DesignValueBase {
		
		private final FileAttribute one; 
		
		
		public MyDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Thing.class), parentContext);
			
			one = new FileAttribute("one", this);
			
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { one };
		}
		
		public Form detail() {
			return new FileSelection(one);
		}
	}
	
   @Test
	public void testForm() throws ArooaParseException {

		String xml = 
			"<stuff one='f.txt'/>";
		
		StandardArooaSession session = new StandardArooaSession();
		
		DesignParser parser = new DesignParser(session, new MyDesignFactory());

		parser.parse(new XMLConfiguration("TEST", xml));
		
		design = parser.getDesign();
		
	}

	public static void main(String args[]) throws ArooaParseException {
		final FileSelectionViewTest test = new FileSelectionViewTest();
		test.testForm();

		ViewMainHelper view = new ViewMainHelper(test.design);
		view.run();
	}
}
