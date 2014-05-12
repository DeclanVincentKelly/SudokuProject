package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

public class SudokuSolver {

	public static boolean solveGame(SudokuGame g) {
		return solve(g, nextPoint(g));
	}

	private static boolean solve(SudokuGame g, Point p) {
		if (g.isWon())
			return true;
		else {
			ArrayList<Integer> poss = calculatePossible(g, p);
			Integer prev = 0;

			for (Integer i : poss) {
				prev = g.set(p.y, p.x, i);
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

}
