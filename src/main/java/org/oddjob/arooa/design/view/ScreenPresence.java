package org.oddjob.arooa.design.view;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represent location and size information for a screen
 * component.
 * 
 * @author rob
 *
 */
public class ScreenPresence implements Serializable {
	private static final long serialVersionUID = 2022092100L;

	private static final int PREFERRED_HEIGHT = 600;

	private static final int PREFERRED_WIDTH = 800;

	private final int x;

	private final int y;

	private final int width;

	private final int height;

	private ScreenPresence(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}


	public static class AtPoint {

		private final int x;

		private final int y;

		private AtPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public ScreenPresence ofSize(int width, int height) {
			return new ScreenPresence(x, y, width, height);
		}
	}

	public static class OfSize {

		private final int width;

		private final int height;

		private OfSize(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public ScreenPresence atLocation(int x, int y) {
			return new ScreenPresence(x, y, width, height);
		}
	}

	public static AtPoint atLocation(int x, int y) {
		return new AtPoint(x, y);
	}

	public static OfSize ofSize(int width, int height) {
		return new OfSize(width, height);
	}

	/**
	 * Fit a component to the location and size of this 
	 * screen presence.
	 * 
	 * @param component The componennt.
	 */
	public void resize(Component component) {
		component.setLocation(x, y);
		component.setSize(width, height);
	}

	/**
	 * Get the location of this screen presence.
	 *
	 * @return The location. Never null.
	 */
	public Point getLocation() {
		return new Point(x, y);
	}

	/**
	 * Get the size of this screen presence.
	 *
	 * @return The size. Never null.
	 */
	public Dimension getSize() {
		return new Dimension(width, height);
	}
	
	/**
	 * Return the location relative to this screen area that would
	 * centre something of the given size within this screen area.
	 * If the width of the component is wider than this screen presence it will be positioned at
	 * the left of this screen presences. If the height is greater than this screen presence
	 * it will be positioned at the top of this screen presence.
	 * 
	 * @param size The size of the thing to position.
	 * @return The position of the thing to be centered relative to this screen presence.
	 */
	public Point locationToCenter(Dimension size) {
		
		final int width = (int) size.getWidth();
		final int height = (int) size.getHeight();

		int x;
		int y;

		if (width < this.width) {
			x = this.x + (this.width - width) / 2;
		}
		else {
			x = this.x;
		}

		if (height < this.height) {
			y = this.y + (this.height - height) / 2;
		}
		else {
			y = this.y;
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
	 * @return A new screen presence resized relative to this one.
	 */
	public ScreenPresence resize(double factor) {
		int width = (int) (this.width * factor);
		int height = (int) (this.height * factor);
		
		Dimension size = new Dimension(width, height);

		Point location = locationToCenter(size);
		return new ScreenPresence(location.x, location.y,
				size.width, size.height);
	}

	/**
	 * Is this screen presence currently on any screen currently attached. When working on a
	 * laptop that is moved between docking stations a screen presence that was visible may now
	 * not be.
	 *
	 * @return True if this screen presence top left location is on any screen.
	 */
	public boolean isOnAnyScreen() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] sd = ge.getScreenDevices();

		for (GraphicsDevice gd : sd) {
			Rectangle bounds = gd.getDefaultConfiguration().getBounds();

			if (bounds.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Create a screen presence the size of the given component.
	 *
	 * @param component The component to create a screen presence from.
	 * @return A screen presence. Never null.
	 */
	public static ScreenPresence of(Component component) {
		Point location = component.getLocation();
		Dimension size = component.getSize();
		return new ScreenPresence(location.x, location.y, size.width, size.height);
	}

	/**
	 * Create a screen presence the default size within the give dimensions.
	 *
	 * @param screenSize The dimensions in which to fit the default screen presence.
	 * @return A screen presence fitting within the given screen size.
	 */
	public static ScreenPresence defaultSize(Dimension screenSize) {

		if (screenSize.getWidth() <= PREFERRED_WIDTH || screenSize.getHeight() <= PREFERRED_HEIGHT) {
			return new ScreenPresence(0, 0, screenSize.width, screenSize.height);
		}
		else {
			int x = (int) (screenSize.getWidth() - PREFERRED_WIDTH) / 2;
			int y = (int) (screenSize.getHeight() - PREFERRED_HEIGHT) / 2;

			return new ScreenPresence(x, y, PREFERRED_WIDTH, PREFERRED_HEIGHT);
		}
	}

	/**
	 * Provide a screen presence the default size for the current screen.
	 *
	 * @return A new screen presence. Never null.
	 */
	public static ScreenPresence defaultSize() {

		return defaultSize(Toolkit.getDefaultToolkit().getScreenSize());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ScreenPresence that = (ScreenPresence) o;
		return x == that.x && y == that.y && width == that.width && height == that.height;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, width, height);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": location=(" + x +
				", " + y + "), size=(" + width + ", " + height + ")";
	}
}
