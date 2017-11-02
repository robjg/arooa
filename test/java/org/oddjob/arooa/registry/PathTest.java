/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.registry;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

/**
 * 
 */
public class PathTest extends Assert {

   @Test
	public void testCreate() {
		assertEquals("a", 
				new Path("a").toString());
		assertEquals("a/b/c", 
				new Path("a/b/c").toString());
		assertEquals("a/b/c", 
				new Path("a/b/c/").toString());
		assertEquals("", 
				new Path("").toString());
		assertEquals(0, 
				new Path("").size());
		assertEquals(0, 
				new Path().size());
		assertEquals(new Path(""), 
				new Path(null));
	}
	
   @Test
	public void testAddId() {
		assertEquals("a/b/c", 
				new Path("a/b")
				.addId("c")
				.toString());
		
		assertEquals("a/b/c/d", 
				new Path("a/b")
				.addId("c/d")
				.toString());
		
		assertEquals("a/b", 
				new Path("a/b")
				.addId(null)
				.toString());
		
		assertEquals("", 
				new Path(null)
				.addId(null)
				.toString());
	}

   @Test
	public void testAddPath() {
		assertEquals("a/b/c", 
				new Path("a/b")
				.addPath(new Path("c"))
				.toString());
		
		assertEquals("a/b", 
				new Path("a/b")
				.addPath(null)
				.toString());
		
		assertEquals("", 
				new Path(null)
				.addPath(null)
				.toString());
	}
	
   @Test
	public void testChildPath() {
		assertEquals("b/c", 
				new Path("a/b/c")
				.getChildPath()
				.toString());
		assertEquals(0, 
				new Path("a")
				.getChildPath()
				.size());
		assertNull(
				new Path()
				.getChildPath());
		
	}
	
   @Test
	public void testGetRoot() {
		assertEquals("a", 
				new Path("a/b/c")
				.getRoot());
		assertEquals("b", 
				new Path("b/c")
				.getRoot());
		assertEquals("c", 
				new Path("c")
				.getRoot());
		assertEquals(null, 
				new Path("")
				.getRoot());
	}
	
   @Test
	public void testGetId() {
		assertEquals("c", 
				new Path("a/b/c")
				.getId());
		assertEquals("c", 
				new Path("b/c")
				.getId());
		assertEquals("c", 
				new Path("c")
				.getId());
		assertEquals(null, 
				new Path("")
				.getId());
	}
	
   @Test
	public void testRelative() {
		assertEquals("a",
				new Path()
				.relativeTo(
						new Path("a"))
				.toString());
		
		assertEquals("b/c", 
				new Path("a")
				.relativeTo(
						new Path("a/b/c"))
				.toString());
		assertEquals("c", 
				new Path("a/b")
				.relativeTo(
						new Path("a/b/c"))
				.toString());
		
		assertNull(
				new Path("a/b/d")
				.relativeTo(
						new Path("a/e/c")));
		assertNull(
				new Path("a/b/d")
				.relativeTo(
						new Path("a/b")));
	}

   @Test
	public void testEqualsAndHash() {
		
		Set<Path> set = new HashSet<Path>();
		
		set.add(new Path("a/b/c"));
		
		assertTrue(set.contains(new Path("a/b/c")));
		assertTrue(set.contains(new Path("/a/b/c")));
		assertTrue(set.contains(new Path("a/b/c/")));
		assertFalse(set.contains(new Path("a/b")));
	}
	
}
