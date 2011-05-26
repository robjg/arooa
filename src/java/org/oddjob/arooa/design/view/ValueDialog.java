/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Create dialogs for forms.
 */
public class ValueDialog {
	private static final long serialVersionUID = 2008100100;

	private final Component form;
	
	public ValueDialog(Component form) {
		this.form = form;
	}
	
	private boolean chosen;
		
	public boolean isChosen() {
		return chosen;
	}
	
	public void showDialog(Component parent) {
		
		Window window = ViewHelper.getWindowForComponent(parent);
		
		final JDialog dialog;  
		
		if (window instanceof Frame) {
			dialog = new JDialog((Frame) window);
		} else {
			dialog = new JDialog((Dialog) window);
		}
		
		if (window != null) {
			ScreenPresence screen = new ScreenPresence(window).smaller(0.66);
			screen.fit(dialog);
		}
		
		dialog.getContentPane().setLayout(new BorderLayout());
		
		dialog.getContentPane().add(form, BorderLayout.CENTER);	

		JPanel selection = new JPanel();
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				chosen = true;
			}
		});
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				chosen = false;
			}
		});
		
		selection.add(ok);
		selection.add(cancel);

		dialog.getContentPane().add(selection, BorderLayout.PAGE_END);
		
		dialog.setDefaultCloseOperation(
				WindowConstants.DISPOSE_ON_CLOSE);
		
		dialog.setModal(true);
		
		dialog.pack();
		dialog.setVisible(true);
	}
}

