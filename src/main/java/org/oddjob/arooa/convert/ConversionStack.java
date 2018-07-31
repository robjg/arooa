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
	public ConversionPath<?, ?> getConversionPath();
	
	/**
	 * The size of the stack. Will always equal the length of the 
	 * conversion patch that created it.
	 * 
	 * @return The size.
	 */
	public int size();
	
	/**
	 * Get stack element info.
	 * 
	 * @param index The 0 based index.
	 * 
	 * @return the Elemement at that position in the stack.
	 */
	public Element getElement(int index);
	
	/**
	 * Print the stack trace to a PrintStream.
	 * 
	 * @param out
	 */
	public void printStack(PrintStream out);
	
	/**
	 * Get the StackTrace as a Sring.
	 * 
	 * @return
	 */
	public String getStackTrace();
	
	/**
	 * The index of the element at which conversion failed.
	 * 
	 * @return The index, -1 if it didn't fail.
	 */
	public int getFailedElementIndex();
	
	/**
	 * Holds information about a paticular element in the stack.
	 */
	public interface Element {
		
		/**
		 * The from class.
		 * 
		 * @return
		 */
		public Class<?> getFromClass();
		
		/**
		 * The to class
		 * @return
		 */
		public Class<?> getToClass();
		
		/**
		 * The object before.
		 * 
		 * @return
		 */
		public Object getBefore();
		
		/**
		 * The converted object.
		 * @return
		 */
		public Object getConverted();
	}
	
}
