package gui;

import java.io.File;
import java.io.Serializable;

public interface SudokuSerializable extends Serializable {
	public String getSuffix();

	public boolean isSaved();

	public void saveAt(File f);
	
	public File getSave();
	
	public String getName();
}
