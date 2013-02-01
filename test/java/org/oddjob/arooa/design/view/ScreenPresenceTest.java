package org.oddjob.arooa.design.view;

import java.awt.Dimension;
import java.awt.Point;

import junit.framework.TestCase;

public class ScreenPresenceTest extends TestCase {

	public void testLocationToCenter() {
		
		ScreenPresence test = new ScreenPresence(
				new Point(400, 600),
				new Dimension(100, 200));
		
		Point point = test.locationToCenter(
				new Dimension(50, 100));
		
		assertEquals(425.0, point.getX());
		assertEquals(650.0, point.getY());
	}
	
	public void testCenterNotOffScreen() {
		
		ScreenPresence test = new ScreenPresence(
				new Point(0, 0),
				new Dimension(100, 200));
		
		Point point = test.locationToCenter(
				new Dimension(150, 280));
		
		assertEquals(0.0, point.getX(), 0.01);
		assertEquals(0.0, point.getY(), 0.01);
	}
	
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
