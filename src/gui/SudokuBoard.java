package gui;

import game.SudokuGame;
import game.SudokuSolverToolkit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * This class is responsible for the displaying of the the {@code SudokuGame},
 * as well as registering all the different key and mouse events that affect the
 * state of the {@code SudokuGame}.
 * 
 * @author Declan
 *
 */
@SuppressWarnings("serial")
public class SudokuBoard extends JPanel {

	/**
	 * The {@code SudokuGame} that this {@code SudokuBoard} is responsible for
	 */
	private final SudokuGame game;

	/**
	 * The background color of the {@code SudokuBoard}
	 */
	private static final Color backgroundColor = Color.WHITE;

	/**
	 * The default brush {@code Color} for the {@code SudokuBoard}
	 */
	private static final Color defaultColor = SudokuGame.getDefaultColors()[0];

	/**
	 * The side length of one cell on the {@code SudokuBoard}
	 */
	private static final int cellSize = 55;

	/**
	 * The width of the border of a cell on the {@code SudokuBoard}
	 */
	private static final int borderWidth = 6;

	/**
	 * The distance to inset the contents of each cell on the
	 * {@code SudokuBoard}
	 */
	private static final int dx = 20, dy = 40;

	/**
	 * The number of cells in one row on the {@code SudokuBoard}
	 */
	private static final int boxLength = 9;

	/**
	 * An array of {@code Rectangle} objects that contain the location and size
	 * information for the cells on the {@code SudokuBoard}
	 */
	private Rectangle[][] boxes = new Rectangle[boxLength][boxLength];

	/**
	 * A boolean indicating whether or not the keyboard editing mode is engaged
	 */
	private boolean engaged = false;

	/**
	 * The coordinate indicating the current keyboard cell selection
	 */
	private Point selection = new Point(0, 0);

	/**
	 * A boolean indicating whether or not the constant editing mode is engaged.
	 * This supercedes the keyboard editing mode
	 */
	private boolean editConstant = false;

	/**
	 * Constructs and initializes a {@code SudokuBoard} object, which will be
	 * used in conjunction with the specified {@code SudokuGame} object
	 * 
	 * @param g
	 *            the {@code SudokuGame} object that this {@code SudokuBoard}
	 *            will belong to
	 */
	public SudokuBoard(SudokuGame g) {
		this.game = g;
		createBoxes();
		setFocusable(true);
		setBackground(backgroundColor);

		addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL && !engaged)
					scrollValue(e.getPoint(), e.getUnitsToScroll());
			}

		});

		addKeyBindings();
		setToolTipText("");
	}

	/**
	 * Adds the various keybindings that are used to interact with the
	 * underlying {@code SudokuGame} and change the value of {@code Cells}
	 */
	private void addKeyBindings() {
		InputMap in = getInputMap();
		ActionMap ap = getActionMap();

		in.put(KeyStroke.getKeyStroke("ENTER"), "ENTER");
		ap.put("ENTER", new BoardEnterAction());

		in.put(KeyStroke.getKeyStroke("UP"), "UP");
		ap.put("UP", new BoardDirectionAction("Up"));

		in.put(KeyStroke.getKeyStroke("DOWN"), "DO");
		ap.put("DO", new BoardDirectionAction("Down"));

		in.put(KeyStroke.getKeyStroke("LEFT"), "LE");
		ap.put("LE", new BoardDirectionAction("Left"));

		in.put(KeyStroke.getKeyStroke("RIGHT"), "RI");
		ap.put("RI", new BoardDirectionAction("Right"));

		for (int i = 0; i <= 9; i++) {
			in.put(KeyStroke.getKeyStroke(String.valueOf(i)), String.valueOf(i) + "_NAV");
			in.put(KeyStroke.getKeyStroke("NUMPAD" + String.valueOf(i)), String.valueOf(i) + "_NAV");
			ap.put(String.valueOf(i) + "_NAV", new BoardNumberAction(String.valueOf(i)));
		}
	}

	/**
	 * This class represents an action taken with one of the direction keys or
	 * the numpad direction keys in order to change the selected {@code Cell}
	 * 
	 * @author Declan
	 *
	 */
	private class BoardDirectionAction extends AbstractAction {

		/**
		 * Constructs and initializes a {@code BoardDirectionAction} which is
		 * charge of handling one key direction, and the resulting action
		 * 
		 * @param n
		 *            the name of the action
		 */
		BoardDirectionAction(String n) {
			putValue(SHORT_DESCRIPTION, n);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (engaged) {
				switch ((String) getValue(SHORT_DESCRIPTION)) {
				case "Up":
					moveSelection(selection.x, selection.y - 1);
					repaint();
					break;
				case "Down":
					moveSelection(selection.x, selection.y + 1);
					repaint();
					break;
				case "Left":
					moveSelection(selection.x - 1, selection.y);
					repaint();
					break;
				case "Right":
					moveSelection(selection.x + 1, selection.y);
					repaint();
					break;
				}
			}
		}

	}

	/**
	 * This class represents an action taken with one the number keys or one of
	 * the number numpad keys, in order to change the value of the selected
	 * {@code Cell} in the underlying {@code SudokuGame}
	 * 
	 * @author Declan
	 *
	 */
	private class BoardNumberAction extends AbstractAction {

		/**
		 * Constructs and initializes a {@code BoardDirectionAction} which is in
		 * charge of handling one number, either numpad or not, and the
		 * resulting action
		 * 
		 * @param num
		 *            the {@code String} representation of the number on the
		 *            keyboard that corresponds to this action
		 */
		BoardNumberAction(String num) {
			putValue(NAME, num);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (engaged && !editConstant) {
				int parsed = Integer.parseInt(String.valueOf(getValue(NAME)));
				int prev = game.get(selection.y, selection.x).getContent();
				game.get(selection.y, selection.x).setContent(parsed);
				game.registerTurn(game.get(selection.y, selection.x), prev);
				repaint();
			}
		}

	}

	/**
	 * THis class represents an action taken with either Enter key, numpad or
	 * not, and the resulting change in the displaying of the {@code SudokuGame}
	 * , or a change in the underlying {@code Cells}
	 * 
	 * @author Declan
	 *
	 */
	private class BoardEnterAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!editConstant)
				engaged = !engaged;
			else {
				game.get(selection.y, selection.x).setEditable(!game.get(selection.y, selection.x).isEditable());
				repaint();
			}

			repaint();
		}
	}

	/**
	 * This method is used to simulate a torus-like construct of a board,
	 * allowing the user to move the selection off the edge of the
	 * {@code SudokuBoard} and have it appear on the opposite side
	 * 
	 * @param x
	 *            the x coordinate to move the {@code Point} selection to
	 * @param y
	 *            the y coordinate to move the {@code Point} selection to
	 */
	private void moveSelection(int x, int y) {
		x = x < 0 ? x + 9 : x % 9;
		y = y < 0 ? y + 9 : y % 9;
		selection.move(x, y);
	}

	/**
	 * Used to manage the scrolling method of changing the value of a
	 * {@code Cell}
	 * 
	 * @param p
	 *            the {@code Point} used to find the {@code Cell} to change the
	 *            value of
	 * @param units
	 *            the number of units that the user scrolled
	 */
	private void scrollValue(Point p, int units) {
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (boxes[i][j].contains(p)) {
					int x = game.get(i, j).getContent();
					if (units > 0)
						game.get(i, j).setContent((x - 1) < 0 ? x + 9 : (x - 1) % 10);
					else
						game.get(i, j).setContent((x + 1) < 0 ? x + 9 : (x + 1) % 10);

					game.registerTurn(game.get(i, j), x);
				}
			}
		}
		repaint();
	}

	/**
	 * This method initializes the values of all the {@code Rectangle} objects
	 * in the array, giving them initial locations an dimensions
	 */
	private void createBoxes() {
		int x = borderWidth;
		int y = borderWidth;
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				boxes[i][j] = new Rectangle(new Point(x, y), new Dimension(cellSize + 2, cellSize + 2));
				x += cellSize + borderWidth;
				if (j == 2 || j == 5)
					x += borderWidth;
			}
			y += cellSize + borderWidth;
			if (i == 2 || i == 5)
				y += borderWidth;
			x = borderWidth;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g2);
		if (game == null)
			return;

		g2.setStroke(new BasicStroke(2));
		g2.setColor(backgroundColor);
		if (!editConstant) {
			drawBoxes(g2);
			drawOccupants(g2);
		} else {
			drawConstantBoxes(g2);
			drawFadedContents(g2);
		}

	}

	/**
	 * Draws the contents of each cell when the editConstant toggle is switched
	 * to true
	 * 
	 * @param g
	 *            the {@code Graphics2D} object used to draw the contents
	 */
	private void drawFadedContents(Graphics2D g) {
		g.setFont(new Font("Serif", Font.PLAIN, 40));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.setColor(Color.GRAY);
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (game.get(i, j).getContent() != 0) {
					int x = boxes[i][j].x + dx;
					int y = boxes[i][j].y + dy;
					g.drawString(String.valueOf(game.get(i, j).getContent()), x, y);
				}
			}
		}

		g.setColor(defaultColor);
	}

	/**
	 * Draws the cells when the editConstant toggle is switched to true
	 * 
	 * @param g
	 *            the {@code Graphics2D} object used to draw the {@code Cells}
	 */
	private void drawConstantBoxes(Graphics2D g) {
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (game.get(i, j).isEditable())
					g.setColor(Color.WHITE);
				else
					g.setColor(Color.BLACK);

				g.fill(boxes[i][j]);

				if (game.get(i, j).getPoint().equals(selection) && engaged) {
					if (game.get(i, j).isEditable())
						g.setColor(Color.BLACK);
					else
						g.setColor(Color.WHITE);
					g.setStroke(new BasicStroke(4));
					g.draw(boxes[i][j]);
					g.setStroke(new BasicStroke(2));
				}
			}
		}

		g.setColor(backgroundColor);
	}

	/**
	 * Used to draw every {@code Cell} the is displayed on the
	 * {@code SudokuBoard} when the editConstant toggle is not in effect
	 * 
	 * @param g
	 *            the {@code Graphics2D} object used to draw each {@code Cell}
	 */
	private void drawBoxes(Graphics2D g) {
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (game.get(i, j).getPoint().equals(selection) && engaged) {
					g.setColor(defaultColor);
					g.setStroke(new BasicStroke(4));
					g.draw(boxes[i][j]);
					g.setStroke(new BasicStroke(2));
				}

				if (SudokuGameFrame.highlighting)
					g.setColor(game.get(i, j).getColor());
				else
					g.setColor(defaultColor);

				g.draw(boxes[i][j]);

			}
		}
		g.setColor(defaultColor);
	}

	/**
	 * This method is used to draw the contents of each {@code Cell} when the
	 * editConstant toggle is not in effect
	 * 
	 * @param g
	 *            the {@code Graphics2D} object used to draw the contents
	 */
	private void drawOccupants(Graphics2D g) {
		g.setFont(new Font("Serif", Font.PLAIN, 40));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (game.get(i, j).getContent() != 0) {
					if (game.get(i, j).isEditable() && SudokuGameFrame.highlighting)
						g.setColor(game.get(i, j).getColor().darker());
					else
						g.setColor(defaultColor);
					int x = boxes[i][j].x + dx;
					int y = boxes[i][j].y + dy;
					g.drawString(String.valueOf(game.get(i, j).getContent()), x, y);
				}
			}
		}

		g.setColor(defaultColor);
	}

	@Override
	public Dimension getPreferredSize() {
		int l = (int) ((2 * (borderWidth - 1)) + (boxLength * (cellSize + 4)) + (2 * (boxLength - 1)) + (2 * borderWidth));
		return new Dimension(l, l);
	}

	@Override
	public void repaint() {
		if (game != null)
			game.refresh();
		super.repaint();
	}

	@Override
	public String toString() {
		return game.getName() + " Board";
	}

	/**
	 * 
	 * @return the underlying {@code SudokuGame} that this {@code SudokuBoard}
	 *         is displaying
	 */
	public SudokuGame getGame() {
		return game;
	}

	/**
	 * 
	 * @return whether or not this {@code SudokuBoard} is currently in the
	 *         editConstant mode
	 */
	public boolean getEditConstant() {
		return editConstant;
	}

	/**
	 * Sets the editConstant mode to the specified boolean
	 * 
	 * @param v
	 *            the {@code boolean} value to set the editConstant toggle to
	 */
	public void setEditConstant(boolean v) {
		editConstant = v;
		if (v)
			engaged = true;
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		if (SudokuGameFrame.tooltips) {
			Point p = event.getPoint();
			ArrayList<Integer> poss = new ArrayList<Integer>();
			for (int i = 0; i < boxes.length; i++)
				for (int j = 0; j < boxes[i].length; j++)
					if (boxes[i][j].contains(p))
						poss = SudokuSolverToolkit.calculatePossible(game, new Point(j, i));
			return "Possibilities: " + poss.toString();
		} else
			return "";
	}

}