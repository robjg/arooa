package org.oddjob.arooa.utils;

/**
 * Simple {@link ArooaDelimiter}.
 * 
 * @author rob
 *
 */
public class SimpleDelimiter implements ArooaDelimiter {

	private final String delimiter;

	public SimpleDelimiter(String delimiter) {
		this.delimiter = delimiter;
		
		if (delimiter == null) {
			throw new NullPointerException("No delimiter.");
		}
	}
	
	protected String maybeQuote(String text) {
		return text;
	}
	
	@Override
	public String delimit(Object[] values) {
		StringBuilder buffer = new StringBuilder();
		
		for (int i = 0; i < values.length; ++i) {
		
			if (i > 0) {
				buffer.append(delimiter);
			}
			
			Object value = values[i];
			
			if (value == null) {
				continue;
			}
			
			if (value instanceof String) {
				buffer.append(maybeQuote((String) value));
			}
			else {
				buffer.append(value.toString());
			}
		}

		return buffer.toString();
	}
	
	public String getDelimiter() {
		return delimiter;
	}
}
