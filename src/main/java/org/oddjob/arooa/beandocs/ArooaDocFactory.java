package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.ArooaType;

/**
 * Something that can create {@link ArooaDoc}.
 */
public interface ArooaDocFactory {

	/**
	 * Create a set of {@link ArooaDoc} for the given {@link ArooaType}.
	 * 
	 * @param type The type. Component/Value
	 * @return The doc.
	 */
	ArooaDoc createBeanDocs(ArooaType type);
	
}
