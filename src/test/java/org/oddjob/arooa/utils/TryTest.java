package org.oddjob.arooa.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class TryTest {

	@Test
	public void testTryingWithCaughtException() {
		
		Exception e = new Exception("Fail");
		
		Try<String> t = Try.of("Foo").trying(s -> { throw e; });
		
		try {
			t.orElseThrow();
			fail("should throw.");
		}
		catch (Exception e2) {
			assertThat(e2.getCause(), sameInstance(e));
		}
	}
	
	@Test
	public void testTryingWithRuntimeException() {
		
		RuntimeException e = new RuntimeException("Fail");

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
	public void testMap() {
		
		Try<String> t = Try.of("Foo").map(s -> s + "Bar");
		
		assertThat(t.orElseThrow(), is("FooBar"));
		
		Exception e = new Exception("Doh!");
		
		Try<String> t2 = Try.fail(e);
		
		Try<String> r = t2.map(s -> { throw new RuntimeException("Unexpected"); });

		try {
			r.orElseThrow(Function.identity());
			fail("should throw.");
		}
		catch (Throwable e2) {
			assertThat(e2, sameInstance(e));
		}
	}
	
	@Test
	public void testMapFailure() {

		assertThat( Try.of("Foo")
						.mapFailure(Function.identity())
						.orElseThrow(), 
					is ("Foo"));
		
		Try<String> t2 = Try.fail(new Exception("Doh!"));
		
		Try<String> r = t2.mapFailure(e -> new Exception("Really Doh!"));

		
		try {
			r.orElseThrow(e -> (Exception) e);
			fail("should throw.");
		}
		catch (Exception e2) {
			assertThat(e2.getMessage(), is("Really Doh!"));
		}
	}
	
	@Test
	public void testFlatMap() {
		
		Try<String> t = Try.of("Foo").flatMap(s -> Try.of(s + "Bar"));
		
		assertThat(t.orElseThrow(), is("FooBar"));
		
		Exception e = new Exception("Doh!");
		
		Try<String> t2 = Try.fail(e);
		
		Try<String> r = t2.flatMap(s -> Try.fail(new RuntimeException("Unexpected")));

		try {
			r.orElseThrow(ex -> (Exception) ex);
			fail("should throw.");
		}
		catch (Exception e2) {
			assertThat(e2, sameInstance(e));
		}
	}
	
	public void testRecover() {
		
		Try<String> success = Try.of("good");
		
		assertThat(success.recover(e -> "cool"), is("good"));		

		Try<String> fail = Try.fail(new Exception("Doh!"));
		
		assertThat(fail.recover(e -> "cool"), is("cool"));		
	}
	
	public void testOnSuccess() {

		
		Try<String> success = Try.of("good");

		AtomicReference<String> goodResult = new AtomicReference<>();
		AtomicReference<Throwable> badResult= new AtomicReference<>();
		
		success.onSuccess(goodResult::set);
	
		assertThat(goodResult.get(), is("good"));		
		assertThat(badResult.get(), CoreMatchers.nullValue());		

	}
	
	public void testOnFailure() {
		
		AtomicReference<Throwable> badResult= new AtomicReference<>();
		
		Try<String> fail = Try.fail(new Exception("Doh!"));

		Try<String> next = fail.onFailure(badResult::set);
		
		assertThat(badResult.get().getMessage(), is("Doh!") );
		
		try {
			next.orElseThrow();
			fail("should throw.");
		}
		catch (Exception e) {
			// expected 
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
