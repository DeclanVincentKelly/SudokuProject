package game;

//TODO Finish Javadoc

import gui.SudokuSerializable;

import java.awt.Color;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;

public class SudokuGame implements SudokuSerializable {

	private static final long serialVersionUID = -1549447228701748191L;

	/**
	 * This array represents the game board with 81 individual cells capable of
	 * different content and color
	 */
	protected Cell[][] cells = new Cell[9][9];

	/**
	 * This array represents the different regions of the board that must have
	 * their requirements fulfilled in order to win
	 */
	private Region[][] regions = new Region[3][9];

	/**
	 * The two stacks in charge of any undo/redo operations
	 */
	@SuppressWarnings("unused")
	private ArrayDeque<Turn> history, future;

	private static final Color defaultColor = new Color(0x000000);
	private static final Color completeColor = new Color(0x30DB00);
	private static final Color duplicateColor = new Color(0xD42F2F);

	private String name;
	private File save;

	/**
	 * Constructs a new SudokuGame with a completely blank board and given name
	 */
	public SudokuGame(String n) {
		name = n;

		for (int y = 0; y < cells.length; y++)
			for (int x = 0; x < cells[y].length; x++)
				cells[y][x] = new Cell(x, y);

		//Rows
		for (int i = 0; i < regions[0].length; i++) {
			Cell[] region = Arrays.copyOfRange(cells[i], 0, cells[i].length);
			regions[0][i] = new Region(region);
			
			for(Cell c: Arrays.copyOfRange(cells[i], 0, cells[i].length))
				c.setRegion(0, regions[0][i]);
		}

		//Columns
		for (int i = 0; i < regions[1].length; i++) {
			Cell[] region = new Cell[] { cells[0][0 + i], cells[1][0 + i], cells[2][0 + i], cells[3][0 + i], cells[4][0 + i], cells[5][0 + i], cells[6][0 + i], cells[7][0 + i], cells[8][0 + i] };
			regions[1][i] = new Region(region);
			
			for(Cell c: region)
				c.setRegion(1, regions[1][i]);
		}
			
		//Squares
		int j = 0;
		for (int i = 0; i < 3; i++)
			for (int x = 0; x < 3; x++) {
				Cell[] region = new Cell[] { cells[0 + (3 * i)][0 + (3 * x)], cells[0 + (3 * i)][1 + (3 * x)], cells[0 + (3 * i)][2 + (3 * x)], cells[1 + (3 * i)][0 + (3 * x)], cells[1 + (3 * i)][1 + (3 * x)], cells[1 + (3 * i)][2 + (3 * x)], cells[2 + (3 * i)][0 + (3 * x)], cells[2 + (3 * i)][1 + (3 * x)], cells[2 + (3 * i)][2 + (3 * x)] };
				regions[2][j] = new Region(region);
				
				for(Cell c: region)
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
	 */
	public void set(int x, int y, int v) {
		cells[x][y].setContent(v);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public void setName(String n) {
		name = n;
	}

	public void refresh() {
		for (Region[] ra : regions)
			for (Region r : ra)
				r.colorAll(defaultColor);

		for (Region[] ra : regions)
			for (Region r : ra)
				if (r.isComplete())
					r.colorAll(completeColor);

		for (Region[] ra : regions)
			for (Region r : ra)
				for (Cell c : r.getDuplicates())
					c.setColor(duplicateColor);
	}
	
	public boolean isWon() {
		for (Region[] ra : regions)
			for (Region r : ra)
				if(!r.isComplete() || r.getDuplicates().length != 0)
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

}
