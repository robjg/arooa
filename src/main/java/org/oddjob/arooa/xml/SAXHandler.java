/*
 * This source code is heavily based on source code from the Apache
 * Ant project. As such the following is included:
 * ------------------------------------------------------------------
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000,2002-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Ant" and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.oddjob.arooa.xml;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.ConfigurationNodeEvent;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;
import org.oddjob.arooa.runtime.ModificationRefusedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

/**
 * Handler for ant processing. Uses a stack of AntHandlers to
 * implement each element ( the original parser used a recursive behavior,
 * with the implicit execution stack )
 */
class SAXHandler extends DefaultHandler {
	private static final Logger logger = LoggerFactory.getLogger(SAXHandler.class);
	    
    private Locator locator;
    
    private LinkedList<ArooaContext> contexts = new LinkedList<>();

    private ArooaContext documentContext;
    
    /**
     * Creates a new RootHandler instance.
     *
     */
    public SAXHandler(ArooaContext rootContext) {
    	
       	rootContext.getConfigurationNode().addNodeListener(new ConfigurationNodeListener() {
    		public void childInserted(ConfigurationNodeEvent nodeEvent) {
    			documentContext = nodeEvent.getChild().getContext();
    		}
    		public void childRemoved(ConfigurationNodeEvent nodeEvent) {
    			documentContext = null;
    		}
       		public void insertRequest(ConfigurationNodeEvent nodeEvent)
					throws ModificationRefusedException {
       		}
       		public void removalRequest(ConfigurationNodeEvent nodeEvent)
       				throws ModificationRefusedException {
       		}
    	});
    	
         contexts.push(rootContext);
    }

    /**
     * Handles the start of a project element. A project handler is created
     * and initialised with the element name and attributes.
     *
     * @param uri The namespace uri for this element.
     * @param tag The name of the element being started.
     *            Will not be <code>null</code>.
     * @param qname The qualified name for this element.
     * @param attrs Attributes of the element being started.
     *              Will not be <code>null</code>.
     *
     * @exception org.xml.sax.SAXParseException if the tag given is not
     *                              <code>"project"</code>
     */
    public void startElement(String uri, String tag, String qname, Attributes attrs)
    throws SAXParseException {
    	
		XMLArooaAttributes arooaAttrs = new XMLArooaAttributes(uri, attrs);
		
		ArooaElement element;
		try {
			URI theUri = null;
			if (uri != null && !"".equals(uri.trim())) {
				theUri = new URI(uri);
			}
			element = new ArooaElement(
					theUri, tag, arooaAttrs);
		} 
		catch (URISyntaxException e) {
    		throw new SAXParseException( 
    				e.getMessage(), locator, e);			
		}
		
    	try {
    		ArooaContext context = contexts.peek();
    		
            if (context == null) {
                throw new IllegalStateException(
                        "Context null - this should never happen.");
            }

    		logger.debug("onStartElement(" + qname + "),  handler [" +
    				context.getArooaHandler() + "]");

    		ArooaContext newContext = context.getArooaHandler(
    				).onStartElement(element, context);
    			
    		contexts.push(newContext);    		
    		
    	} catch (Exception e) {
    		throw new SAXParseException("<" + element + ">: " + 
    				e.getMessage(), locator, e);
    	}
    }

    /**
     * Sets the locator in the project helper for future reference.
     *
     * @param locator The locator used by the parser.
     *                Will not be <code>null</code>.
     */
    public void setDocumentLocator(Locator locator) {
    	this.locator = locator;
    }

    /**
     * Handles the end of an element. Any required clean-up is performed
     * by the onEndElement() method and then the original handler
     * is restored to the parser.
     *
     * @exception SAXParseException in case of error (not thrown in
     *                         this implementation)
     *
     */
    public void endElement(String uri, String tag, String qName) 
    throws SAXParseException {
    	try {
    		logger.debug("onEndElement(" + qName + ")");

    		ArooaContext currentContext = contexts.pop();

    		ArooaContext parentContext = contexts.peek();

    		if (parentContext == null) {
                throw new IllegalStateException(
                        "Context is null - this should never happen.");
            }

			ConfigurationNode currentConfigurationNode = currentContext.getConfigurationNode();
			if (currentConfigurationNode == null) {
				throw new IllegalArgumentException("Null Configuration Node for Context " + currentContext);
			}

			// order is important here:
    		// add node before init() so indexed properties
    		// know their index.
    		int index = parentContext.getConfigurationNode()
					.insertChild(currentConfigurationNode);
    		
    		try {
    			currentContext.getRuntime().init();
    		} catch (RuntimeException e) {
    			parentContext.getConfigurationNode().removeChild(index);
    			throw e;
    		}
    		
    	} catch (Exception e) {
    		throw new SAXParseException(e.getMessage(), locator, e);
    	}
    }

    /**
     * Handle text within an element, calls currentHandler.characters.
     *
     * @param buf  A character array of the test.
     * @param start The start offset in the array.
     * @param count The number of characters to read.
     */
    public void characters(char[] buf, int start, int count) {

    	String text = new String(buf, start, count);

    	ArooaContext context = contexts.peek();

        if (context == null) {
            throw new IllegalStateException(
                    "Context is null adding text [" + text + "] - this should never happen.");
        }

        ConfigurationNode configurationNode = context.getConfigurationNode();
        if (configurationNode == null) {
			throw new IllegalStateException(
					"Configuration Node is null adding text [" + text + "] to context [" + context + "].");
		}

        configurationNode.addText(text);
    }

    /**
     * Start a namespace prefix to uri mapping
     *
     * @param prefix the namespace prefix
     * @param uri the namespace uri
     */
    public void startPrefixMapping(String prefix, String uri) 
    throws SAXParseException {
        ArooaContext context = contexts.peek();
        if (context == null) {
            throw new IllegalStateException(
                    "Context is null - this should never happen.");
        }
    	try {
    		context.getPrefixMappings()
                   .put(prefix, new URI(uri));
    	}
    	catch (URISyntaxException e) {
    		throw new SAXParseException(e.getMessage(), locator, e);        	
    	}
    	catch (ArooaException e) {
    		throw new SAXParseException(e.getMessage(), locator, e);        	
        }
    }

    /**
     * End a namepace prefix to uri mapping
     *
     * @param prefix the prefix that is not mapped anymore
     */
    public void endPrefixMapping(String prefix) {
    	
    }

    public ArooaContext getDocumentContext() {
    	return documentContext;
    }


}
