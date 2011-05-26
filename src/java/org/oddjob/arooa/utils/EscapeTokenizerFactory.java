package org.oddjob.arooa.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@link ArooaTokenizerFactory} that provides a tokenizer where the
 * token is a regular expression which can be escaped with an escape character.
 * <p>
 * The escape character may also be escaped with itself.
 * 
 * @author rob
 *
 */
public class EscapeTokenizerFactory implements ArooaTokenizerFactory {

	private final String regexp;
	
	private final String escape; 

	public EscapeTokenizerFactory(String regexp, char escape) {
		// Validate regexp.
		Pattern.compile(regexp);
		
		this.regexp = regexp;
		this.escape = Pattern.quote(new String(new char[] { escape }));;
	}
	
	public ArooaTokenizer newTokenizer() {
		
		return new ParserImpl();		
	}
	
	public String getPattern() {
		
		return "((?>[^" + escape + "]|" +
					escape + escape + "|" + 
					escape + "(?!" + regexp + ")|" + 
					escape + regexp 
						+ ")*?)" +
				"(?:" + regexp + "|($))";
	}
	
	class ParserImpl implements ArooaTokenizer {
		
		private final Pattern pattern;
		
		private final Pattern escapeReplace;
		
		public ParserImpl() {
			
			pattern = Pattern.compile(getPattern());

			escapeReplace = Pattern.compile(escape + 
					"(" + regexp + "|" + escape +")");
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
				String found = matcher.group(1);

				results.add(replaceEscaped(found));
				
				eol = matcher.group(2) != null;
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
