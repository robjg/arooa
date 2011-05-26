package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.ArooaType;

public interface ArooaDocFactory {

	/**
	 * Create a set of {@link ArooaDoc} for the given {@link ArooaType}.
	 * 
	 * @param type
	 * @return
	 */
	public ArooaDoc createBeanDocs(ArooaType type);	
	
}
