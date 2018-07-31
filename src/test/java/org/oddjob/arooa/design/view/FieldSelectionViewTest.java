package org.oddjob.arooa.design.view;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DesignValueBase;
import org.oddjob.arooa.design.IndexedDesignProperty;
import org.oddjob.arooa.design.SimpleDesignProperty;
import org.oddjob.arooa.design.SimpleTextAttribute;
import org.oddjob.arooa.design.SimpleTextProperty;
import org.oddjob.arooa.design.etc.FileAttribute;
import org.oddjob.arooa.design.screem.BorderedGroup;
import org.oddjob.arooa.design.screem.FieldSelection;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class FieldSelectionViewTest extends Assert {

	public static class Thing {
		
		public void setOne(String one) {}
		
		public void setFour(String four) {}
		
		public void setFive(String five) {}
	}
	
	DesignInstance design;
	
	
	class MyDesignFactory implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new MyDesign(element, parentContext);
		}
	}
	
	class MyDesign extends DesignValueBase {
		
		private final SimpleTextAttribute one; 
		
		private final SimpleDesignProperty two; 
		
		private final IndexedDesignProperty three; 
		
		private final SimpleTextAttribute four; 
		
		private final SimpleTextAttribute five; 
		
		private final SimpleTextProperty six; 
		
		private final FileAttribute seven;
		
		public MyDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Thing.class), parentContext);
			
			one = new SimpleTextAttribute("one", this);
			
			two = new SimpleDesignProperty(
					"two", Object.class, ArooaType.VALUE, this);
			
			three = new IndexedDesignProperty(
					"three", Object.class, ArooaType.VALUE, this);
			
			four = new SimpleTextAttribute("four", this);
			
			five = new SimpleTextAttribute("five", this);
			
			six = new SimpleTextProperty("six");
			
			seven = new FileAttribute("seven", this);
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { 
					one, two, three, four, five, six, seven };
		}
		
		public Form detail() {
			return new StandardForm("Stuff", this).addFormItem(
				new BorderedGroup("Options")
					.add(new FieldSelection()
						.add(one.view())
						.add(two.view())
						.add(three.view())
						.add(new BorderedGroup()
							.add(four.view())
							.add(five.view()))
						.add(six.view())
						.add(seven.view())
					)
			);
		}
	}
	
   @Test
	public void testForm() throws ArooaParseException {

		String xml = 
			"<stuff " +
//			"four='a' five='b'" +
			"    >" +
//			"  <two>" +
//			"    <value value='Apples'/>" +
//			"  </two>" +
			"  <three>" +
			"    <value value='Apples'/>" +
			"    <value value='Pears'/>" +
			"  </three>" +
			"</stuff>";
		
		StandardArooaSession session = new StandardArooaSession();
		
		DesignParser parser = new DesignParser(session, new MyDesignFactory());

		parser.parse(new XMLConfiguration("TEST", xml));
		
		design = parser.getDesign();
		
	}

	public static void main(String args[]) throws ArooaParseException {
		final FieldSelectionViewTest test = new FieldSelectionViewTest();
		test.testForm();

		ViewMainHelper view = new ViewMainHelper(test.design);
		view.run();
	}
}
