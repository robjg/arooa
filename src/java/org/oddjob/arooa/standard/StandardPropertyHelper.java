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

package org.oddjob.arooa.standard;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.runtime.Evaluator;
import org.oddjob.arooa.runtime.ExpressionParser;
import org.oddjob.arooa.runtime.ParsedExpression;
import org.oddjob.arooa.runtime.PropertyFirstEvaluator;
import org.oddjob.arooa.runtime.SubstitutionPolicy;


/**
 * Used for replacing properties in strings.
 * <p>
 * Based on an original from <b>Ant</b>.
 */

public class StandardPropertyHelper implements ExpressionParser {

	private final SubstitutionPolicy substitutionPolicy;
	
	private final Evaluator evaluator = new PropertyFirstEvaluator();
	
    /**
     * Default constructor.
     */
    public StandardPropertyHelper(
			SubstitutionPolicy substitutionPolicy) {
    	this.substitutionPolicy = substitutionPolicy;
    }

    public StandardPropertyHelper() {
    	this(new SubstitutionPolicy() {
				public <T> T substituteObject(T value) {
					return value;
				}
				public String substituteString(String value) {
					return value;
				}
			}
    	);
    }

    public ParsedExpression parse(String expression) {
		if (expression == null) {
			throw new NullPointerException("Null Expression.");
		}

    	Vector<String> fragments = new Vector<String>();
    	Vector<String> propertyRefs = new Vector<String>();
    	
		parsePropertyString(expression, fragments, propertyRefs);
		
		// return an object if there was only one ref and no fragments.
		if (propertyRefs.size() == 1 && fragments.size() == 1) {
			String propertyName = (String)propertyRefs.elementAt(0);
			return new SinglePropertyOnly(propertyName);
		}
		else {
			return new StandardPropertyEvaluator(fragments, propertyRefs);
		}
    }
    
	/**
	 * Parses a string containing <code>${xxx}</code> style property
	 * references into two lists. The first list is a collection
	 * of text fragments, while the other is a set of string property names.
	 * <code>null</code> entries in the first list indicate a property
	 * reference from the second list.
	 *
	 * It can be overridden with a more efficient or customized version.
	 *
	 * @param value     Text to parse. Must not be <code>null</code>.
	 * @param fragments List to add text fragments to.
	 *                  Must not be <code>null</code>.
	 * @param propertyRefs List to add property names to.
	 *                     Must not be <code>null</code>.
	 *
	 * @exception ArooaException if the string contains an opening
	 *                           <code>${</code> without a closing
	 *                           <code>}</code>
	 */

	private void parsePropertyString(String value, 
			List<String>fragments, List<String> propertyRefs)
	throws ArooaException {
		int prev = 0;
		int pos;
		//search for the next instance of $ from the 'prev' position
		while ((pos = value.indexOf("$", prev)) >= 0) {

			//if there was any text before this, add it as a fragment
			//TODO, this check could be modified to go if pos>prev;
			//seems like this current version could stick empty strings
			//into the list
			if (pos > 0) {
				fragments.add(value.substring(prev, pos));
			}
			//if we are at the end of the string, we tack on a $
			//then move past it
			if (pos == (value.length() - 1)) {
				fragments.add("$");
				prev = pos + 1;
			} else if (value.charAt(pos + 1) != '{') {
				//peek ahead to see if the next char is a property or not
				//not a property: insert the char as a literal
				/*
				fragments.addElement(value.substring(pos + 1, pos + 2));
				prev = pos + 2;
				*/
				if (value.charAt(pos + 1) == '$') {
					//backwards compatibility two $ map to one mode
					fragments.add("$");
					prev = pos + 2;
				} else {
					//new behaviour: $X maps to $X for all values of X!='$'
					fragments.add(value.substring(pos, pos + 2));
					prev = pos + 2;
				}

			} else {
				//property found, extract its name or bail on a typo
				int endName = value.indexOf('}', pos);
				if (endName < 0) {
					throw new ArooaException("Syntax error in property: "
												 + value);
				}
				String propertyName = value.substring(pos + 2, endName);
				fragments.add(null);
				propertyRefs.add(propertyName);
				prev = endName + 1;
			}
		}
		//no more $ signs found
		//if there is any tail to the file, append it
		if (prev < value.length()) {
			fragments.add(value.substring(prev));
		}
	}
	
	class StandardPropertyEvaluator implements ParsedExpression {
		
		private final Vector<String> fragments;
		
		private final Vector<String> propertyRefs;
	
		public StandardPropertyEvaluator(Vector<String> fragments, Vector<String> propertyRefs) {
			
			this.fragments = fragments;
			this.propertyRefs = propertyRefs;
		}
		
		
	    /**
	     * Replaces <code>${xxx}</code> style constructions in the given value
	     * with the string value of the corresponding data types.
	     *
	     * @param thing The string to be scanned for property references.
	     *              May be <code>null</code>, in which case this
	     *              method returns immediately with no effect.
	     * @param keys  Mapping (String to String) of property names to their
	     *              values. If <code>null</code>, only project properties will
	     *              be used.
	     *
	     * @exception ArooaException if the string contains an opening
	     *                           <code>${</code> without a closing
	     *                           <code>}</code>
	     * @return the original string with the properties replaced, or
	     *         <code>null</code> if the original string is <code>null</code>.
	     */
		@Override
		public <T> T evaluate(ArooaSession session, Class<T> type) 
		throws ArooaConversionException {
	    	
	        StringBuffer sb = new StringBuffer();
	        
	        Enumeration<String> i = fragments.elements();
	        Enumeration<String> j = propertyRefs.elements();
	
	        while (i.hasMoreElements()) {
	            String fragment = (String) i.nextElement();
	            if (fragment == null) {
	                String propertyName = (String) j.nextElement();
	                String converted = evaluator.evaluate(
		                		propertyName, session, String.class);
	                if (converted == null) {
	                	converted = "";
	                }
	                String replacement = substitutionPolicy.substituteString((String) converted); 
	
	                fragment = (replacement != null)
	                        ? replacement.toString()
	                        : "${" + propertyName + "}";
	            }
	            sb.append(fragment);
	        }

	        ArooaConverter converter = session.getTools().getArooaConverter();
	    
	        return converter.convert(sb.toString(), type);
	    }
	
		/**
		 * Is the property constant. i.e. it doesn't contain any ${} type things.
		 * 
		 * @return True if the property is constant.
		 */
		@Override
		public boolean isConstant() {
			return propertyRefs.size() == 0;
		}
	
	}

	/**
	 * A {@link ParsedExpression} for 
	 */
	class SinglePropertyOnly implements ParsedExpression {
		
		private final String propertyExpression;
	
		public SinglePropertyOnly(String propertyExpression) {
			this.propertyExpression = propertyExpression;
		}
		
		@Override
		public <T> T evaluate(ArooaSession session, Class<T> type) 
		throws ArooaConversionException {
			
			T replacement = evaluator.evaluate(propertyExpression, session, type);
			
            return substitutionPolicy.substituteObject(replacement);
		}
		
		@Override
		public boolean isConstant() {
			return false;
		}
		
	}
}