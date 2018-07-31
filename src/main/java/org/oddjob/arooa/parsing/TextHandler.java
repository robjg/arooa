package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaException;

/**
 * Handles accumulating text from XML text nodes during parsing.
 * 
 * @author rob
 *
 */
public class TextHandler {
	
    private final StringBuilder characters = new StringBuilder();
    
    /**
     * Add text but only if it isn't just whitespace.
     * 
     * @param text
     * @throws ArooaException
     */
    public void addText(String text) throws ArooaException {
        if (text.trim().length() == 0) {
            return;
        }
        
        characters.append(text);
    }    	

    /**
     * Get the text or null if no text has been set.
     * 
     * @return
     */
    public String getText() {
    	
    	if (characters.length() == 0) {
    		return null;
    	}
    	return characters.toString();
    }
    
    /**
     * Get the length of the text.
     * 
     * @return The number of characters of text.
     */
    public int length() {
    	return characters.length();
    }

}
