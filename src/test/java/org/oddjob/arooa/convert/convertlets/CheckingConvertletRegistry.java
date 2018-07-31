package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.MockConvertletRegistry;

public class CheckingConvertletRegistry extends MockConvertletRegistry {

	interface Check {
		public <F, T> void check(Class<F> from, Class<T> to, 
				Convertlet<F, T> convertlet) throws Exception;
	}
	
	final Check[] checks;
	int count;
	
	public CheckingConvertletRegistry(Check[] checks) {
		this.checks = checks;
	}
	
	public <F> void registerJoker(Class<F> from, Joker<F> joker) {
		throw new RuntimeException("Unexpected");
	}
	
	public <F, T> void register(Class<F> from, Class<T> to, 
			Convertlet<F, T> convertlet) {
		try {
			checks[count++].check(from, to, convertlet);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
