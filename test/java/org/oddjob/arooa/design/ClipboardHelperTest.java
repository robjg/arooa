package org.oddjob.arooa.design;

import org.junit.Test;

import java.awt.GraphicsEnvironment;

import org.junit.Assert;

public class ClipboardHelperTest extends Assert {

   @Test
	public void testRoundTrip() {
		
		if (GraphicsEnvironment.isHeadless()) {
			return;
		}
		
		ClipboardHelper test = new ClipboardHelper();
		
		test.setText("Hello");
		
		assertEquals("Hello", test.getText());
	}
}
