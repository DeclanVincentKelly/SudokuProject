package game;

import gui.SudokuSerializable;

import java.awt.Color;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * A game construct that contains all of the exacts board {@code Cells} and
 * {@code Regions}, as well as other information such as the {@code Color}
 * scheme that the {@code SudokuBoard} will draw, the save location of the
 * {@code SudokuGame} stored as a {@code File}, and the {@code String} name of
 * the particular {@code SudokuGame} which is used for graphical presentation.
 * The {@code SudokuGame} is also responsible for reporting state information
 * about the game, and updating the {@code Colors} of each individual
 * {@code Cell}.
 * 
 * @author Declan
 *
 */
public class SudokuGame implements SudokuSerializable {

	private static final long serialVersionUID = -1549447228701748191L;

	/**
	 * An array of {@code Cell} objects that represent the 81 different
	 * {@code Cells} on a regular Sudoku board
	 */
	protected Cell[][] cells = new Cell[9][9];

	/**
	 * An array of {@code Region} objects that represent the various rows,
	 * columns, and boxes that are part of the Sudoku game
	 */
	private Region[][] regions = new Region[3][9];

	/**
	 * The default color for the {@code Cells} and the values displayed
	 * on-screen if the {@code Cell} isn't a duplicate or part of a complete
	 * {@code Region}
	 */
	public static final Color STANDARD = new Color(0x000000);

	/**
	 * The default color for a complete {@code Region}
	 */
	public static final Color COMPLETE = new Color(0x30DB00);

	/**
	 * The default value for a {@code Cell} that contains a value that is
	 * duplicated somewhere in one of the {@code Regions} that contains the same
	 * {@code Cell}
	 */
	public static final Color DUPLICATE = new Color(0xD42F2F);

	/**
	 * The {@code Color} object that is actually used to paint the the
	 * {@code SudokuBoard}
	 */
	private Color standard = STANDARD;

	/**
	 * The {@code Color} object that is actually used to paint completed
	 * {@code Regions} on the {@code SudokuBoard}
	 */
	private Color complete = COMPLETE;

	/**
	 * The {@code Color} object that is actually used to paint duplicate
	 * {@code Cells} on the {@code SudokuBoard}
	 */
	private Color duplicate = DUPLICATE;

	/**
	 * The {@code String} name of the {@code SudokuGame}
	 */
	private String name;

	/**
	 * The {@code File} object representing the save location of the
	 * {@code SudokuGame}
	 */
	private File save;

	/**
	 * The stack that stores the history of the {@code SudokuGame}
	 */
	private ArrayDeque<Turn> history = new ArrayDeque<Turn>();

	/**
	 * The stack that stores the undone changes, before they are erased or
	 * redone
	 */
	private ArrayDeque<Turn> future = new ArrayDeque<Turn>();

	/**
	 * Constructs and initializes a new SudokuGame with a completely blank board
	 * and given name
	 * 
	 * @param n
	 *            the {@code String} name of the newly constructed
	 *            {@code SudokuGame}
	 */
	public SudokuGame(String n) {
		name = n;

		for (int y = 0; y < cells.length; y++)
			for (int x = 0; x < cells[y].length; x++)
				cells[y][x] = new Cell(x, y, standard);

		// Rows
		for (int i = 0; i < regions[0].length; i++) {
			Cell[] region = Arrays.copyOfRange(cells[i], 0, cells[i].length);
			regions[0][i] = new Region(region);

			for (Cell c : Arrays.copyOfRange(cells[i], 0, cells[i].length))
				c.setRegion(0, regions[0][i]);
		}

		// Columns
		for (int i = 0; i < regions[1].length; i++) {
			Cell[] region = new Cell[] { cells[0][0 + i], cells[1][0 + i], cells[2][0 + i], cells[3][0 + i], cells[4][0 + i], cells[5][0 + i], cells[6][0 + i], cells[7][0 + i], cells[8][0 + i] };
			regions[1][i] = new Region(region);

			for (Cell c : region)
				c.setRegion(1, regions[1][i]);
		}

		// Squares
		int j = 0;
		for (int i = 0; i < 3; i++)
			for (int x = 0; x < 3; x++) {
				Cell[] region = new Cell[] { cells[0 + (3 * i)][0 + (3 * x)], cells[0 + (3 * i)][1 + (3 * x)], cells[0 + (3 * i)][2 + (3 * x)], cells[1 + (3 * i)][0 + (3 * x)], cells[1 + (3 * i)][1 + (3 * x)], cells[1 + (3 * i)][2 + (3 * x)], cells[2 + (3 * i)][0 + (3 * x)], cells[2 + (3 * i)][1 + (3 * x)], cells[2 + (3 * i)][2 + (3 * x)] };
				regions[2][j] = new Region(region);

				for (Cell c : region)
					c.setRegion(2, regions[2][j]);
				j++;
			}

	}

	/**
	 * Returns the <code>Cell</code> at the specified xy position
	 * 
	 * @param x
	 *            the x value of the <code>Cell</code> to get
	 * @param y
	 *            the y value of the <code>Cell</code> to get
	 * @return the <code>Cell</code> at the specified xy position
	 */
	public Cell get(int x, int y) {
		return cells[x][y];
	}

	/**
	 * Sets the <code>Cell</code> contents at the specified xy position
	 * 
	 * @param x
	 *            the x value of the <code>Cell</code> to set
	 * @param y
	 *            the y value of the <code>Cell</code> to set
	 * @param v
	 *            the value to set the <code>Cell</code>'s contents to
	 * @return the value that {@code Cell} previously contained
	 */
	public int set(int x, int y, int v) {
		int prev = cells[x][y].getContent();
		cells[x][y].setContent(v);
		return prev;
	}

	/**
	 * @return the name of this {@code SudokuGame}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this {@code SudokuGame}
	 * 
	 * @param n
	 *            the {@code String} to set the name to
	 */
	public void setName(String n) {
		name = n;
	}

	/**
	 * Colors every {@code Region} in the {@code SudokuGame} to the standard
	 * {@code Color}, then checks for complete {@code Regions}, and for
	 * duplicates within {@code Regions} and colors both of those cases
	 * differently.
	 */
	public void refresh() {
		for (Region[] ra : regions)
			for (Region r : ra)
				r.colorAll(standard);
		for (Region[] ra : regions)
			for (Region r : ra)
				if (r.isComplete())
					r.colorAll(complete);

		for (Region[] ra : regions)
			for (Region r : ra)
				for (Cell c : r.getDuplicates())
					c.setColor(duplicate);
	}

	/**
	 * @return whether or not the {@code SudokuGame} is finished.
	 */
	public boolean isWon() {
		for (Region[] ra : regions)
			for (Region r : ra)
				if (!r.isComplete() || r.getDuplicates().length != 0)
					return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getSuffix() {
		return "game";
	}

	@Override
	public boolean isSaved() {
		return save != null;
	}

	@Override
	public void setSave(File f) {
		save = f;
	}

	@Override
	public File getSave() {
		return save;
	}

	/**
	 * @return whether or not the game contains duplicate numbers within
	 *         {@code Regions}
	 */
	public boolean hasDuplicates() {
		for (Region[] ra : regions)
			for (Region r : ra)
				if (r.getDuplicates().length != 0)
					return true;
		return false;
	}

	/**
	 * Registers a turn, so that the user can undo or redo {@code Turns}
	 * 
	 * @param c
	 *            the {@code Cell} that changed value
	 * @param pre
	 *            the {@code int} value before the change
	 */
	public void registerTurn(Cell c, int pre) {
		if (future.size() != 0)
			future.clear();
		history.push(new Turn(c, pre));
	}

	/**
	 * Handles the undoing of {@code Turns} and registering the {@code Turn} for
	 * redoing
	 * 
	 */
	public void undo() {
		Turn un = null;
		try {
			un = history.pop();
		} catch (NoSuchElementException e) {
			return;
		}
		future.push(un);
		un.undoChange();
		this.refresh();
	}

	/**
	 * Handles the redoing of {@code Turns} and reregistering the {@code Turn}
	 * for undoing
	 * 
	 */
	public void redo() {
		Turn un = null;
		try {
			un = future.pop();
		} catch (NoSuchElementException e) {
			return;
		}
		history.push(un);
		un.redoChange();
		this.refresh();
	}

	/**
	 * Changes the value of the three different {@code Colors} that make up the
	 * {@code SudokuBoard}
	 * 
	 * @param s
	 *            the standard {@code Color} to change to
	 * @param c
	 *            the complete {@code Color} to change to
	 * @param d
	 *            the duplicate {@code Color} to change to
	 */
	public void setColors(Color s, Color c, Color d) {
		this.standard = s;
		this.complete = c;
		this.duplicate = d;
	}

	/**
	 * @return the color scheme of the {@code SudokuBoard} with the standard at
	 *         array[0], complete at array[1], and duplicate at array[2]
	 */
	public static Color[] getDefaultColors() {
		return new Color[] { STANDARD, COMPLETE, DUPLICATE };
	}

}
