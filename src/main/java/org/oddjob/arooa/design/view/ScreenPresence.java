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
	
	/**
	 * Fit a component to the location and size of this 
	 * screen presence.
	 * 
	 * @param component
	 */
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
	
	/**
	 * Return the location relative to this screen area that would
	 * centre something of the given size within this screen area..
	 * 
	 * @param size
	 * @return
	 */
	public Point locationToCenter(Dimension size) {
		
		int width = (int) size.getWidth();
		int height = (int) size.getHeight();
		
		int x = (int) (location.getX() + 
				(this.size.getWidth() - width) / 2);
		int y = (int) (location.getY() + 
				(this.size.getHeight() - height) / 2);
		
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		
		return new Point(x, y);
	}
	
	/**
	 * Create ScreenPresence a factor the size of this. The location will 
	 * be adjusted so that it is offset equally in both x and y direction
	 * from this ScreenPresence.
	 * 
	 * @param factor The factor to size the new ScreenPresence by.
	 * 
	 * @return
	 */
	public ScreenPresence smaller(double factor) {
		int width = (int) (size.getWidth() * factor);
		int height = (int) (size.getHeight() * factor);
		
		Dimension size = new Dimension(width, height);
		
		return new ScreenPresence(
				locationToCenter(size), 
				size);
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
