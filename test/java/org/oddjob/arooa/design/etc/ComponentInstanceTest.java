package org.oddjob.arooa.design.etc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.design.DesignComponent;
import org.oddjob.arooa.design.DesignListener;
import org.oddjob.arooa.design.DesignSeedContext;
import org.oddjob.arooa.design.DesignStructureEvent;
import org.oddjob.arooa.design.GenericDesignFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ComponentInstanceTest extends Assert {

	public static class OurComponent {
		
		public void setChildren(int ignore, OurComponent child) {}
	}

	public static class OurComponentArooa extends MockArooaBeanDescriptor {

		public String getComponentProperty() {
			return "children";
		}
		
		public ParsingInterceptor getParsingInterceptor() {
			return null;
		}
		
		public String getTextProperty() {
			return null;
		}
		
		public ConfiguredHow getConfiguredHow(String property) {
			return ConfiguredHow.ELEMENT;
		}
		
		@Override
		public boolean isAuto(String property) {
			return false;
		}
		
	}
	
	class OurListener implements DesignListener {
		List<DesignComponent> children = new ArrayList<DesignComponent>();
		
		public void childAdded(DesignStructureEvent event) {
			children.add(event.getIndex(), (DesignComponent) event.getChild());
		}
		
		public void childRemoved(DesignStructureEvent event) {
			children.remove(event.getIndex());
		}
	}
	
   @Test
	public void testAddRemove() throws ArooaParseException {

		DesignSeedContext context = new DesignSeedContext(
				ArooaType.COMPONENT,
				new StandardArooaSession());
		
		DesignComponent instance = (DesignComponent) 
			new GenericDesignFactory(
					new SimpleArooaClass(OurComponent.class)).createDesign(
				new ArooaElement("fruit"), 
				context);

		OurListener listener = new OurListener();
		
		instance.addStructuralListener(listener);
		
		assertEquals(0, listener.children.size());
		
		CutAndPasteSupport cutAndPaste = new CutAndPasteSupport(
				instance.getArooaContext());
		
		cutAndPaste.paste(0, new XMLConfiguration("TEST", "<rubbish> </rubbish>"));
		
		assertEquals(1, listener.children.size());
		
		cutAndPaste.cut(listener.children.get(0).getArooaContext());
		
		assertEquals(0, listener.children.size());
	}
}
