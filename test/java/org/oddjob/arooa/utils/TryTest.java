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
	public void testTryingCompose() {
		
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
		
		Exception e = new Exception("Doh!");
		
		Try<String> t2 = Try.fail(e);
		
		Try<String> r = t2.map(s -> { throw new RuntimeException("Unexpected"); });

		try {
			r.orElseThrow();
		}
		catch (Exception e2) {
			assertThat(e2, sameInstance(e));
		}
	}

	@Test
	public void testFlatMap() throws Exception {
		
		Try<String> t = Try.of("Foo").flatMap(s -> Try.of(s + "Bar"));
		
		assertThat(t.orElseThrow(), is("FooBar"));
		
		Exception e = new Exception("Doh!");
		
		Try<String> t2 = Try.fail(e);
		
		Try<String> r = t2.flatMap(s -> Try.fail(new RuntimeException("Unexpected")));

		try {
			r.orElseThrow();
		}
		catch (Exception e2) {
			assertThat(e2, sameInstance(e));
		}
	}
	
	@Test
	public void testEquals() {

		Try<String> t1 = Try.of("Hello");
		Try<String> t2 = Try.of("Hello");
		Try<String> t3 = Try.of("Bye");
		Try<String> t4 = Try.of(null);
		Try<String> t5 = Try.of(null);
		
		assertThat(t1.equals(t1), is(true));
		assertThat(t2.equals(t1), is(true));
		assertThat(t3.equals(t1), is(false));
		assertThat(t4.equals(t1), is(false));
		assertThat(t1.equals(t2), is(true));
		assertThat(t1.equals(t3), is(false));
		assertThat(t1.equals(t4), is(false));
		assertThat(t4.equals(t5), is(true));

		assertThat(t2.hashCode() == t1.hashCode(), is(true));
		assertThat(t3.hashCode() == t1.hashCode(), is(false));
		assertThat(t4.hashCode() == t1.hashCode(), is(false));
		assertThat(t4.hashCode() == t5.hashCode(), is(true));
		
		Try<String> f1 = Try.fail(new Exception("E1"));
		Try<String> f2 = Try.fail(new Exception("E2"));

		assertThat(f1.equals(f1), is(true));
		assertThat(f2.equals(f1), is(false));
		
		assertThat(f1.hashCode() == f1.hashCode(), is(true));
		assertThat(f2.hashCode() == f1.hashCode(), is(false));
	}
	
	@Test
	public void testToString() {
		
		assertThat(Try.of("Hello").toString(), is ("Success: Hello"));
		assertThat(Try.fail(new Exception("Doh!")).toString(), is ("Failure: java.lang.Exception: Doh!"));
	}
}
