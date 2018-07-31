/*
 * 
 * 
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * 
 */
public class RadioActions extends JPanel {
	private static final long serialVersionUID = 2008100100;
	
	private Action selected;
	private boolean result;
	
	public RadioActions(final Action[] actions) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		ButtonGroup group = new ButtonGroup();
		
		class ButtonAction implements ActionListener {
			int i;
			ButtonAction(int i) {
				this.i = i;
			}
			public void actionPerformed(ActionEvent e) {
				selected = actions[i];
			}
			
		};
		
		for (int i = 0; i < actions.length; ++i) {
			JRadioButton button = new JRadioButton((String) actions[i].getValue(Action.NAME));
			button.addActionListener(new ButtonAction(i));
			group.add(button);
			add(button);
			if (i == 0) {
				button.setSelected(true);
				selected = actions[i];
			}
		}
	}
	
	public Action getSelected() {
		return selected;
	}
	
	public static boolean showDialog(final Component parent, Action[] actions) {
		Window w = ViewHelper.getWindowForComponent(parent);

		final JDialog dialog;
		if (w instanceof Frame) {
			dialog = new JDialog((Frame) w);
		} else {
			dialog = new JDialog((Dialog) w);
		}
				
		final RadioActions radioActions = new RadioActions(actions);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
				Action action = radioActions.getSelected();
				try {
					action.actionPerformed(
							new ActionEvent(this, 0, null));
					radioActions.result = true;
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(parent, ex, "Error!", 
							JOptionPane.ERROR_MESSAGE);
					radioActions.result = false;
				}
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
				radioActions.result = false;
			}
		});
		
		JPanel closePanel = new JPanel();
		closePanel.setLayout(new BoxLayout(closePanel,
					BoxLayout.LINE_AXIS));
		closePanel.add(Box.createHorizontalGlue());
		closePanel.add(okButton);
		closePanel.add(Box.createHorizontalStrut(5));
		closePanel.add(cancelButton);
		closePanel.setBorder(BorderFactory.
				createEmptyBorder(5, 5, 5, 5));
		radioActions.setBorder(BorderFactory.
				createEmptyBorder(5, 5, 5, 5));
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(radioActions, BorderLayout.CENTER);
		contentPane.add(closePanel, BorderLayout.PAGE_END);
		contentPane.setOpaque(true);
		dialog.setContentPane(contentPane);

		//Show it.
		dialog.setLocationRelativeTo(parent);
		dialog.setModal(true);
		dialog.pack();
		okButton.requestFocusInWindow();
		dialog.setVisible(true);
		return radioActions.result;
	}

}
