package game;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A region construct that contains information about an array of {@code Cells}.
 * The {@code Region} object is responsible for determining duplicates within
 * itself, checking the completeness of the {@code Region}, and setting the
 * {@code Color} of every {@code Cell} within itself. The {@code Region}
 * represents a region of a Sudoku board, such as a row, column, or 3x3 box of
 * {@code Cells}.
 * 
 * @author Declan
 *
 */
public class Region implements Serializable {

	private static final long serialVersionUID = 8267115454017559922L;

	/**
	 * The array of {@code Cell} objects that this {@code Region} is responsible
	 * for.
	 *
	 * @serial
	 * @see #getCells()
	 */
	private final Cell[] cells;

	/**
	 * Constructs and initializes a {@code Region} object with the given array
	 * of {@code Cells}.
	 * 
	 * @param c
	 *            The array of {@code Cells} that this region will be
	 *            responsible for.
	 */
	public Region(Cell[] c) {
		cells = c;
	}

	/**
	 * Checks the array of {@code Cells} for one instance of the numbers 1-9.
	 * 
	 * @return a boolean indicating whether or not this region is complete
	 */
	public boolean isComplete() {
		for (Cell c : cells) {
			if (c.getContent() == 0)
				return false;

			for (Cell x : cells)
				if (c.getContent() == x.getContent() && c != x)
					return false;
		}

		return true;
	}

	/**
	 * Checks the array of {@code Cells} for any {@code Cells} with duplicate
	 * content.
	 * 
	 * @return an array of {@code Cells} that contains the {@code Cells} within
	 *         the {@code Region} that have the same {@code int} content.
	 */
	public Cell[] getDuplicates() {
		ArrayList<Cell> temp = new ArrayList<Cell>();

		for (Cell c : cells)
			for (Cell x : cells)
				if (c.getContent() == x.getContent() && c != x && c.getContent() != 0) {
					temp.add(c);
					temp.add(x);
				}

		return temp.toArray(new Cell[temp.size()]);
	}

	/**
	 * 
	 * @return the array of {@code Cells} that this {@code Region} is
	 *         responsible for.
	 */
	public Cell[] getCells() {
		return cells;
	}

	/**
	 * Sets every {@code Cell} in this {@code Region} to the same {@code Color}.
	 * 
	 * @param a
	 *            the {@code Color} to set each {@code Cell} in this
	 *            {@code Region} to.
	 */
	public void colorAll(Color a) {
		for (Cell c : cells)
			c.setColor(a);
	}

	@Override
	public String toString() {
		return Arrays.toString(cells);
	}
}
