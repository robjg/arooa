package org.oddjob.arooa.standard;

import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.ArooaDescriptorDescriptor;
import org.oddjob.arooa.deploy.ArooaDescriptorFactory;
import org.oddjob.arooa.life.ArooaLifeAware;
import org.oddjob.arooa.xml.XMLConfiguration;

public class DestructionTest extends TestCase {

	public static class Stuff implements ArooaLifeAware {
		
		Stuff moreStuff;
		Other other;
		
		boolean destroyed;
		
		public void setMoreStuff(Stuff moreStuff) {
			if (moreStuff == null) {
				assertNotNull(this.moreStuff);
				this.moreStuff = null;
			}
			else {
				assertNull(this.moreStuff);
				this.moreStuff = moreStuff;
			}
		}
		
		public void setOther(Other other) {
			if (other == null) {
				assertNotNull(this.other);
				this.other = null;
			}
			else {
				assertNull(this.other);
				this.other = other;				
			}
		}
		
		public void initialised() {
		}
		
		public void configured() {
		}
		
		public void destroy() {
			destroyed = true;
		}
	}
	
	public static class Other implements ArooaLifeAware {
				
		boolean destroyed;
				
		public void initialised() {
		}
		
		public void configured() {
		}
		
		public void destroy() {
			destroyed = true;
		}
	}
	
	String EOL = System.getProperty("line.separator");
	
	String descriptor =
		"<arooa:descriptor xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" + EOL +
		"  <values>" + EOL +
		"        <arooa:bean-def element='other' className='" + Other.class.getName() + "'/>" + EOL +
		"  </values>" + EOL +
		"  <components>" + EOL +
		"        <arooa:bean-def element='stuff' className='" + Stuff.class.getName() + "'>" + EOL + 
		"          <properties>" +
		"            <arooa:property name='moreStuff' type='COMPONENT'/>" +
		"          </properties>" +
		"        </arooa:bean-def>" + EOL + 
		"  </components>" + EOL +
		"</arooa:descriptor>" + EOL;
		
	String xml =
		"<stuff>" + EOL +
		"    <moreStuff>" + EOL +
		"        <stuff id='stuff'/>" + EOL +
		"    </moreStuff>" + EOL +
		"    <other>" + EOL +
		"        <other/>" + EOL +
		"    </other>" + EOL +
		"</stuff>" + EOL;
	
	public void testParseAndDestroy() throws URISyntaxException, ArooaParseException {

		StandardFragmentParser descriptorParser = new StandardFragmentParser(
						new ArooaDescriptorDescriptor());
		
		descriptorParser.parse(new XMLConfiguration("TEST", descriptor));
		
		ArooaDescriptorFactory descriptorFactory = 
			(ArooaDescriptorFactory) descriptorParser.getRoot();
		
		ArooaDescriptor ourDescriptor = descriptorFactory.createDescriptor(
				getClass().getClassLoader());
				
		StandardArooaSession session = new StandardArooaSession(ourDescriptor);

		Stuff root = new Stuff();
		
		StandardArooaParser parser = new StandardArooaParser(root, session);

		ConfigurationHandle handle = parser.parse(
				new XMLConfiguration("TEST", xml));

		assertNotNull(session.getBeanRegistry().lookup("stuff"));
		
		parser.getSession().getComponentPool().configure(root);
		
		Stuff stuff = root.moreStuff;
		Other other = root.other;
		
		assertFalse(root.destroyed);
		assertFalse(stuff.destroyed);
		assertFalse(other.destroyed);
		
		handle.getDocumentContext().getRuntime().destroy();
		
		assertNull(root.moreStuff);
		assertNull(root.other);
		
		assertTrue(root.destroyed);
		assertTrue(stuff.destroyed);
		assertTrue(other.destroyed);
		
		assertNull(session.getBeanRegistry().lookup("stuff"));
	}
	
	public void testParseAndDestroyNoConfigure() throws URISyntaxException, ArooaParseException {

		StandardFragmentParser descriptorParser = new StandardFragmentParser(
						new ArooaDescriptorDescriptor());
		
		descriptorParser.parse(new XMLConfiguration("TEST", descriptor));
		
		ArooaDescriptorFactory descriptorFactory = 
			(ArooaDescriptorFactory) descriptorParser.getRoot();
		
		ArooaDescriptor ourDescriptor = descriptorFactory.createDescriptor(
				getClass().getClassLoader());
				
		StandardArooaSession session = new StandardArooaSession(ourDescriptor);

		Stuff root = new Stuff();
		
		StandardArooaParser parser = new StandardArooaParser(root, session);

		ConfigurationHandle handle = parser.parse(new XMLConfiguration("TEST", xml));

		Stuff stuff = root.moreStuff;
		assertNull(root.other);
		
		assertFalse(root.destroyed);
		assertFalse(stuff.destroyed);
		
		handle.getDocumentContext().getRuntime().destroy();
		
		assertNull(root.moreStuff);
		
		assertTrue(root.destroyed);
		assertTrue(stuff.destroyed);		
	}
	

}
