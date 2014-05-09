package gui;

import java.io.File;
import java.io.Serializable;

public interface SudokuSerializable extends Serializable {

	boolean isSaved();

	File getSave();

	void setSave(File f);

	String getName();

	void setName(String s);

	String getSuffix();
}
