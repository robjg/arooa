package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignNotifier;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.actions.*;
import org.oddjob.arooa.design.view.Looks;
import org.oddjob.arooa.design.view.Standards;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.logging.Appender;
import org.oddjob.arooa.logging.AppenderAdapter;
import org.oddjob.arooa.logging.Layout;
import org.oddjob.arooa.logging.LoggerAdapter;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ElementConfiguration;
import org.oddjob.arooa.types.BeanType;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Rob Gordon
 * @oddjob.description Run a GUI designer for Oddjob.
 */

public class ArooaDesigner
        implements ArooaSessionAware, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ArooaDesigner.class);

    private static final String MODEL_PROPERTY = "designNotifier";
    private static final String FILE_PROPERTY = "file";

    public volatile boolean stop;

    private ConfigurationHandle<ArooaContext> configHandle;

    /**
     * @oddjob.property
     * @oddjob.description The oddjob file.
     * @oddjob.required Yes.
     */
    private transient volatile File file;

    /**
     * The default directory. used when opening files.
     */
    private transient File dir;

    private transient DesignNotifier designerModel;

    /**
     * The frame
     */
    private transient JFrame frame;

    /**
     * The menu bar.
     */
    private DesignerMenuBar menuBar;

    private ArooaSession session;

    private DesignFactory rootFactory;

    private ArooaElement documentElement;

    private ArooaType arooaType;

    private final VetoableChangeSupport vetoSupport = new VetoableChangeSupport(this);

    private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);


    /**
     * Constructor.
     */
    public ArooaDesigner() {

        vetoSupport.addVetoableChangeListener(MODEL_PROPERTY,
                evt -> {

                    // one day check if file has changed and
                    // allow the new designer to be vetoed.

                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(new JPanel());

                    frame.getContentPane().validate();

                    menuBar.setFormMenu(null);

                });

        propertySupport.addPropertyChangeListener(MODEL_PROPERTY,
                evt -> {

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
     * @param configFile The config file name.
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

    public void load(File file) {

        if (file == null) {
            throw new IllegalArgumentException("No file to load");
        }

        frame.toFront();

        XMLConfiguration config = new XMLConfiguration(file);

        DesignParser parser = new DesignParser(session, rootFactory);

        parser.setArooaType(arooaType);
        parser.setExpectedDocumentElement(documentElement);

        try {
            configHandle = parser.parse(config);
        } catch (Exception e) {
            logger.error("Failed creating Design from XML.", e);

            return;
        }

        setDesignerModel(parser);
    }


    public void run() {
        if (session == null) {
            throw new NullPointerException("No Session.");
        }
        if (arooaType == null) {
            throw new NullPointerException("No ArooType.");
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

        Appender errorListener = event -> JOptionPane.showMessageDialog(
                frame,
                event.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);

        frame.setSize(Looks.DESIGNER_WIDTH, Looks.DESIGNER_HEIGHT);
        frame.setVisible(true);

        Layout layout = LoggerAdapter.layoutFor("%m");
        AppenderAdapter appenderAdapter = LoggerAdapter
                .appenderAdapterFor("org.oddjob.arooa.design")
                .addAppender(errorListener, layout);

        if (file != null) {
            try {
                load(file);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            setDesignerModel(null);
        }


        while (!stop) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        appenderAdapter.removeAppender(errorListener);

        frame = null;
    }

    /**
     * Stop the monitor.
     */
    public void stop() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.dispose();
            } else {
                logger.debug("Designer hasn't been started.");
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
     * @return The root element.
     */
    public ArooaElement getDocumentElement() {
        return documentElement;
    }

    /**
     * Set the root element when a designer is restricted to
     * a single root element.
     *
     * @param documentElement the root element.
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
            parser.setExpectedDocumentElement(documentElement);

            try {
                final ConfigurationHandle<ArooaContext> newHandle = parser.parse(
                        new ElementConfiguration(newDocElement));

                configHandle = new ConfigurationHandle<>() {

                    public ArooaContext getDocumentContext() {
                        return newHandle.getDocumentContext();
                    }

                    public void save() throws ArooaParseException {

                        XMLArooaParser parser = new XMLArooaParser(
                                newHandle.getDocumentContext().getPrefixMappings());

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
            } catch (Exception ex) {
                logger.error("Failed: {}", ex.getMessage(), ex);
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
                logger.error("Failed on load file: {}", ex.getMessage(), ex);
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
            propertySupport.addPropertyChangeListener(evt -> {
                if (designerModel == null) {
                    setEnabled(false);
                } else {
                    setEnabled(file != null);
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
                    logger.error("Failed to save: {}", ex.getMessage(), ex);
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
            propertySupport.addPropertyChangeListener(MODEL_PROPERTY,
                    evt -> setEnabled(evt.getNewValue() != null));
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
                logger.error("Failed to save: {}", ex.getMessage(), ex);
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

        @Override
        public void addKeyStrokes(JComponent component) {
            // TODO Auto-generated method stub

        }
    }

    /**
     * A MenuBar that allows menus to be added and removed.
     */
    static class DesignerMenuBar extends JMenuBar {
        private static final long serialVersionUID = 2008121900L;

        JMenu[] existingFormMenus;

        /**
         * Constructor.
         *
         * @param staticMenus Menus that are fixed (e.g. File).
         */
        DesignerMenuBar(JMenu[] staticMenus) {
            if (staticMenus != null) {
                for (JMenu menu : staticMenus) {
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
                for (JMenu existing : existingFormMenus) {
                    remove(existing);
                }
            }
            if (formMenus != null) {
                for (JMenu menu : formMenus) {
                    add(menu);
                }
            }
            existingFormMenus = formMenus;
            validate();
            repaint();
        }
    }

}
