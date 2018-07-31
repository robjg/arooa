package org.oddjob.arooa.life;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Check for annotations for {@link ArooaLifeAware}.
 * 
 * @author rob
 *
 */
public class LifecycleInterfaceFactory {

	public ArooaLifeAware lifeCycleFor(final Object component,
						ArooaSession session) {
					
		PropertyAccessor accessor = 
				session.getTools().getPropertyAccessor();
		
		ArooaBeanDescriptor beanDescriptor = 
				session.getArooaDescriptor().getBeanDescriptor(
						new SimpleArooaClass(component.getClass()), 
						accessor);
		
		ArooaAnnotations annotations = 
				beanDescriptor.getAnnotations();
		
		final Method initialisedMethod = 
				annotations.methodFor(Initialised.class.getName());
		final Method configuredMethod = 
				annotations.methodFor(Configured.class.getName());
		final Method destroyMethod = 
				annotations.methodFor(Destroy.class.getName());
		
		if (initialisedMethod == null && configuredMethod == null &&
				destroyMethod == null) {
			return null;
		}
		
		return new ArooaLifeAware() {
			
			@Override
			public void initialised() {
				invoke(component, initialisedMethod);
			}
			
			@Override
			public void configured() {
				invoke(component, configuredMethod);
			}
			
			@Override
			public void destroy() {
				invoke(component, destroyMethod);
			}			
		};
	}
	
	private void invoke(Object component, Method m) {
		if (m == null) {
			return;
		}
		try {
			m.invoke(component);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
