package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.*;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.ArrayList;
import java.util.List;

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

		for (ArooaElement element : elements) {

			arooaDoc.add(createBeanDoc(element, content.documentClass(element)));
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
		
		List<WriteablePropertyDoc> propertyDocs = new ArrayList<>();
		
		for (String property: properties) {
		
			if ("class".equals(property)) {
				continue;
			}
			
			WriteablePropertyDoc propertyDoc = new WriteablePropertyDoc();
			
			propertyDoc.setPropertyName(property);
			
			PropertyDoc.Access access = PropertyDoc.Access.READ_WRITE;

			if (overview.hasWriteableProperty(property)) {
				propertyDoc.setConfiguredHow(beanDescriptor.getConfiguredHow(
						property));
				if (!overview.hasReadableProperty(property)) {
					access = PropertyDoc.Access.WRITE_ONLY;
				}
			}
			else {
				access = PropertyDoc.Access.READ_ONLY;
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
