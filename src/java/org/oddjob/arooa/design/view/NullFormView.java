package org.oddjob.arooa.design.view;

import java.awt.Component;

import javax.swing.JLabel;

public class NullFormView implements SwingFormView {

	public Component cell() {
		return new JLabel("(No Properties)");
	}
	
	public Component dialog() {
		throw new IllegalStateException("No way to create form.");
	}
}
