package org.oddjob.arooa.design.designer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.DragContext;
import org.oddjob.arooa.parsing.DragPoint;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ArooaTransferHandlerIdsTest extends TestCase {
	private static final Logger logger = Logger.getLogger(ArooaTransferHandlerIdsTest.class);
	
	public static class Component {
		
		List<Component> children = new ArrayList<Component>();
		
		@ArooaComponent
		public void setChild(int index, Component component) {
			this.children.add(component);
		}
		
		public void setValue(Object value) {
			
		}
	}
	
	private class OurArooaContainer 
	extends JComponent implements ArooaContainer {
		private static final long serialVersionUID = 2011032500L;
		
		final DragPoint dragPoint;
		
		public OurArooaContainer(ArooaContext context) {
			this.dragPoint = new DragContext(context);
		}
		
		@Override
		public DragPoint getCurrentDragPoint() {
			return dragPoint;
		}
		
		@Override
		public DropPoint dropPointFrom(TransferSupport support) {
			throw new RuntimeException("Unexpected.");
		}
	}
	
		
	public void testSimulatedDrag() throws ArooaConfigurationException, ArooaParseException, ArooaConversionException {
		
		ArooaTransferHandler test = new ArooaTransferHandler();
		
		test.addTransferEventListener(new TransferEventListener() {
			
			@Override
			public void transferException(TransferEvent event, String message,
					Exception exception) {
				logger.error(message, exception);
			}
		});
		
		Component root = new Component();
		
		String middleBit = 
			"  <bean class='" + Component.class.getName() + "' id='a'>" +
			"   <child>" +
			"    <bean class='" + Component.class.getName() + "' id='b'>" +
			"     <value>" +
			"      <bean id='v'/>" +
			"     </value>" +
			"    </bean>" +
			"   </child>" +
			"  </bean>";
		
		
		String xml =
			"<component>" +
			" <child>" +
			middleBit +
			" </child>" +
			"</component>";

		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(new XMLConfiguration("XML", xml));
		
		ArooaSession session = parser.getSession();
		
		ComponentPool pool = parser.getSession().getComponentPool();
		
		Component a1 = session.getBeanRegistry().lookup("a", Component.class);
		Component b1 = session.getBeanRegistry().lookup("b", Component.class);
		
		Object value1 = session.getBeanRegistry().lookup("v");
		assertNotNull(value1);
		
		ArooaContext aContext = pool.trinityForId("a").getTheContext();

		ArooaContext bContext = pool.trinityForId("b").getTheContext();
		
		OurArooaContainer fromContainer = new OurArooaContainer(
				bContext);
		
		OurArooaContainer toContainer = new OurArooaContainer(
				aContext);
		
		final Transferable transferable = test.createTransferable(
				fromContainer);
				
		TransferSupport transferSupport = new TransferSupport(
				toContainer, transferable);
		
		assertTrue(test.importData(transferSupport));
		
		test.exportDone(fromContainer, transferable, TransferHandler.MOVE);
		
		
		// Checks
		
		Component a2 = session.getBeanRegistry().lookup("a", Component.class);
		Component b2 = session.getBeanRegistry().lookup("b", Component.class);		
		Object value2 = session.getBeanRegistry().lookup("v");
		
		assertSame(a1, a2);
		
		assertNotSame(b1, b2);
		
		assertNotNull(value2);
		assertNotSame(value1, value2);
		
		handle.getDocumentContext().getRuntime().destroy();
		
		assertEquals(4, a1.children.size());
		assertNotNull(a1.children.get(0));
		assertNull(a1.children.get(1));
		assertNotNull(a1.children.get(2));
		assertNull(a1.children.get(3));
		
	}
	
	private class OurTransferable implements Transferable {
		
		private final String data;
		
		public OurTransferable(String data) {
			this.data = data;
		}
		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			assertEquals(DataFlavor.stringFlavor, flavor);
			return data;
		}
		
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			throw new RuntimeException("Unexpected");
		}
		
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			assertEquals(DataFlavor.stringFlavor, flavor);
			return true;
		}
	}
	
	public void testSimulatedPaste() throws ArooaConfigurationException, ArooaParseException, ArooaConversionException {
		
		ArooaTransferHandler test = new ArooaTransferHandler();
		
		test.addTransferEventListener(new TransferEventListener() {
			
			@Override
			public void transferException(TransferEvent event, String message,
					Exception exception) {
				logger.error(message, exception);
			}
		});
		
		Component root = new Component();
		
		String xml =
			"<component>" +
			" <child>" +
			"  <bean class='" + Component.class.getName() + "' id='a'>" +
			"   <child>" +
			"   </child>" +
			"  </bean>" +		
			" </child>" +
			"</component>";

		String copyText = 
			"    <bean class='" + Component.class.getName() + "' id='b'>" +
			"     <value>" +
			"      <bean id='v'/>" +
			"     </value>" +
			"    </bean>";

		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(new XMLConfiguration("XML", xml));
		
		ArooaSession session = parser.getSession();
		
		ComponentPool pool = parser.getSession().getComponentPool();
		
		Component a1 = session.getBeanRegistry().lookup("a", Component.class);
		
		ArooaContext aContext = pool.trinityForId("a").getTheContext();

		OurArooaContainer toContainer = new OurArooaContainer(
				aContext);
		
		final Transferable transferable = new OurTransferable(copyText);
				
		TransferSupport transferSupport = new TransferSupport(
				toContainer, transferable);
		
		assertTrue(test.importData(transferSupport));
		
		
		// Checks
		
		Component b2 = session.getBeanRegistry().lookup("b", Component.class);		
		Object value2 = session.getBeanRegistry().lookup("v");
		
		assertNotNull(b2);		
		assertNotNull(value2);
		
		handle.getDocumentContext().getRuntime().destroy();
		
		assertEquals(2, a1.children.size());
		assertNotNull(a1.children.get(0));
		assertNull(a1.children.get(1));
		
	}
}
