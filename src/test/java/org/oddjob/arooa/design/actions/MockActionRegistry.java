package org.oddjob.arooa.design.actions;

public class MockActionRegistry implements ActionRegistry {

	@Override
	public void addContextMenuItem(String group, ArooaAction action) {
		throw new RuntimeException("Unexepcted from " + getClass().getName());
	}

	@Override
	public void addContextSubMenu(String group, ActionMenu menu) {
		throw new RuntimeException("Unexepcted from " + getClass().getName());
	}

	@Override
	public void addMainMenu(ActionMenu menu) {
		throw new RuntimeException("Unexepcted from " + getClass().getName());
	}

	@Override
	public void addMenuItem(String menuId, String group, ArooaAction action) {
		throw new RuntimeException("Unexepcted from " + getClass().getName());
	}

	@Override
	public void addSubMenu(String menuId, String group, ActionMenu menu) {
		throw new RuntimeException("Unexepcted from " + getClass().getName());
	}

	
}
