/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.view;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.arooa.design.screem.BorderedGroup;
import org.oddjob.arooa.design.screem.FieldGroup;
import org.oddjob.arooa.design.screem.FieldSelection;
import org.oddjob.arooa.design.screem.FileSelection;
import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.design.screem.LabelledComboBox;
import org.oddjob.arooa.design.screem.MultiTypeTable;
import org.oddjob.arooa.design.screem.SelectionList;
import org.oddjob.arooa.design.screem.SingleTypeSelection;
import org.oddjob.arooa.design.screem.TabGroup;
import org.oddjob.arooa.design.screem.TextField;
import org.oddjob.arooa.design.screem.TextInput;

/**
 * 
 */
@SuppressWarnings("rawtypes")
abstract public class SwingItemFactory<T extends FormItem> {

	static class Mapping {

		private final Map<Class<? extends FormItem>, SwingItemFactory<? extends FormItem>> factories = 
			new HashMap<Class<? extends FormItem>, SwingItemFactory<? extends FormItem>>(); 
		
		
		@SuppressWarnings("unchecked")
		<X extends FormItem> SwingItemFactory<X > get(Class<X> forClass) {
			return (SwingItemFactory<X>) factories.get(forClass);
		}
		
		<X extends FormItem> void put(Class<X> forClass, SwingItemFactory<X> factory) {
			factories.put(forClass, factory);
		}
	}
	
	
	private static final Mapping FACTORIES = 
		new Mapping(); 
	
	static {
		FACTORIES.put(TextField.class, 
				new SwingItemFactory<TextField>() {
			public SwingItemView onCreate(TextField viewModel) {
				return new TextFieldView(viewModel);
			}	
		});
	
		FACTORIES.put(BorderedGroup.class, 
				new SwingItemFactory<BorderedGroup>() {
			public SwingItemView onCreate(BorderedGroup viewModel) {
				return new FieldGroupView(viewModel);
			}	
		});
	
		FACTORIES.put(FieldGroup.class, 
				new SwingItemFactory<FieldGroup>() {
			public SwingItemView onCreate(FieldGroup viewModel) {
				return new FieldGroupView(viewModel);
			}	
		});
		
		FACTORIES.put(FieldSelection.class, 
					new SwingItemFactory<FieldSelection>() {
				public SwingItemView onCreate(FieldSelection viewModel) {
					return new FieldSelectionView(viewModel);
				}	
		});
	
		FACTORIES.put(MultiTypeTable.class, 
				new SwingItemFactory<MultiTypeTable>() {
			public SwingItemView onCreate(MultiTypeTable viewModel) {
				return new MultiTypeTableView(viewModel);
			}	
		});
	
		FACTORIES.put(SingleTypeSelection.class, 
				new SwingItemFactory<SingleTypeSelection>() {
			public SwingItemView onCreate(SingleTypeSelection viewModel) {
				return new TypeSelectionView(viewModel);
			}	
		});
		
		FACTORIES.put(SelectionList.class, 
				new SwingItemFactory<SelectionList>() {
			public SwingItemView onCreate(SelectionList viewModel) {
				return new SelectionListView(viewModel);
			}	
		});
		
		FACTORIES.put(TextInput.class, 
				new SwingItemFactory<TextInput>() {
			public SwingItemView onCreate(TextInput viewModel) {
				return new TextInputView(viewModel);
			}	
		});
		

		FACTORIES.put(FileSelection.class, 
				new SwingItemFactory<FileSelection>() {
			public SwingItemView onCreate(FileSelection viewModel) {
				return new FileSelectionView(viewModel);
			}	
		});
		
		FACTORIES.put(TabGroup.class, 
				new SwingItemFactory<TabGroup>() {
			public SwingItemView onCreate(TabGroup viewModel) {
				return new TabGroupView(viewModel);
			}	
		});

		FACTORIES.put(LabelledComboBox.class, 
				new SwingItemFactory<LabelledComboBox>() {
			@SuppressWarnings({ "unchecked" })
			public SwingItemView onCreate(LabelledComboBox viewModel) {
				return new LabelledComboBoxView(viewModel);
			}	
		});
	}
	
	public abstract SwingItemView onCreate(T viewModel);
		

	public static <Y extends FormItem> void register(Class<Y> cl, SwingItemFactory<Y> factory) {
		FACTORIES.put(cl, factory);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <Y extends FormItem> SwingItemView create(Y viewModel) {
		Class<Y> cl = (Class<Y>) viewModel.getClass();
		SwingItemFactory<Y> factory = FACTORIES.get(cl);
		return factory.onCreate(viewModel);
	}
}
