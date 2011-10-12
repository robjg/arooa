package org.oddjob.arooa.design.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
		
		JButton xml = new JButton("XML");
		xml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XMLArooaParser parser = new XMLArooaParser();
				try {
					parser.parse(design.getArooaContext().getConfigurationNode());
				} catch (ArooaParseException ex) {
					throw new RuntimeException(ex);
				}
				System.out.println(parser.getXml());
			}
		});
		
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(xml, BorderLayout.SOUTH);
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);

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
