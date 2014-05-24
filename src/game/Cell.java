package game;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

/**
 * A cell construct that contain information about color, {@code int} content,
 * and location designated by a {@code Point}.
 * 
 * @author Declan
 *
 */

public class Cell implements Serializable {

	private static final long serialVersionUID = 5110017981588016179L;

	/**
	 * The color of this cell, represented by a {@code Color} object.
	 *
	 * @serial
	 * @see #getColor()
	 * @see #setColor(Color)
	 */
	private Color color;

	/**
	 * The {@code int} content of the cell. Value can range from 0-9, with 0
	 * representing a blank cell.
	 *
	 * @serial
	 * @see #getContent()
	 * @see #setContent(int)
	 */
	private int cellContent;

	/**
	 * The {@code boolean} representation of whether or not the cell can be
	 * edited.
	 *
	 * @serial
	 * @see #isEditable()
	 * @see #setEditable(boolean)
	 */
	private boolean editable = true;

	/**
	 * The {@code Point} object representation of the {@code Cell}'s location
	 * within 2 dimensional space.
	 *
	 * @serial
	 * @see #getPoint()
	 */
	private final Point point;

	/**
	 * The array containing references to the {@code Regions} that this
	 * {@code Cell} belongs to.
	 *
	 * @serial
	 * @see #setRegion(int, Region)
	 * @see game.Region
	 */
	protected final Region[] regions = new Region[3];

	/**
	 * Constructs and initializes a {@code Cell} with the same location as the
	 * specified {@code Point} object, and the same color as the specified
	 * {@code Color} object. The content of the {@code Cell} is 0.
	 * 
	 * @param p
	 *            a point
	 * @param c
	 *            a color
	 */
	public Cell(Point p, Color c) {
		this(0, c, p);
	}

	/**
	 * Constructs and initializes a {@code Cell} with a location at the
	 * specified {@code (x,y)} location in the coordinate space, and the same
	 * color as the specified {@code Color} object. The content of the
	 * {@code Cell} is 0.
	 * 
	 * @param x
	 *            the X coordinate of the newly constructed <code>Cell</code>
	 * @param y
	 *            the Y coordinate of the newly constructed <code>Cell</code>
	 * @param c
	 *            the color of the {@code Cell}
	 */
	public Cell(int x, int y, Color c) {
		this(0, c, new Point(x, y));
	}

	/**
	 * Constructs and initializes a {@code Cell} with a location at the
	 * specified {@code Point} in the coordinate space, and the same color as
	 * the specified {@code Color} object. The initial content of the
	 * {@code Cell} is the value passed.
	 * 
	 * @param con
	 *            the initial content newly constructed <code>Cell</code>
	 * @param c
	 *            the color of the {@code Cell}
	 * @param p
	 *            the location of the newly constructed <code>Cell</code> in
	 *            {@code (x,y)} coordinate space, designated by a {@code Point}
	 *            object.
	 */
	public Cell(int con, Color c, Point p) {
		cellContent = con;
		color = c;
		point = p;
	}

	/**
	 * @return the color of the {@code Cell}
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return the {@code int} content of the {@code Cell}
	 */
	public int getContent() {
		return cellContent;
	}

	/**
	 * @return {@code boolean} representing whether or not the cell is editable.
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @param c
	 *            the {@code Color} to set the {@code Color} of the {@code Cell}
	 *            to.
	 */
	public void setColor(Color c) {
		this.color = c;
	}

	/**
	 * @param c
	 *            the {@code int} to set the content of the {@code Cell} to.
	 */
	public void setContent(int c) {
		if (editable)
			this.cellContent = c;
	}

	/**
	 * @param c
	 *            the {@code boolean} to set the editablility of the
	 *            {@code Cell} to.
	 */
	public void setEditable(boolean b) {
		this.editable = b;
	}

	/**
	 * @param i
	 *            the {@code int} index of the {@code Region} array to set the
	 *            value of.
	 * @param r
	 *            the {@code Region} object to set the specified index of
	 *            {@code Region} array to.
	 */
	protected void setRegion(int i, Region r) {
		regions[i] = r;
	}

	/**
	 * @return the {@code Point} object representing the location of the
	 *         {@code Cell}.
	 */
	public Point getPoint() {
		return point;
	}

	@Override
	public String toString() {
		return "[" + point + ":" + color + ":" + cellContent + "]";
	}
}
