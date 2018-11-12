package nc.ui.wa.datainterface;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class TextFileFilter4TW extends FileFilter {

	private String filterString;
	private String description;

	public TextFileFilter4TW() {
		this.filterString = "*.TXT";
	}

	@Override
	public boolean accept(File f) {
		return f.getName().toUpperCase().equals(filterString.toUpperCase());
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public String getFilterString() {
		return filterString;
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString;
	}
}
