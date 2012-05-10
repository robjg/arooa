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
import java.util.concurrent.Callable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

/**
 * Create dialogs for forms.
 */
public class ValueDialog {
	private static final Logger logger = Logger.getLogger(ValueDialog.class);

	/** The form. */
	private final Component form;
	
	/** Called when OK selected. */
	private final Callable<Boolean> okAction;

	private boolean chosen;

	/**
	 * Default OK action set the chosen flag.
	 * 
	 * @param form
	 */
	public ValueDialog(Component form) {
		this(form, null);
	}
	
	/**
	 * Provide an action for when OK selected.
	 * 
	 * @param form
	 * @param okAction
	 */
	public ValueDialog(Component form, Callable<Boolean> okAction) {
		this.form = form;
		if (okAction == null) {
			this.okAction = new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return true;
				}
			};
		}
		else {
			this.okAction = okAction;
		}
	}
	
	public boolean isChosen() {
		return chosen;
	}
	
	/**
	 * Show the dialogue.
	 * 
	 * @param parent The parent component/frame.
	 */
	public void showDialog(Component parent) {
		showDialog(parent, false);
	}
	
	/**
	 * This is used by designer sub dialogues because there is no
	 * way to cancel a change. This implementation is a quick and dirty
	 * and needs re-thinking.
	 * 
	 * @param parent The parent component/frame.
	 * @param hideCancel Hide the cancel button.
	 */
	public void showDialog(Component parent, boolean hideCancel) {
			
		
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
				try {
					chosen = okAction.call();
				}
				catch (Exception ex) {
					if (logger.isDebugEnabled()) {
						logger.debug("OK Action failed.", ex);
					}
					DialogueHelper.showExceptionMessage(form, ex);
					chosen = false;					
				}
				if (chosen) {
					dialog.dispose();
				}
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
		if (!hideCancel) {
			selection.add(cancel);
		}

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
	
}

