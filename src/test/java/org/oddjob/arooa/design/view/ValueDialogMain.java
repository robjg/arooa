package org.oddjob.arooa.design.view;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ValueDialogMain {

	public static void main(String[] args) {
		
		JTextArea text = new JTextArea(10, 20);
		
		JScrollPane scroll = new JScrollPane(text);
		
		ValueDialog test = new ValueDialog(scroll);
		
		test.showDialog(null);
	}
	
	
}
