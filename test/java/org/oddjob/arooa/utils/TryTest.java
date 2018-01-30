package org.oddjob.arooa.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TryTest {

	@Test
	public void testTrying() {
		
		Exception e = new Exception("Fail");
		
		Try<String> t = Try.of("Foo").trying(s -> { throw e; });
		
		try {
			t.orElseThrow();
			fail("should throw.");
		}
		catch (Exception e2) {
			assertThat(e2, sameInstance(e));
		}
	}
	
	@Test
	public void testMap() throws Exception {
		
		Try<String> t = Try.of("Foo").map(s -> s + "Bar");
		
		assertThat(t.orElseThrow(), is("FooBar"));
	}

	
}
