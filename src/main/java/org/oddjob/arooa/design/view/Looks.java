/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.oddjob.arooa.design.DesignComponent;
import org.oddjob.arooa.design.DesignInstance;

/**
 * Provide some components and dimensions with a standard look and feel.
 */

public class Looks{
	
	public static final int DESIGNER_TREE_WIDTH = 200;
	public static final int DETAIL_FORM_WIDTH = 400;
	
	public static final int DETAIL_FORM_BORDER = 4;
	public static final Insets DETIAL_FORM_INSETS = 
			new Insets(Looks.DETAIL_FORM_BORDER, 
					Looks.DETAIL_FORM_BORDER, 
					Looks.DETAIL_FORM_BORDER, 
					Looks.DETAIL_FORM_BORDER);
	
	public static final int GROUP_BORDER = 3;
	
	public static final int DETAIL_USABLE_WIDTH = DETAIL_FORM_WIDTH
			- 2 * GROUP_BORDER- 2 * DETAIL_FORM_BORDER; 
	
	public static final int TEXT_FIELD_SIZE = 30;
	public static final int LABEL_SIZE = 25;
	
	public static final int LIST_ROWS = 8;
	
	public static final int DESIGNER_HEIGHT = 380;
	public static final int DESIGNER_WIDTH 
			= DESIGNER_TREE_WIDTH + DETAIL_FORM_WIDTH;
	
	/**
	 * Create a standard looking border.
	 * 
	 * @param title The border title.
	 * @return The border.
	 */
	public static Border groupBorder(String title) {
		return new CompoundBorder(new TitledBorder(title),
				new EmptyBorder(GROUP_BORDER, GROUP_BORDER, 
						GROUP_BORDER, GROUP_BORDER));
	}
	
	/**
	 * Create the title panel with the type name for the top of
	 * the detail form.
	 */
	public static Component typePanel(String title, final DesignInstance design) {
		JPanel typePanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
			
//		typePanel.setBorder(Looks.groupBorder("Type"));
		JLabel typeLabel = new JLabel(title);
		Font labelFont = typeLabel.getFont(); 
		typeLabel.setFont(labelFont.deriveFont(
				labelFont.getStyle() ^ Font.BOLD, 
				labelFont.getSize() * 1.5F));
		typePanel.add(typeLabel, c);

		if (design instanceof DesignComponent) {
			
			final DesignComponent designComponent = 
				(DesignComponent) design;

			setCommonLabelContraints(c);
			c.gridwidth = 1;
			++c.gridy;
			
			JLabel idLabel = new JLabel(ViewHelper.padLabel("id"),
					SwingConstants.LEADING);
			typePanel.add(idLabel, c);
			
			setCommonTextFieldContraints(c);
			c.gridx = 1;
	
			final JTextField idText = new JTextField(TEXT_FIELD_SIZE);		
			idText.setText(designComponent.getId());
			
			idText.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					designComponent.setId(idText.getText());
				}
				public void removeUpdate(DocumentEvent e) {
					designComponent.setId(idText.getText());
				}
				public void insertUpdate(DocumentEvent e) {
					designComponent.setId(idText.getText());
				}
			});		
			
			typePanel.add(idText, c);
		}
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		++c.gridy;
		
		typePanel.add(new JSeparator(), c);
		
		return typePanel;		
	}
	
	public static void setCommonLabelContraints(GridBagConstraints c) {
		c.weightx = 0.3;
		c.weighty = 0.0;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(3, 3, 3, 20);		 
	}
	
	public static void setCommonTextFieldContraints(GridBagConstraints c) {
		c.weightx = 1.0;
		c.weighty = 0.0;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(3, 0, 3, 0);
	}
}
