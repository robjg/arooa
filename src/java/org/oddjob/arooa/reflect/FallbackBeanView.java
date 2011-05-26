package org.oddjob.arooa.reflect;


public class FallbackBeanView 
implements BeanView {
	
	private final String[] properties;
	
	public FallbackBeanView(PropertyAccessor accessor, 
			Object bean) {
		BeanOverview overview = accessor.getClassName(
				bean).getBeanOverview(accessor);
		
		properties = overview.getProperties();
	}

	public FallbackBeanView(PropertyAccessor accessor, 
			ArooaClass arooaClass) {
		BeanOverview overview = arooaClass.getBeanOverview(accessor);
		
		properties = overview.getProperties();
	}

	@Override
	public String[] getProperties() {
		return properties;
	}
	
	@Override
	public String titleFor(String property) {
		return property;
	}
}
