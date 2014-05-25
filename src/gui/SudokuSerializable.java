package gui;

import java.io.File;
import java.io.Serializable;

/**
 * The {@code SudokuSerializable} interface functions as cover any class that
 * the application might need to serialize, only exposing the necessary
 * functions with regards to the name of the file and its save location.
 * 
 * @author Declan
 *
 */
public interface SudokuSerializable extends Serializable {

	/**
	 * 
	 * @return whether or not the {@code SudokuSerializable} object has a
	 *         non-null save location, stored as a {@code File}
	 */
	boolean isSaved();

	/**
	 * Gets the save {@code File} of the @{code SudokuSerializable}
	 * 
	 * @return the save location, stored as a {@code File}
	 */
	File getSave();

	/**
	 * Sets the {@code File} save locations of the {@code SudokuSerializable}
	 * 
	 * @param f
	 *            the {@code File} save location to set
	 */
	void setSave(File f);

	/**
	 * Gets the name of the {@code SudokuSerializable}
	 * 
	 * @return the {@code String} name of the {@code SudokuSerializable}
	 */
	String getName();

	/**
	 * Sets the name of the {@code SudokuSerializable} to the specified
	 * {@code String}
	 * 
	 * @param s
	 *            the {@code String} name of the {@code SudokuSerializable} to
	 *            set
	 */
	void setName(String s);

	/**
	 * 
	 * @return the file-type suffix for the {@code SudokuSerializable}
	 */
	String getSuffix();
}
