package org.oddjob.arooa.beanutils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.utils.ClassUtils;

/**
 * Creates new Magic Bean {@link ArooaClass}.
 * 
 * @author rob
 *
 */
public class MagicBeanClassCreator {

	private final String name;
	
	private final Map<String, Class<?>> propertyNamesAndTypes = 
			new LinkedHashMap<String, Class<?>>();
	
	/**
	 * Create an instance of the creator with the given class name.
	 * 
	 * @param name The name.
	 */
	public MagicBeanClassCreator(String name) {
		if (name == null) {
			throw new IllegalStateException(
					"A Magic Bean Definition must have a name.");
		}
		
		this.name = name;
	}
	
	/**
	 * Add a property.
	 * 
	 * @param name The name of the property.
	 * @param type The type of the property.
	 */
	public void addProperty(String name, Class<?> type) {
		if (type.isPrimitive()) {
			type = ClassUtils.wrapperClassForPrimitive(type);
		}
		if (type == null) {
			throw new IllegalArgumentException("No Type.");
		}
		propertyNamesAndTypes.put(name, type);
	}
	
	/**
	 * Create an ArooaClass.
	 * 
	 * @return
	 */
	public ArooaClass create() {
		
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
