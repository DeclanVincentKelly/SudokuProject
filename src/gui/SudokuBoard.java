package gui;

//TODO Finish Javadoc

import game.SudokuGame;

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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class SudokuBoard extends JPanel {

	private final SudokuGame game;
	private static final Color backgroundColor = Color.WHITE;
	private static final Color defaultColor = Color.BLACK;
	private static final int cellSize = 55;
	private static final int borderWidth = 6;
	private static final int dx = 20, dy = 40;
	private static final int boxLength = 9;
	private Rectangle[][] boxes = new Rectangle[boxLength][boxLength];
	private boolean engaged = false;
	private Point selection = new Point(0, 0);
	private boolean editConstant = false;

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
	}

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

	private class BoardDirectionAction extends AbstractAction {

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

	private class BoardNumberAction extends AbstractAction {

		BoardNumberAction(String num) {
			putValue(NAME, num);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (engaged && !editConstant) {
				game.get(selection.y, selection.x).setContent(Integer.parseInt(String.valueOf(getValue(NAME))));
				repaint();
			}
		}

	}

	private class BoardEnterAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(!editConstant)
				engaged = !engaged;
			else {
				game.get(selection.y, selection.x).setEditable(!game.get(selection.y, selection.x).isEditable());
				repaint();
			}
				
			repaint();
		}
	}

	private void moveSelection(int x, int y) {
		x = x < 0 ? x + 9 : x % 9;
		y = y < 0 ? y + 9 : y % 9;
		selection.move(x, y);
	}

	private void scrollValue(Point p, int units) {
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (boxes[i][j].contains(p)) {
					int x = game.get(i, j).getContent();
					if (units > 0)
						game.get(i, j).setContent((x - 1) < 0 ? x + 9 : (x - 1) % 10);
					else
						game.get(i, j).setContent((x + 1) < 0 ? x + 9 : (x + 1) % 10);
				}
			}
		}
		repaint();
	}

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
			if(game.isWon())
				drawWin();
		} else {
			drawConstantBoxes(g2);
			drawFadedContents(g2);
		}

	}

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

	private void drawBoxes(Graphics2D g) {
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (game.get(i, j).getPoint().equals(selection) && engaged) {
					g.setColor(defaultColor);
					g.setStroke(new BasicStroke(4));
					g.draw(boxes[i][j]);
					g.setStroke(new BasicStroke(2));
				}

				g.setColor(game.get(i, j).getColor());
				g.draw(boxes[i][j]);

			}
		}
		g.setColor(defaultColor);
	}

	private void drawOccupants(Graphics2D g) {
		g.setFont(new Font("Serif", Font.PLAIN, 40));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		for (int i = 0; i < boxes.length; i++) {
			for (int j = 0; j < boxes[i].length; j++) {
				if (game.get(i, j).getContent() != 0) {
					if (game.get(i, j).isEditable())
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
	
	private void drawWin() {
		//TODO Complete the win animation
	}

	@Override
	public Dimension getPreferredSize() {
		return SudokuBoard.getBoardPreferredSize();
	}
	
	public static Dimension getBoardPreferredSize() {
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

	public SudokuGame getGame() {
		return game;
	}

	public boolean getEditConstant() {
		return editConstant;
	}

	public void setEditConstant(boolean v) {
		editConstant = v;
		if(v)
			engaged = true;
		else
			engaged = false;
	}
}