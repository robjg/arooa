package org.oddjob.arooa.reflect;

import java.util.LinkedHashMap;
import java.util.Map;

import org.oddjob.arooa.types.ValueFactory;


/**
 * 
 * &lt;beanView&gt;
 *          properties="variety, colour, taste" 
 *          titles="Variety, The Colour", "The Taste"/&gt;
 * 
 * @author rob
 *
 */

public class BeanViewBean implements ValueFactory<BeanView> {

	private String properties;
	
	private String titles;

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public String getTitles() {
		return titles;
	}

	public void setTitles(String titles) {
		this.titles = titles;
	}
	
	
	public BeanView toValue() {
		
	
		if (properties == null) {
			return null;
		}
				
		String[] props = properties.split("\\s*,\\s*");
				
		String[] titles;
		if (this.titles != null) {
			titles = this.titles.split("\\s*,\\s*");
		}
		else {
			titles = new String[0];
		}
				
		return new Extra(props, titles);
	}
	
	class Extra implements BeanView {
		
		private final Map<String, String> map;
		
		public Extra(String[] properties, String[] titles) {
			this.map = new LinkedHashMap<String, String>();
			for (int i = 0; i < properties.length; ++i) {
				String property = properties[i];
				String title;
				if (i >= titles.length) {
					title = property;
				}
				else {
					title = titles[i];
				}
				map.put(property, title);
			}
		}
		
		@Override
		public String titleFor(String property) {
			return map.get(property);
		}
		
		@Override
		public String[] getProperties() {
			return map.keySet().toArray(new String[map.size()]);
		}	
		
		@Override
		public String toString() {
			return "BeanView: " + map.toString();
		}
	}
}
