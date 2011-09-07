package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.Serializable;

/**
 * Represent location and size information for a screen
 * component.
 * 
 * @author rob
 *
 */
public class ScreenPresence implements Serializable {
	private static final long serialVersionUID = 2011042600L;
	
	private final Point location;
	
	private final Dimension size;

	public ScreenPresence(Point position, Dimension size) {
		this.location = position;
		this.size = size;
	}
	
	public ScreenPresence(Component component) {
		this.location = component.getLocation();
		this.size = component.getSize();
	}
	
	public void fit(Component component) {
		component.setLocation(location);
		component.setSize(size);
	}
	
	public Point getLocation() {
		return location;
	}

	public Dimension getSize() {
		return size;
	}
	
	public ScreenPresence smaller(double factor) {
		int width = (int) (size.getWidth() * factor);
		int height = (int) (size.getHeight() * factor);
		
		int x = (int) (location.getX() + 
				(size.getWidth() - width) / 2);
		int y = (int) (location.getY() + 
				(size.getHeight() - height) / 2);
		
		return new ScreenPresence(new Point(x, y), 
				new Dimension(width, height));
	}
	
	public static ScreenPresence wholeScreen() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		return new ScreenPresence(new Point(0, 0), dim);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": location=(" + location.getX() + 
				", " + location.getY() + ", size=(" + size.getWidth() +
				", " + size.getHeight();
	}
}
