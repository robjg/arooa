package org.oddjob.arooa.design;

import java.awt.GraphicsEnvironment;

import junit.framework.TestCase;

public class ClipboardHelperTest extends TestCase {

	public void testRoundTrip() {
		
		if (GraphicsEnvironment.isHeadless()) {
			return;
		}
		
		ClipboardHelper test = new ClipboardHelper();
		
		test.setText("Hello");
		
		assertEquals("Hello", test.getText());
	}
}
