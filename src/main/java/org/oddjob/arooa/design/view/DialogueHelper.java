package org.oddjob.arooa.design.view;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Helpers for dialogues.
 * 
 * @author rob
 *
 */
public class DialogueHelper {

	/**
	 * Show an Exception dialogue.
	 * 
	 * @param parentComponent
	 * @param exception
	 * @throws HeadlessException
	 */
	public static void showExceptionMessage(Component parentComponent,
			Exception exception)
	throws HeadlessException {

		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));

		JLabel message = new JLabel(exception.getMessage());
		message.setBorder(BorderFactory.createEmptyBorder(3, 0, 10, 0));
		
		JTextArea text = new JTextArea();
		text.setEditable(false);
		text.setFont(UIManager.getFont("Label.font"));
		text.setText(stringWriter.toString());
		text.setCaretPosition(0);

		JScrollPane scroller = new JScrollPane( text );
		scroller.setPreferredSize(new Dimension(400,200));

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panel.add(message, BorderLayout.NORTH);
		panel.add(scroller, BorderLayout.SOUTH);
		
		JOptionPane.showMessageDialog(parentComponent, 
				panel, 
				"Exception!", JOptionPane.ERROR_MESSAGE);
		
	}

	public static boolean showOKCancelDialogue(Component parentComponent,
													 Component dialogueForm,
													 Callable<Boolean> okAction,
													 Consumer<? super AutoCloseable> asyncClose) {

		ValueDialog dialogue = new ValueDialog(dialogueForm, okAction);
		dialogue.showDialog(parentComponent, false, asyncClose);

		return dialogue.isChosen();
	}
}

