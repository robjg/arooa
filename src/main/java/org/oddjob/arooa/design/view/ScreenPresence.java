package org.oddjob.arooa.design.view;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;

/**
 * Represent location and size information for a screen
 * component. Mutable so that the position can be updated and saved.
 * 
 * @author rob
 *
 */
public class ScreenPresence implements Serializable {
	private static final long serialVersionUID = 2022092100L;

	private static final int PREFERRED_HEIGHT = 600;

	private static final int PREFERRED_WIDTH = 800;

	private volatile Point location;
	
	private volatile Dimension size;

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

	public void adjustTo(Component component) {
		this.location = component.getLocation();
		this.size = component.getSize();
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
	
	public static ScreenPresence defaultSize(Dimension screenSize) {

		if (screenSize.getWidth() <= 800 || screenSize.getHeight() <= 600) {
			return new ScreenPresence(new Point(0, 0), screenSize);
		}
		else {
			int x = (int) (screenSize.getWidth() - PREFERRED_WIDTH) / 2;
			int y = (int) (screenSize.getHeight() - PREFERRED_HEIGHT) / 2;

			return new ScreenPresence(new Point(x, y), new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
		}
	}

	public static ScreenPresence defaultSize() {

		return defaultSize(Toolkit.getDefaultToolkit().getScreenSize());
	}

	/**
	 * Custom de-serialisation.
	 */
	private Object readResolve()
			throws IOException, ClassNotFoundException {

		Dimension wholeScreen = Toolkit.getDefaultToolkit().getScreenSize();

		if (location.getX() > (wholeScreen.getWidth() - 100) ||
			location.getY() > (wholeScreen.getHeight() - 100)) {

			return defaultSize(wholeScreen);
		}
		else {
			return this;
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": location=(" + location.getX() + 
				", " + location.getY() + ", size=(" + size.getWidth() +
				", " + size.getHeight();
	}
}
