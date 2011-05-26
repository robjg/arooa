/*
 * This source code is heavily based on source code from the Apache
 * Ant project. As such the following is included:
 * ------------------------------------------------------------------
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.oddjob.arooa.ArooaException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Collection of helper methods that retrieve a ParserFactory or
 * Parsers and Readers.
 * <p>
 * This class will create only a single factory instance.
 * <p>
 * Based on the original by <b>Stefan Bodewig</b>.
 *
 */
public class JAXPUtils {


    /**
     * Parser factory to use to create parsers.
     * @see #getParserFactory
     *
     */
    private static SAXParserFactory parserFactory = null;

    /**
     * Parser Factory to create Namespace aware parsers.
     *
     */
    private static SAXParserFactory nsParserFactory = null;

    /**
     * Parser factory to use to create document builders.
     *
     */
    private static DocumentBuilderFactory builderFactory = null;

    /**
     * Returns the parser factory to use. Only one parser factory is
     * ever created by this method and is then cached for future use.
     *
     * @return a SAXParserFactory to use
     *
     */
    public static synchronized SAXParserFactory getParserFactory()
        throws ArooaException {

        if (parserFactory == null) {
            parserFactory = newParserFactory();
        }
        return parserFactory;
    }

    /**
     * Returns the parser factory to use to create namespace aware parsers.
     *
     * @return a SAXParserFactory to use which supports manufacture of
     * namespace aware parsers
     *
     */
    public static synchronized SAXParserFactory getNSParserFactory()
        throws ArooaException {

        if (nsParserFactory == null) {
            nsParserFactory = newParserFactory();
            nsParserFactory.setNamespaceAware(true);
        }
        return nsParserFactory;
    }

    /**
     * Returns a new  parser factory instance.
     *
     */
    public static SAXParserFactory newParserFactory() throws ArooaException {

        try {
            return SAXParserFactory.newInstance();
        } catch (FactoryConfigurationError e) {
            throw new ArooaException("XML parser factory has not been "
                                     + "configured correctly: "
                                     + e.getMessage(), e);
        }
    }

    /**
     * Returns a newly created SAX 2 XMLReader, using the default parser
     * factory.
     *
     * @return a SAX 2 XMLReader.
     * @see #getParserFactory
     */
    public static XMLReader getXMLReader() throws ArooaException {
        try {
            return newSAXParser(getParserFactory()).getXMLReader();
        } catch (SAXException e) {
            throw convertToArooaException(e);
        }
    }

    /**
     * Returns a newly created SAX 2 XMLReader, which is namespace aware
     *
     * @return a SAX 2 XMLReader.
     * @see #getParserFactory
     */
    public static XMLReader getNamespaceXMLReader() throws ArooaException {
        try {
            return newSAXParser(getNSParserFactory()).getXMLReader();
        } catch (SAXException e) {
            throw convertToArooaException(e);
        }
    }

    /**
     * Returns a newly created DocumentBuilder.
     *
     * @return a DocumentBuilder
     */
    public static DocumentBuilder getDocumentBuilder() throws ArooaException {
        try {
            return getDocumentBuilderFactory().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ArooaException(e);
        }
    }

    /**
     * @return a new SAXParser instance as helper for getParser and
     * getXMLReader.
     *
     */
    private static SAXParser newSAXParser(SAXParserFactory factory)
         throws ArooaException {
        try {
            return factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new ArooaException("Cannot create parser for the given "
                                     + "configuration: " + e.getMessage(), e);
        } catch (SAXException e) {
            throw convertToArooaException(e);
        }
    }

    /**
     * Translate a SAXException into a ArooaException
     *
     */
    private static ArooaException convertToArooaException(SAXException e) {
        Exception nested = e.getException();
        if (nested != null) {
            return new ArooaException(nested);
        } else {
            return new ArooaException(e);
        }
    }

    /**
     * Obtains the default builder factory if not already.
     *
     */
    private static synchronized 
        DocumentBuilderFactory getDocumentBuilderFactory() 
        throws ArooaException {
        if (builderFactory == null) {
            try {
                builderFactory = DocumentBuilderFactory.newInstance();
            } catch (FactoryConfigurationError e) {
                throw new ArooaException("Document builder factory has not "
                                         + "been configured correctly: "
                                         + e.getMessage(), e);
            }
        }
        return builderFactory;
    }

}
