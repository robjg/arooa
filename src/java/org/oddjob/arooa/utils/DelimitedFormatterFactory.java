package org.oddjob.arooa.utils;


public class DelimitedFormatterFactory {

	private String delimiter;
	
	private Character quote;
	
	private Character escape;
	
	private boolean alwaysQuote;
	
	interface QuoteStrategy {
		
		public String maybeQuote(String text);
	}
	
	class NoQuote implements QuoteStrategy {
		
		@Override
		public String maybeQuote(String text) {
			return text;
		}
	}
	
	class AlwaysQuote implements QuoteStrategy {
		
		@Override
		public String maybeQuote(String text) {
	
			Character escape = DelimitedFormatterFactory.this.escape;
			if (escape == null) {
				escape = quote;
			}
			StringBuilder builder = new StringBuilder();
			builder.append(quote);
			builder.append(text.replace(quote.toString(), 
					new String(new char[] { escape.charValue(), quote.charValue() })));
			builder.append(quote);
			
			return builder.toString();
		}
		
	}
	
	class QuoteWhenNeeded implements QuoteStrategy {
		
		@Override
		public String maybeQuote(String text) {
			
			if (text.contains(quote.toString())) {
				return new AlwaysQuote().maybeQuote(text);
			}
			else {
				return text;
			}
		}
	}
	
	public DelimitedFormatter newWriter() {
		
		if (delimiter == null) {
			throw new IllegalStateException("No Delimiter.");
		}

		final QuoteStrategy quoteStrategy;
		if (quote == null) {
			quoteStrategy = new NoQuote();
		}
		else if (alwaysQuote) {
			quoteStrategy = new AlwaysQuote();	
		}
		else {
			quoteStrategy = new QuoteWhenNeeded();
		}
		
		return new DelimitedFormatter() {
			
			@Override
			public String format(Object[] values) {
				
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
						buffer.append(
								quoteStrategy.maybeQuote((String) value));
					}
					else {
						buffer.append(value.toString());
					}
				}

				return buffer.toString();
			}
		};
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public Character getQuote() {
		return quote;
	}

	public void setQuote(Character quote) {
		this.quote = quote;
	}

	public Character getEscape() {
		return escape;
	}

	public void setEscape(Character escape) {
		this.escape = escape;
	}

	public boolean isAlwaysQuote() {
		return alwaysQuote;
	}

	public void setAlwaysQuote(boolean alwaysQuote) {
		this.alwaysQuote = alwaysQuote;
	}
}
