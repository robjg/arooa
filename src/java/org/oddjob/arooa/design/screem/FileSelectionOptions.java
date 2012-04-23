package org.oddjob.arooa.design.screem;

import java.io.File;
import java.util.Arrays;

import org.oddjob.arooa.design.view.FileSelectionWidget;

/**
 * Selection options for {@link FileSelectionWidget}.
 * 
 * @author rob
 *
 */
public class FileSelectionOptions {
	
	public enum SelectionMode {
		FILE,
		DIRECTORY
	}
	
	private File currentDirectory;
	
	private String fileFilterDescription;
	
	private String[] fileFilterExtensions;

	private SelectionMode selectionMode;
	
	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	public File getCurrentDirectory() {
		return currentDirectory;
	}

	public void setCurrentDirectory(File currentDirectory) {
		this.currentDirectory = currentDirectory;
	}

	public String getFileFilterDescription() {
		if (fileFilterDescription == null) {
			if (fileFilterExtensions == null) {
				return null;
			}
			else {
				return Arrays.toString(fileFilterExtensions);
			}
		}
		else {
			return fileFilterDescription;
		}
	}

	public void setFileFilterDescription(String fileFilterDescription) {
		this.fileFilterDescription = fileFilterDescription;
	}

	public String[] getFileFilterExtensions() {
		return fileFilterExtensions;
	}

	public void setFileFilterExtensions(String[] fileFilterExtensions) {
		this.fileFilterExtensions = fileFilterExtensions;
	}
}
