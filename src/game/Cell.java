package game;

//TODO Finish Javadoc

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

public class Cell implements Serializable {

	private static final long serialVersionUID = 5110017981588016179L;

	/**
	 * This is the current color of this particular cell's box
	 */
	private Color color;

	/**
	 * This int is the content of the particular cell, ranging from 1-9 A value
	 * of zero indicates that the cell is empty and shouldn't be displayed
	 */
	private int cellContent;

	/**
	 * This boolean determines whether or not this particular <code>Cell</code>
	 * 's content can be edited
	 */
	private boolean editable = true;

	/**
	 * 
	 */
	private final Point point;
	
	protected final Region[] regions = new Region[3];

	/**
	 * Class default constructor specifying initial values of
	 * <code>content</code> and <code>content</code> to 0 and
	 * <code>Color.BLACK</code>
	 */
	public Cell(Point p, Color c) {
		this(0, c, p);
	}

	/**
	 * 
	 */
	public Cell(int x, int y, Color c) {
		this(0, c, new Point(x, y));
	}

	/**
	 * Class constructor specifying initial values of <code>content</code> and
	 * <code>content</code>
	 */
	public Cell(int con, Color c, Point p) {
		cellContent = con;
		color = c;
		point = p;
	}

	/**
	 * @return the boxColor
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return the cellContent
	 */
	public int getContent() {
		return cellContent;
	}

	/**
	 * @return whether or not this cell is editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @param boxColor
	 *            the boxColor to set
	 */
	public void setColor(Color boxColor) {
		this.color = boxColor;
	}

	/**
	 * @param content
	 *            the cellContent to set
	 */
	public void setContent(int content) {
		if (editable)
			this.cellContent = content % 10;
	}

	/**
	 * @param editable
	 *            the editability of the cell
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	protected void setRegion(int i, Region r) {
		regions[i] = r;
	}

	/**
	 * @return the point
	 */
	public Point getPoint() {
		return point;
	}
	
	@Override
	public String toString() {
		return "[" + point + ":" + color + ":" + cellContent + "]";
	}
}
