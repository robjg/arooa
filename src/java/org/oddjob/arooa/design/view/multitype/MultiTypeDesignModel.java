package org.oddjob.arooa.design.view.multitype;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.DesignElementProperty;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignListener;
import org.oddjob.arooa.design.DesignStructureEvent;
import org.oddjob.arooa.design.InstanceSupport;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.MultiTypeTable;
import org.oddjob.arooa.design.view.DesignViewException;
import org.oddjob.arooa.design.view.SwingFormFactory;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.parsing.QTag;

/**
 * The model for a MultiTypeTable created from a property.
 * 
 * @author rob
 *
 */
public class MultiTypeDesignModel extends AbstractMultiTypeModel {

	public static final QTag NULL_TAG = new QTag("");

	private final MultiTypeTable viewModel;

	private final InstanceSupport support;
	
	private final List<InstanceRow> instances = 
			new ArrayList<InstanceRow>();	

	private final QTag[] options;
	
	/**
	 * Constructor.
	 * 
	 * @param viewModel The model.
	 */
	public MultiTypeDesignModel(MultiTypeTable viewModel) {
		this.viewModel = viewModel;
		
		DesignElementProperty designProperty =
				viewModel.getDesignProperty();
		
		designProperty.addDesignListener(new DesignListener() {
			@Override
			public void childAdded(DesignStructureEvent event) {
				DesignInstance child = event.getChild();
				InstanceRow row = new InstanceRow(child);
				int index = event.getIndex();
				instances.add(index, row);
				fireRowInserted(index);
			}
			@Override
			public void childRemoved(DesignStructureEvent event) {
				int index = event.getIndex();
				instances.remove(index);
				fireRowRemoved(index);
			}
		});
		
		support = new InstanceSupport(
				designProperty);
		
		options = support.getTags();
	}

	@Override
	public Object[] getTypeOptions() {
		return options;
	}

	@Override
	public Object getDeleteOption() {
		return NULL_TAG;
	}
	
	@Override
	public int getRowCount() {
		return instances.size();
	}

	@Override
	public void createRow(Object creator, int rowIndex) {
		try {
			support.insertTag(rowIndex, (QTag) creator);
		} catch (ArooaParseException e) {
			throw new DesignViewException(e);
		}
	}

	@Override
	public MultiTypeRow getRow(int index) {
		return instances.get(index);
	}

	@Override
	public void removeRow(int rowIndex) {
		support.removeInstance(instances.get(rowIndex).instance);
	}

	@Override
	public void swapRow(int from, int direction) {
		DesignInstance instance = instances.get(from).instance;
		String key = null;
		if (viewModel.isKeyed()) {
			key = viewModel.getChildName(from);
		}
		ArooaContext instanceContext = instance.getArooaContext();
		ArooaContext parentContext = instanceContext.getParent();
		
		CutAndPasteSupport.cut(parentContext, instanceContext);
		
		fireRowRemoved(from);
		
		int to = from+direction;
		
		try {
			CutAndPasteSupport.paste(parentContext, to, 
					instanceContext.getConfigurationNode());
		} catch (ArooaParseException e) {
			throw new DesignViewException(e);
		}
		if (key != null) {
			viewModel.setChildName(to, key);
		}
		fireRowInserted(to);
	}
	
	/**
	 * The Row.
	 */
	class InstanceRow implements MultiTypeRow {

		final DesignInstance instance;
		
		private final Component component;

		private final QTag type;

		public InstanceRow(DesignInstance instance) {
			this.instance = instance;
			
			this.type = InstanceSupport.tagFor(instance);
			
			Form designDefintion = instance.detail();
			this.component = SwingFormFactory.create(designDefintion).cell();
		}
		
		@Override
		public Object getType() {
			return type;
		}

		@Override
		public void setType(Object type) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getName() {
			return viewModel.getChildName(instances.indexOf(this));
		}

		@Override
		public void setName(String name) {
			viewModel.setChildName(instances.indexOf(this), name);
		}

		@Override
		public EditableValue getValue() {
			return new EditableValue() {
				@Override
				public Component getEditor() {
					return component;
				}
				
				@Override
				public void commit() {
				}
				
				@Override
				public void abort() {
				}
			};
		}		
	}
}
