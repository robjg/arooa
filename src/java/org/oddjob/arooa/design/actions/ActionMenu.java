package org.oddjob.arooa.design.actions;

import java.awt.event.KeyEvent;

/**
 * Encapsulate Menu Attributes.
 * 
 * @author rob
 *
 */
public class ActionMenu {

	private final String id;
	
	private final String name;
	
	private final int mnumonic;
	
	public ActionMenu(String id, String name, int mnumonic) {
		this.id = id;
		this.name = name;
		this.mnumonic = mnumonic;
	}
	
	public ActionMenu(String id, String name) {
		this(id, name, KeyEvent.VK_UNDEFINED);
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getMnumonic() {
		return mnumonic;
	}
	
}
