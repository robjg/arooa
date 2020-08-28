package org.oddjob.arooa.design.screem;

/**
 * Represent a form that has no properties. Having a different type allows the cell swing view in
 * {@link org.oddjob.arooa.design.view.SwingFormView} to be an un clickable label and so a dialog is never
 * required.
 * <p/>
 * It should be impossible for a component to provide a {@code NullForm} because it has an Id.
 */
public class NullForm implements Form {

	public String getTitle() {
		throw new IllegalStateException(
				"There is no title because this should never be used as a dialog!");
	}

}
