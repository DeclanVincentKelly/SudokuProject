package game;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class GameSolver {

	private static SudokuGame g = new SudokuGame("Final");

	// C:\Users\Declan\workspace\SudokuProject\def\Default Library\Easy\E001.game

	public static void main(String[] args) {
		
		// Loading
		SudokuGame temp = null;
		try {
			temp = (SudokuGame) (new ObjectInputStream(new FileInputStream("C:\\Users\\Declan\\workspace\\SudokuProject\\def\\Default Library\\Easy\\E003.game"))).readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		for (Cell[] ca : temp.cells)
			for (Cell c : ca) {
				g.set(c.getPoint().x, c.getPoint().y, c.getContent());
				if (c.getContent() != 0)
					g.get(c.getPoint().x, c.getPoint().y).setEditable(false);
			}

		// Solving

		displayGrid(g);

		System.out.println();

		System.out.println("Starting");
		System.out.println();

		System.out.println(solve(g, nextPoint(g)));

		System.out.println();
		System.out.println("Ending");

		displayGrid(g);
	}

	private static boolean solve(SudokuGame g, Point p) {
		if (g.isWon())
			return true;
		else {
			ArrayList<Integer> poss = calculatePossible(g, p);
			Integer prev = 0;

			for (int j = 0; j < poss.size(); j++) {
				prev = g.set(p.y, p.x, poss.get(j));
				if (!solve(g, nextPoint(g))) {
					g.set(p.y, p.x, prev);
					continue;
				} else
					return true;
			}

			return false;
		}
	}

	private static Point nextPoint(SudokuGame g) {
		for (Cell[] ca : g.cells)
			for (Cell c : ca)
				if (c.getContent() == 0)
					return c.getPoint();
		return null;
	}

	private static ArrayList<Integer> calculatePossible(SudokuGame g, Point p) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		res.addAll(Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }));
		Cell sel = g.cells[p.y][p.x];
		for (Region r : sel.regions) {
			for (Cell c : r.getCells()) {
				if (res.contains(c.getContent()) && c.getContent() != 0)
					res.remove(res.indexOf(c.getContent()));
			}
		}
		return res;
	}

	@SuppressWarnings("unused")
	private static void displayEditable(SudokuGame g) {
		for (Cell[] ca : g.cells) {
			for (Cell c : ca) {
				if ((c.getPoint().x + 1) % 3 == 0)
					System.out.print((c.isEditable() ? "T" : "F") + " | ");
				else
					System.out.print((c.isEditable() ? "T" : "F") + "  ");
			}
			if ((ca[0].getPoint().y + 1) % 3 == 0 && ca[0].getPoint().y != 8)
				System.out.println("\n------------------------------");
			else
				System.out.println();
		}
	}

	private static void displayGrid(SudokuGame g) {
		for (Cell[] ca : g.cells) {
			for (Cell c : ca) {
				if ((c.getPoint().x + 1) % 3 == 0)
					System.out.print(c.getContent() + " | ");
				else
					System.out.print(c.getContent() + "  ");
			}
			if ((ca[0].getPoint().y + 1) % 3 == 0 && ca[0].getPoint().y != 8)
				System.out.println("\n------------------------------");
			else
				System.out.println();
		}
	}

}
