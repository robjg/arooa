package org.oddjob.arooa.design.view;

import java.awt.Dimension;
import java.awt.Point;

import junit.framework.TestCase;

public class ScreenPresenceTest extends TestCase {

	public void testSmaller() {
		
		ScreenPresence test = new ScreenPresence(
				new Point(70, 0),
				new Dimension(100, 200));
		
		ScreenPresence result = test.smaller(0.8);
		
		assertEquals(80.0, result.getLocation().getX(), 0.001);
		assertEquals(20.0, result.getLocation().getY(), 0.001);
		assertEquals(80.0, result.getSize().getWidth(), 0.001);
		assertEquals(160.0, result.getSize().getHeight(), 0.001);
	}
	
}
