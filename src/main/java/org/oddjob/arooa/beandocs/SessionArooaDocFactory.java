package org.oddjob.arooa.beandocs;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * An {@link ArooaDocFactory} that creates a set of {@link ArooaDoc} from
 * an {@link ArooaSession}.
 * 
 * @author rob
 *
 */
public class SessionArooaDocFactory 
implements ArooaDocFactory {

	final private ArooaSession session;
	
	final private ArooaDescriptor descriptor;
	
	public SessionArooaDocFactory(ArooaSession session) {
		this(session, session.getArooaDescriptor());
	}	
	
	public SessionArooaDocFactory(ArooaSession session, 
			ArooaDescriptor descriptor) {
		this.session = session;
		this.descriptor = descriptor;
	}	
	
	@Override
	public WriteableArooaDoc createBeanDocs(ArooaType arooaType) {
		
    	ElementMappings mappings = descriptor.getElementMappings();
		
    	MappingsContents content = mappings.getBeanDoc(arooaType);
    	
    	ArooaElement[] elements = content.allElements();
    	
    	WriteableArooaDoc arooaDoc = new WriteableArooaDoc();
    	    	
    	for (int i = 0; i < elements.length; ++i) {
    	
    		arooaDoc.add(createBeanDoc(elements[i],
    				content.documentClass(elements[i])
    				));
    	}
    	
		return arooaDoc;
	}
	
	public WriteableBeanDoc createBeanDoc(ArooaElement element, 
			ArooaClass arooaClass) {
		
		WriteableBeanDoc beanDoc = new WriteableBeanDoc();
		
		String prefix = descriptor.getPrefixFor(element.getUri());

		beanDoc.setPrefix(prefix);
		beanDoc.setTag(element.getTag());
		
		beanDoc.setClassName(arooaClass.forClass().getName());
		
		Iterable<WriteablePropertyDoc> propertyDocs = 
			createPropertyDocs(arooaClass);
		
		for (WriteablePropertyDoc propertyDocBean : propertyDocs) {
			
			beanDoc.addPropertyDoc(propertyDocBean);			
		}
		
		return beanDoc;
	}
	
	public Iterable<WriteablePropertyDoc> createPropertyDocs(ArooaClass forClass) {

		PropertyAccessor accessor = session.getTools().getPropertyAccessor();
		
		ArooaBeanDescriptor beanDescriptor = 
				descriptor.getBeanDescriptor(forClass, accessor);
		
		if (beanDescriptor == null) {
			throw new NullPointerException("No BeanDescriptor for " +
					forClass);
		}
		
		BeanOverview overview = forClass.getBeanOverview(accessor);

		String[] properties = overview.getProperties();
		
		List<WriteablePropertyDoc> propertyDocs = new ArrayList<WriteablePropertyDoc>();
		
		for (String property: properties) {
		
			if ("class".equals(property)) {
				continue;
			}
			
			WriteablePropertyDoc propertyDoc = new WriteablePropertyDoc();
			
			propertyDoc.setPropertyName(property);
			
			propertyDoc.setConfiguredHow(beanDescriptor.getConfiguredHow(
					property));
			
			PropertyDoc.Access access = PropertyDoc.Access.READ_WRITE;
			if (!overview.hasWriteableProperty(property)) {
				access = PropertyDoc.Access.READ_ONLY;
			}
			else if (!overview.hasReadableProperty(property)) {
				access = PropertyDoc.Access.WRITE_ONLY;				
			}
			
			propertyDoc.setAccess(access);
			
			PropertyDoc.Multiplicity multiplicity = 
				PropertyDoc.Multiplicity.SIMPLE;
			if (overview.isIndexed(property)) {
				multiplicity = PropertyDoc.Multiplicity.INDEXED;
			}
			else if (overview.isMapped(property)) {
				multiplicity = PropertyDoc.Multiplicity.MAPPED;
			}
			
			propertyDoc.setMultiplicity(multiplicity);
			
			propertyDoc.setAuto(beanDescriptor.isAuto(property));
			
			propertyDocs.add(propertyDoc);
		}
		
		return propertyDocs;
	}
}
