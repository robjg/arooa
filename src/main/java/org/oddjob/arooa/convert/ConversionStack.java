/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;

import java.io.PrintStream;

/**
 * <p>
 * A ConversionStack contains the diagnostics of applying a ConversionPath
 * to an Object.
 *
 */
public interface ConversionStack {

	/**
	 * Get the original ConversionPath that create this ConversionStack.
	 * 
	 * @return A ConversionPath.
	 */
    ConversionPath<?, ?> getConversionPath();
	
	/**
	 * The size of the stack. Will always equal the length of the 
	 * conversion patch that created it.
	 * 
	 * @return The size.
	 */
    int size();
	
	/**
	 * Get stack element info.
	 * 
	 * @param index The 0 based index.
	 * 
	 * @return the Elemement at that position in the stack.
	 */
    Element getElement(int index);
	
	/**
	 * Print the stack trace to a PrintStream.
	 * 
	 * @param out Where to print to.
	 */
    void printStack(PrintStream out);
	
	/**
	 * Get the stack trace as a String.
	 * 
	 * @return The stack trace.
	 */
    String getStackTrace();
	
	/**
	 * The index of the element at which conversion failed.
	 * 
	 * @return The index, -1 if it didn't fail.
	 */
    int getFailedElementIndex();
	
	/**
	 * Holds information about a paticular element in the stack.
	 */
    interface Element {
		
		/**
		 * The from class.
		 * 
		 * @return The from type
		 */
        TypeArooa<?> getFromType();
		
		/**
		 * The to class
         *
		 * @return The to type.
		 */
        TypeArooa<?> getToType();
		
		/**
		 * The object before.
		 * 
		 * @return The object in.
		 */
		Object getBefore();
		
		/**
		 * The converted object.
         *
		 * @return The converted object.
		 */
        Object getConverted();
	}
	
}
