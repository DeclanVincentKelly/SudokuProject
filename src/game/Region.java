package game;

//TODO Finish Javadoc

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class Region implements Serializable {

	private static final long serialVersionUID = 8267115454017559922L;

	/**
	 * This the the array of 9 <code>Cell</code> objects that this
	 * <code>Region</code> is responsible for
	 */
	private final Cell[] cells;

	/**
	 * Constructs a <code>Region</code> with the given array of
	 * <code>Cells</code>
	 * 
	 * @param c
	 *            The <code>Cells</code> that this region will be responsible
	 *            for
	 */
	public Region(Cell[] c) {
		cells = c;
	}

	/**
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

	public Cell[] getCells() {
		return cells;
	}

	public void colorAll(Color a) {
		for (Cell c : cells)
			c.setColor(a);
	}
}
