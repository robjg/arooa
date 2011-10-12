/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.view;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/**
 *  
 */
public class Standards {

	// file actions 
	public static final Integer NEW_EXPLORER_MNEMONIC_KEY = new Integer(KeyEvent.VK_E);
	public static final KeyStroke NEW_EXPLORER_ACCELERATOR_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK);
	
	public static final Integer NEW_MNEMONIC_KEY = new Integer(KeyEvent.VK_N);
	public static final KeyStroke NEW_ACCELERATOR_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK);
		
	public static final Integer OPEN_MNEMONIC_KEY = new Integer(KeyEvent.VK_O); 
	public static final KeyStroke OPEN_ACCELERATOR_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK);

	public static final Integer CLOSE_MNEMONIC_KEY = new Integer(KeyEvent.VK_C);
	public static final KeyStroke CLOSE_ACCELERATOR_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.CTRL_MASK);

	public static final Integer RELOAD_MNEMONIC_KEY = new Integer(KeyEvent.VK_R);
	public static final KeyStroke RELOAD_ACCELERATOR_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK);

	public static final Integer SAVE_MNEMONIC_KEY = new Integer(KeyEvent.VK_S);
	public static final KeyStroke SAVE_ACCELERATOR_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK);

	public static final Integer SAVEAS_MNEMONIC_KEY = new Integer(KeyEvent.VK_A);
	public static final KeyStroke SAVEAS_ACCELERATOR_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK);
	
	public static final Integer EXIT_MNEMONIC_KEY = new Integer(KeyEvent.VK_X);
	
}
