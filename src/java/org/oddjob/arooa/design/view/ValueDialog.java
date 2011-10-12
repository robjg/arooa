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
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
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
		
		JPanel all = new JPanel(new BorderLayout());
				
		all.add(form, BorderLayout.CENTER);	

		JPanel selection = new JPanel();
		
		
		final ActionListener enterAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chosen = true;
				dialog.dispose();
			}
		};
		
		JButton ok = new JButton("OK");
		ok.addActionListener(enterAction);

		final ActionListener cancelAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chosen = false;
				dialog.dispose();
			}
		};
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(cancelAction);
		
		selection.add(ok);
		selection.add(cancel);

		KeyStroke escapeStroke = KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0);
		KeyStroke enterStroke = KeyStroke.getKeyStroke(
				KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK);
		
		dialog.getRootPane().registerKeyboardAction(enterAction, 
				enterStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		dialog.getRootPane().registerKeyboardAction(cancelAction, 
				escapeStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		all.add(selection, BorderLayout.PAGE_END);
		
		dialog.getContentPane().add(all);
		dialog.setDefaultCloseOperation(
				WindowConstants.DISPOSE_ON_CLOSE);
		
		dialog.setModal(true);
		
		dialog.pack();
		if (window != null) {
			ScreenPresence screen = new ScreenPresence(window);
			dialog.setLocation(screen.locationToCenter(dialog.getPreferredSize()));
		}
		
		dialog.setVisible(true);
	}
	
	class OK implements Runnable {

		private final Dialog dialog;
		
		public OK(Dialog dialog) {
			this.dialog = dialog;
		}
		
		public void run() {
			chosen = true;
			dialog.dispose();
		}
	}
	
}

