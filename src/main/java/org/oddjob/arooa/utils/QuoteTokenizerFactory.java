package org.oddjob.arooa.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a tokenizer where the token is a regular expression but where
 * a quote character and escape character can be used.
 * 
 * @author rob
 *
 */
public class QuoteTokenizerFactory implements ArooaTokenizerFactory {

	private final String regexp;
	
	private final String quote;
	
	private final String escape; 

	public QuoteTokenizerFactory(String regexp, char quote, char escape) {
		// Validate regexp.
		Pattern.compile(regexp);
		
		this.regexp = regexp;
		this.quote = Pattern.quote(new String(new char[] { quote }));
		this.escape = Pattern.quote(new String(new char[] { escape }));;
	}
	
	public ArooaTokenizer newTokenizer() {
		
		return new ParserImpl();		
	}
	
	public String getPattern() {
		
		return "(?:" + quote +
				"((?>[^" + escape + quote + "]|" + 
				escape + "[^" + escape + quote + "]|" +
				escape + escape + "|" +
				escape + quote + ")*?)" +
				"(?:" + quote + regexp + "|" + quote + "?($)))|" +
				"(?:(.*?)(?:" + regexp + "|($)))";
	}
	
	class ParserImpl implements ArooaTokenizer {
		
		private final Pattern pattern;
		
		private final Pattern escapeReplace;
		
		public ParserImpl() {
			
			pattern = Pattern.compile(getPattern());

			escapeReplace = Pattern.compile(escape + 
					"(" + quote + "|" + escape + ")");
		}
	
		public String[] parse(String text) throws ParseException {
			
			List<String> results = new ArrayList<String>();
					
			Matcher matcher = pattern.matcher(text);
			
			boolean eol = false;
			do {
				if (!matcher.lookingAt()) {
					throw new ParseException("Unable to match [" + text.substring(
							matcher.regionStart(), matcher.regionEnd() ) + "]",
							matcher.regionEnd());
				}
				String foundQuoted = matcher.group(1);
				if (foundQuoted != null) {
					results.add(replaceEscaped(foundQuoted));
					eol = matcher.group(2) != null;
				}
				else {
					results.add(matcher.group(3));
					eol = matcher.group(4) != null;					
				}
				
				matcher.region(matcher.end(), matcher.regionEnd());

			} while (!eol);
					
			return results.toArray(new String[results.size()]);
		}
	
		String replaceEscaped(String input) {
			
			Matcher matcher = escapeReplace.matcher(input);

			return matcher.replaceAll("$1");
		}
	}
}
