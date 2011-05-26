package org.oddjob.arooa.design.designer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignNotifier;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.actions.AbstractArooaAction;
import org.oddjob.arooa.design.actions.ActionContributor;
import org.oddjob.arooa.design.actions.ActionMenu;
import org.oddjob.arooa.design.actions.ActionRegistry;
import org.oddjob.arooa.design.actions.ArooaAction;
import org.oddjob.arooa.design.actions.ConfigurableMenus;
import org.oddjob.arooa.design.view.Looks;
import org.oddjob.arooa.design.view.Standards;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ElementConfiguration;
import org.oddjob.arooa.types.BeanType;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

/**
 * @oddjob.description Run a GUI designer for Oddjob.
 * 
 * @author Rob Gordon
 */

public class ArooaDesigner 
implements ArooaSessionAware, Runnable {

	private static final Logger logger = Logger.getLogger(ArooaDesigner.class);

	private static final String MODEL_PROPERTY = "designNotifier"; 
	private static final String FILE_PROPERTY = "file"; 
	
	public volatile boolean stop;

	private ConfigurationHandle configHandle;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The oddjob file.
	 * @oddjob.required Yes.
	 */
	private transient volatile File file;

	/** The default directory. used when opening files. */
	private transient File dir;

	private transient DesignNotifier designerModel;

	/** The frame */
	private transient JFrame frame;

	/** The menu bar. */
	private DesignerMenuBar menuBar;

	private ArooaSession session;

	private DesignFactory rootFactory;
	
	private ArooaElement documentElement;

	private ArooaType arooaType;
	
	private final VetoableChangeSupport vetoSupport = new VetoableChangeSupport(this);
	
	private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
	
	
	/**
	 * Constructor.
	 *
	 */
	public ArooaDesigner() {

		vetoSupport.addVetoableChangeListener(MODEL_PROPERTY, 
				new VetoableChangeListener() {
			public void vetoableChange(PropertyChangeEvent evt)
					throws PropertyVetoException {
				
				// one day check if file has changed and
				// allow the new designer to be vetoed.
								
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new JPanel());

				frame.getContentPane().validate();
				
				menuBar.setFormMenu(null);
				
			}
		});
		
		propertySupport.addPropertyChangeListener(MODEL_PROPERTY,
				new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {

				DesignNotifier notifier = (DesignNotifier) evt.getNewValue();
				
				if (notifier == null) {
					return;
				}
				
				ArooaDesignerForm form = new ArooaDesignerForm(notifier);
				
				ArooaDesignerFormView view = new ArooaDesignerFormView(form, true);
				Component treeComp = view.dialog();
			    
				frame.getContentPane().removeAll();
				frame.getContentPane().add(treeComp);
				frame.pack();
				frame.validate();		
				
				menuBar.setFormMenu(view.getMenus().getJMenuBar());
			}
		});
	}

	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	private void setDesignerModel(DesignNotifier designerModel) {
		PropertyChangeEvent evt = new PropertyChangeEvent(this, 
				MODEL_PROPERTY, this.designerModel, designerModel);
		
		try {
			vetoSupport.fireVetoableChange(evt);
		} catch (PropertyVetoException e) {
			return;
		}
		
		this.designerModel = designerModel;
		
		propertySupport.firePropertyChange(evt);
	}	

	/**
	 * Set the config file name.
	 * 
	 * @param configFile
	 *            The config file name.
	 */
	public void setFile(File configFile) {
		PropertyChangeEvent evt = new PropertyChangeEvent(
				this, FILE_PROPERTY, file, configFile);
		
		this.file = configFile;
		if (file != null) {
			this.dir = configFile.getAbsoluteFile().getParentFile();
		}
		
		title();
		
		propertySupport.firePropertyChange(evt);
	}
	
	/**
	 * Change the title.
	 */
	void title() {
		if (frame != null) {
			frame.setTitle("OddJob Designer" + (file == null 
					? "" : " - " + file.getName()));
		}
		
	}
	
	/**
	 * Get the config file name.
	 * 
	 * @return The config file name.
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Set the default directory.
	 * 
	 * @param dir A directory.
	 */
	public void setDir(File dir) {
		this.dir = dir;
	}
	
	public File getDir() {
		return dir;
	}
	
	public void load(File file) throws FileNotFoundException {
		
		if (file == null) {
			throw new IllegalArgumentException("No file to load");
		}
		
		frame.toFront();

		XMLConfiguration config = new XMLConfiguration(file);

		DesignParser parser = new DesignParser(session, rootFactory);
		
		parser.setArooaType(arooaType);
		parser.setExpectedDoucmentElement(documentElement);
						
		try {
			configHandle = parser.parse(config);
		}
		catch (Exception e) {
			logger.error("Failed creating Design from XML.", e);

			return;
		}

		setDesignerModel(parser);			
	}

	
	public void run() {
		if (session == null) {
			throw new NullPointerException("No Session.");
		}
		
		stop = false;
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		frame = new JFrame();		
		frame.setLocation(300, 300);
				
		frame.addWindowListener(new WindowAdapter() {

			public void windowClosed(WindowEvent e) {
				synchronized (ArooaDesigner.this) {
					stop = true;
					ArooaDesigner.this.notifyAll();
				}
				logger.debug("Monitor closed.");
			}
		});

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		FileActionContributor fileActions = new FileActionContributor();
		
		ConfigurableMenus staticMenus = new ConfigurableMenus();
		fileActions.contributeTo(staticMenus);
				
		menuBar = new DesignerMenuBar(staticMenus.getJMenuBar());
		
		frame.setJMenuBar(menuBar);

		Appender errorListener = 
			new AppenderSkeleton () {
			
			@Override
			protected void append(LoggingEvent event) {

				JOptionPane.showMessageDialog(
						frame, 
						event.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);

			}
			
			public void close() {
			}


			public boolean requiresLayout() {
				return false;
			}
		};

		frame.setSize(Looks.DESIGNER_WIDTH, Looks.DESIGNER_HEIGHT);
		frame.setVisible(true);
		
		Logger.getLogger("org.oddjob.arooa.design").addAppender(errorListener);

		if (file != null) {
			try {
				load(file);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		else {
			setDesignerModel(null);
		}

				
		while (!stop) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		Logger.getLogger("org.oddjob.arooa.design").removeAppender(errorListener);
		
		frame = null;
	}

	/**
	 * Stop the monitor.
	 */
	public void stop() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (frame != null) {
					frame.dispose();
				} else {
					logger.debug("Designer hasn't been started.");
				}
			}
		});
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public DesignFactory getRootFactory() {
		return rootFactory;
	}

	public void setRootFactory(DesignFactory designFactory) {
		this.rootFactory = designFactory;
	}

	
	/**
	 * Get the root element.
	 * 
	 * @return
	 */
	public ArooaElement getDocumentElement() {
		return documentElement;
	}

	/**
	 * Set the root element when a designer is restricted to
	 * a single root element.
	 * 
	 * @param documentElement
	 */
	public void setDocumentElement(ArooaElement documentElement) {
		this.documentElement = documentElement;
	}


	public ArooaType getArooaType() {
		return arooaType;
	}

	public void setArooaType(ArooaType component) {
		this.arooaType = component;
	}

	class NewAction extends AbstractArooaAction {
		private static final long serialVersionUID = 2008111300;
	
		NewAction() {
			putValue(Action.NAME, "New");
			putValue(Action.MNEMONIC_KEY, Standards.NEW_MNEMONIC_KEY); 
			putValue(Action.ACCELERATOR_KEY, Standards.NEW_ACCELERATOR_KEY);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {

			DesignParser parser = new DesignParser(session, rootFactory);
			
			ArooaElement newDocElement = documentElement;
			if (newDocElement == null) {
				newDocElement = BeanType.ELEMENT; 
			}

			parser.setArooaType(arooaType);
			parser.setExpectedDoucmentElement(documentElement);
			
			try {
				final ConfigurationHandle newHandle = parser.parse(
						new ElementConfiguration(newDocElement));
				
				configHandle = new ConfigurationHandle() {
					
					public ArooaContext getDocumentContext() {
						return newHandle.getDocumentContext();
					}
					
					public void save() throws ArooaParseException {
						
						XMLArooaParser parser = new XMLArooaParser();
						
						parser.parse(newHandle.getDocumentContext().getConfigurationNode());

						try {
							FileWriter writer = new FileWriter(file);
							writer.write(parser.getXml());
							writer.close();
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}
				};
			}
			catch (Exception ex) {
				logger.error("Failed: " + ex.getMessage(), ex);
				return;
			}

			setDesignerModel(parser);
			setFile(null);			
		}
	}

	class OpenAction extends AbstractArooaAction {
		private static final long serialVersionUID = 2008111300;
	
		OpenAction() {
			putValue(Action.NAME, "Open");
			putValue(Action.MNEMONIC_KEY, Standards.OPEN_MNEMONIC_KEY); 
			putValue(Action.ACCELERATOR_KEY, Standards.OPEN_ACCELERATOR_KEY);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			if (dir != null) {
				chooser.setCurrentDirectory(dir);
			}

			int option = chooser.showOpenDialog(frame);
			if (option != JFileChooser.APPROVE_OPTION) {
				return;
			}
			
			File file = chooser.getSelectedFile(); 
			try {
				load(file);
			} catch (Exception ex) {
				logger.error("Failed on load file: " + ex.getMessage(), ex);
			}
			
			setFile(file);
		}
	}

	class CloseAction extends AbstractArooaAction {
		private static final long serialVersionUID = 2008111300;
	
		CloseAction() {
			putValue(Action.NAME, "Close");
			putValue(Action.MNEMONIC_KEY, Standards.CLOSE_MNEMONIC_KEY); 
			putValue(Action.ACCELERATOR_KEY, Standards.CLOSE_ACCELERATOR_KEY);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			setDesignerModel(null);

			// should probably check not vetoed.
			
			setFile(null);
		}
	}
	
	class SaveAction extends AbstractArooaAction {
		private static final long serialVersionUID = 2008111300;
	
		SaveAction() {
			putValue(Action.NAME, "Save");
			putValue(Action.MNEMONIC_KEY, Standards.SAVE_MNEMONIC_KEY); 
			putValue(Action.ACCELERATOR_KEY, Standards.SAVE_ACCELERATOR_KEY);
			
			setEnabled(false);
			propertySupport.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (designerModel == null) {
						setEnabled(false);
					}
					else {
						if (file == null) {
							setEnabled(false);
						}
						else {
							setEnabled(true);							
						}
					}
				}
			});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (file == null) {
				new SaveAsAction().actionPerformed(e);
			} else {
				try {
					configHandle.save();
				} catch (Exception ex) {
					logger.error("Failed to save: " + ex.getMessage(), ex); 
				}
			}
		}
	}

	class SaveAsAction extends AbstractArooaAction {
		private static final long serialVersionUID = 2008111300;
		
		SaveAsAction() {
			putValue(Action.NAME, "Save As...");
			putValue(Action.MNEMONIC_KEY, Standards.SAVEAS_MNEMONIC_KEY); 
			putValue(Action.ACCELERATOR_KEY, Standards.SAVEAS_ACCELERATOR_KEY);
			
			setEnabled(false);
			propertySupport.addPropertyChangeListener(MODEL_PROPERTY, new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getNewValue() == null) {
						setEnabled(false);
					}
					else {
						setEnabled(true);							
					}
				}
			});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			if (dir != null) {
				chooser.setCurrentDirectory(dir);
			}
			int option = chooser.showSaveDialog(frame);
			if (option != JFileChooser.APPROVE_OPTION) {
				return;
			}

			setFile(chooser.getSelectedFile());
			
			try {
				configHandle.save();
			} catch (Exception ex) {
				logger.error("Failed to save: " + ex.getMessage(), ex); 
			}
		}
	}
	
	class ExitAction extends AbstractArooaAction {
		private static final long serialVersionUID = 2008111300;
	
		ExitAction() {
			putValue(Action.NAME, "Exit");
			putValue(Action.MNEMONIC_KEY, Standards.EXIT_MNEMONIC_KEY); 
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			setDesignerModel(null);
			
			// should check veto.
			
			stop();
		}
	}	
	
	class FileActionContributor implements ActionContributor {
		
		private static final String FILE_ID = "FILE";
		
		private static final String FILE_GROUP = "file";
		
		private static final String EXIT_GROUP = "exit";
		
		public void contributeTo(ActionRegistry actionRegistry) {

			actionRegistry.addMainMenu(
					new ActionMenu(FILE_ID, "File", KeyEvent.VK_F));
			
			ArooaAction newAction = new NewAction();

			ArooaAction openAction = new OpenAction();

			ArooaAction closeAction = new CloseAction();
			
			ArooaAction saveAction = new SaveAction();

			ArooaAction saveAsAction = new SaveAsAction();

			ArooaAction exitAction = new ExitAction();
			
			actionRegistry.addMenuItem(
					FILE_ID, FILE_GROUP, newAction);
			
			actionRegistry.addMenuItem(
					FILE_ID, FILE_GROUP, openAction);
			
			actionRegistry.addMenuItem(
					FILE_ID, FILE_GROUP, saveAction);
			
			actionRegistry.addMenuItem(
					FILE_ID, FILE_GROUP, saveAsAction);
			
			actionRegistry.addMenuItem(
					FILE_ID, FILE_GROUP, closeAction);
			
			actionRegistry.addMenuItem(
					FILE_ID, EXIT_GROUP, exitAction);
		}
	}

	/**
	 * A MenuBar that allows menus to be added and removed.
	 * 
	 */
	class DesignerMenuBar extends JMenuBar {
		private static final long serialVersionUID = 2008121900L;
		
		JMenu[] existingFormMenus;
		
		/**
		 * Constructor.
		 *  
		 * @param staticMenus Menus that are fixed (e.g. File).
		 */
		DesignerMenuBar(JMenu[] staticMenus) {
			if (staticMenus != null) {
				for (JMenu menu: staticMenus) {
					add(menu);
				}
			}
		}		
		
		/**
		 * Set new menu bar menus or remove them.
		 * 
		 * @param formMenus Null to removed.
		 */
		public void setFormMenu(JMenu[] formMenus) {
			if (existingFormMenus != null) {
				for (JMenu existing: existingFormMenus) {
					remove(existing);
				}
			}
			if (formMenus != null) {
				for (JMenu menu: formMenus) {
					add(menu);
				}
			}	
			existingFormMenus = formMenus;
			validate();
			repaint();
		}		
	}
	
}
