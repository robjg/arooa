package org.oddjob.arooa.beanutils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.expression.DefaultResolver;
import org.apache.commons.beanutils.expression.Resolver;
import org.oddjob.arooa.reflect.ArooaPropertyException;

public class PropertyPath {

	private final List<Fragment> fragments = 
		new ArrayList<Fragment>();
	
	public PropertyPath(String path) {

		Resolver resolver = new DefaultResolver();
		
		String remainingPath = path;
        while (remainingPath != null) {

        	String currentPath = remainingPath; 
        	remainingPath = resolver.remove(currentPath);
        	if (remainingPath == null) {
        		String name = resolver.getProperty(currentPath);
        		if (resolver.isIndexed(currentPath)) {
                    fragments.add(new IndexedFragment(
                    		name, resolver.getIndex(currentPath)));
        		}
        		else if (resolver.isMapped(currentPath)) {
                    fragments.add(new MappedFragment(
                    		name, resolver.getKey(currentPath)));        			
        		}
        		else {
                    fragments.add(new SimpleFragment(name));        			
        		}
        	}
        	else {
            	fragments.add(new IntermediateFragment(
            			resolver.next(currentPath)));
        	}
        }
	}
	
	public void iterate(FragmentVisitor fragmentVisitor) 
	throws ArooaPropertyException {
		for (Fragment element : fragments) {
			element.accept(fragmentVisitor);
		}
	}
	
	abstract class Fragment {
		
		private final String name;
		
		Fragment(String name) {
			this.name = name;
		}
		
		String getName() {
			return name;
		}

		abstract void accept(FragmentVisitor fragmentVisitor)
		throws ArooaPropertyException;
	}
	
	class IntermediateFragment extends Fragment {

		public IntermediateFragment(String name) {
			super(name);
		}
	
		void accept(FragmentVisitor fragmentVisitor) 
		throws ArooaPropertyException {
			fragmentVisitor.onIntermediateProperty(getName());
		}
	}
	
	class SimpleFragment extends Fragment {

		public SimpleFragment(String name) {
			super(name);
		}
	
		void accept(FragmentVisitor fragmentVisitor) 
		throws ArooaPropertyException {
			fragmentVisitor.onSimpleProperty(getName());
		}
	}
	
	class MappedFragment extends Fragment {
		
		private final String key;
		
		MappedFragment(String name, String key) {
			super(name);
			this.key = key;
		}
		
		void accept(FragmentVisitor fragmentVisitor) 
		throws ArooaPropertyException {
			fragmentVisitor.onMappedProperty(getName(), key);
		}
	}
	
	class IndexedFragment extends Fragment {
		
		private int index;
		
		IndexedFragment(String name, int index) {
			super(name);
			this.index = index;
		}
		
		void accept(FragmentVisitor fragmentVisitor) 
		throws ArooaPropertyException {
			fragmentVisitor.onIndexedProperty(getName(), index);
		}
	}
	
	public interface FragmentVisitor {
		
		public void onIntermediateProperty(String name) 
		throws ArooaPropertyException;
		
		public void onMappedProperty(String name, String key)
		throws ArooaPropertyException;
		
		public void onIndexedProperty(String name, int index)
		throws ArooaPropertyException;
		
		public void onSimpleProperty(String nam)
		throws ArooaPropertyException;

	}
}
