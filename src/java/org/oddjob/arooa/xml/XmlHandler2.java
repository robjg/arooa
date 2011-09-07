package org.oddjob.arooa.xml;

import java.io.StringWriter;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.QTag;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.AbstractRuntimeConfiguration;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Converts {@link ArooaHandler#onStartElement(ArooaElement, ArooaContext)}
 * events into an internal DOM.
 * 
 * @author rob
 *
 */
public class XmlHandler2 implements ArooaHandler {

	private final Document document;

	private final Node current;
	
	class XMLRuntime extends AbstractRuntimeConfiguration {
		ArooaContext context;
		Element element;

		XMLRuntime(Element element) {
			this.element = element;
		}

		public ArooaClass getClassIdentifier() {
			return null;
		}

		public void configure() 
		throws ArooaConfigurationException {
			fireBeforeConfigure();
			fireAfterConfigure();
		}

		void addText(String text) {
			Text textNode = document.createCDATASection(text);
			element.appendChild(textNode);
		}

		public void init() 
		throws ArooaConfigurationException {

			fireBeforeInit();
			
			String text = ((XMLConfigurationNode) context.getConfigurationNode()).getText();
			if (text != null) {
				addText(text);
			}
			current.appendChild(element);
			
			fireAfterInit();
		}

		public void destroy() 
		throws ArooaConfigurationException {
			fireBeforeDestroy();
			current.removeChild(element);
			fireAfterDestroy();
		}

		public void setIndexedProperty(String name, int index, Object value) {
			throw new ArooaException(
					"It's not possible to set a propoerty on an XML handler.");
		}

		public void setMappedProperty(String name, String key, Object value) {
			throw new ArooaException(
					"It's not possible to set a propoerty on an XML handler.");
		}

		public void setProperty(String name, Object value)
				throws ArooaException {
			throw new ArooaException(
					"It's not possible to set a propoerty on an XML handler.");
		}
	}

	class XMLContext implements ArooaContext {

		final XMLRuntime runtime;
		final ConfigurationNode runtimeNode;
		final ArooaContext parentContext;

		final Element current;
		
		XMLContext(XMLRuntime runtime, 
				ConfigurationNode runtimeNode, 
				ArooaContext parentContext,
				Element current) {
			this.runtime = runtime;
			this.runtimeNode = runtimeNode;
			this.parentContext = parentContext;
			this.current = current;
		}
		
		public ArooaType getArooaType() {
			return null;
		}

		public ArooaContext getParent() {
			return parentContext;
		}
		
		public ArooaSession getSession() {
			return parentContext.getSession();
		}
		
		public ConfigurationNode getConfigurationNode() {
			return runtimeNode;
		}
		
		public RuntimeConfiguration getRuntime() {
			return runtime;
		}
		
		public PrefixMappings getPrefixMappings() {
			return parentContext.getPrefixMappings();
		}
		
		public ArooaHandler getArooaHandler() {
			return new XmlHandler2(document, current);
		}
	}
	
	public XmlHandler2() {
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		
		builderFactory.setNamespaceAware(true);

		DocumentBuilder builder;
		try {
			builder = builderFactory.newDocumentBuilder();
			this.document = builder.newDocument();
			this.current = document;
		} catch (ParserConfigurationException e) {
			throw new ArooaException(e);
		}
	}

	XmlHandler2(Document out, Element current) {
		this.document = out;
		this.current = current;
	}

	public ArooaContext onStartElement(ArooaElement element,
			ArooaContext parentContext) {

		URI uri = element.getUri();
		String uriString = null;
		if (uri != null) {
			uriString = uri.toString();
		}
		
		PrefixMappings pms = parentContext.getPrefixMappings();

		QTag tag = pms.getQName(element);
		
		Element elementNode = document.createElementNS(
				uriString, tag.toString());
		
		ArooaAttributes attrs = element.getAttributes();
		String[] attributeNames = attrs.getAttributNames();
		for (int i = 0; i < attributeNames.length; ++i) {
			elementNode.setAttribute(
					attributeNames[i], attrs.get(attributeNames[i]));
		}

		XMLConfigurationNode node = new XMLConfigurationNode(
				element);
		
		XMLRuntime runtime = new XMLRuntime(elementNode);
		
		XMLContext ourContext = new XMLContext(
				runtime, 
				node, 
				parentContext,
				elementNode);
		
		runtime.context = ourContext;
		node.setContext(ourContext);
		
		return ourContext;
	}

	public Node getNode() {
		return current;
	}
	
	public String getXml() {
		
		TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        try {
            serializer = tfactory.newTransformer();
            //Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            
            StringWriter writer = new StringWriter();
            serializer.transform(new DOMSource(current), new StreamResult(writer));
            
            return writer.toString();
            
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }	
	}
}
