package org.oddjob.arooa.design;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * @author based on http://www.devx.com/Java/Article/22326
 *
 */
public class ClipboardHelper {

	public void setText(String text) {
		
		// get the system clipboard
		Clipboard systemClipboard =
			Toolkit
				.getDefaultToolkit()
				.getSystemClipboard();
		
		// set the textual content on the clipboard to our 
		// Transferable object
		// we use the 
		Transferable transferableText =
			new StringSelection(text);
		
		systemClipboard.setContents(
			transferableText,
			null);
	}
	
	public String getText() {
		
		// get the system clipboard
		Clipboard systemClipboard =
			Toolkit
			.getDefaultToolkit()
			.getSystemClipboard();
		// get the contents on the clipboard in a 
		// transferable object
		Transferable clipboardContents =
			systemClipboard
			.getContents(
					null);
		// check if clipboard is empty
		if (clipboardContents
				== null) {
			return null;
		} else {
			try {
				// see if DataFlavor of 
				// DataFlavor.stringFlavor is supported
				if (clipboardContents
						.isDataFlavorSupported(
								DataFlavor
								.stringFlavor)) {
					// return text content
					String returnText =
						(
								String) clipboardContents
								.getTransferData(
										DataFlavor
										.stringFlavor);
					return returnText;
				}
				else {
					return null;
				}
			} catch (UnsupportedFlavorException ufe) {
				throw new RuntimeException(ufe);
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
	}
}
