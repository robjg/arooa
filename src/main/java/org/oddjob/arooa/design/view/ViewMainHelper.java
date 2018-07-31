package org.oddjob.arooa.design.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.xml.XMLArooaParser;

public class ViewMainHelper implements Runnable {

	private final DesignInstance design;
	
	public ViewMainHelper(DesignInstance design) {
		
		this.design = design;	
	}
	
	public void run() {
		
		Form form = design.detail();
		
		Component view = SwingFormFactory.create(form).dialog();
		
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(view);
//		scroll.setPreferredSize(new Dimension(500, 400));
		
		JPanel panel = new JPanel(new BorderLayout());

		ActionListener xmlAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XMLArooaParser parser = new XMLArooaParser();
				try {
					parser.parse(
							design.getArooaContext().getConfigurationNode());
				} catch (ArooaParseException ex) {
					throw new RuntimeException(ex);
				}
				System.out.println(parser.getXml());
			}
		};
		
		JButton xml = new JButton("XML");
		xml.addActionListener(xmlAction);
				
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(xml, BorderLayout.SOUTH);
		
		final JFrame frame = new JFrame();
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);

		ActionListener cancelAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}	
		};
		
		KeyStroke escapeStroke = KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0);
		KeyStroke enterStroke = KeyStroke.getKeyStroke(
				KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK);
		
		frame.getRootPane().registerKeyboardAction(xmlAction, 
				enterStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		frame.getRootPane().registerKeyboardAction(cancelAction, 
				escapeStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				synchronized(ViewMainHelper.this) {
					ViewMainHelper.this.notifyAll();
				}
			}
		});
		
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				
			}
		}
	}
	
}