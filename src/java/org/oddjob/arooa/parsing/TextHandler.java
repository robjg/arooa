package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaException;

public class TextHandler {
	
    private final StringBuilder characters = new StringBuilder();
    
    public void addText(String text) throws ArooaException {
        if (text.trim().length() == 0) {
            return;
        }
        
        characters.append(text);
    }    	

    public String getText() {
    	
    	if (characters.length() == 0) {
    		return null;
    	}
    	return characters.toString();
    }
    
    public int length() {
    	return characters.length();
    }

}
