package org.oddjob.arooa.beanutils;

import java.util.Map;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * Creates new Magic Bean {@link ArooaClass}.
 * 
 * @author rob
 *
 */
public class MagicBeanClassCreator {

	/**
	 * Create an ArooaClass.
	 * 
	 * @param name
	 * @param propertyNamesAndTypes
	 * @return
	 */
	public ArooaClass create(String name,
			Map<String, Class<?>> propertyNamesAndTypes) {
		
		if (name == null) {
			throw new IllegalStateException(
					"A Magic Bean Definition must have a name.");
		}
		
		DynaProperty[] dynas = new DynaProperty[propertyNamesAndTypes.size()];
		
		int i = 0;
		for (String propertyName : propertyNamesAndTypes.keySet()) {
			
			dynas[i++] = new DynaProperty(propertyName, 
					propertyNamesAndTypes.get(propertyName));
		}
		
		DynaClass dynaClass = new MagicBeanClass(dynas, name);
		
		return new DynaArooaClass(dynaClass, MagicBean.class);

	}
	
	
}
