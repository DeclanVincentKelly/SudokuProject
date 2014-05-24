package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class contains a collections of static methods that serve purpose
 * related to the solving of {@code SudokuGames}
 * 
 * @author Declan
 *
 */
public class SudokuSolverToolkit {

	/**
	 * This method will solve a {@code SudokuGame} in place using a brute-force
	 * method of checking and backtracking. If no solutions are found, it will
	 * backtrack to the original state and return false.
	 * 
	 * @param g
	 *            the {@code SudokuGame} to solve
	 * @return returns whether or not the {@code SudokuGame} that was passed in
	 *         had one or more final solutions
	 */
	public static boolean solveGame(SudokuGame g) {
		return solve(g, nextPoint(g));
	}

	/**
	 * 
	 * @param g
	 *            the {@code SudokuGame} to solve
	 * @param p
	 *            the {@code Point} that the solver should start the tree of
	 *            solutions from
	 * @return returns whether or not the {@code SudokuGame} that was passed in
	 *         had one or more final solutions
	 * @see #solve(SudokuGame, Point)
	 */
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

	/**
	 * This method is used in determining whether or not there are multiple
	 * solutions to a {@code SudokuGame}. It uses the same strategies as the
	 * solve() method in this class, but doesn't edit the game in place. Instead
	 * it tallies the number of completed {@code SudokuGames} in the tree of
	 * solutions.
	 * 
	 * @param g
	 *            the {@code SudokuGame} to count solutions for
	 * @return the number of solutions for the passed in {@code SudokuGame}
	 * @see #countSolutions(SudokuGame, Point)
	 */
	public static int countSolutions(SudokuGame g) {
		return numSolutions(g, nextPoint(g));
	}

	/**
	 * 
	 * @param g
	 *            the {@code SudokuGame} to count solutions for
	 * @param p
	 *            the {@code Point} that the solver should start the tree of
	 *            solutions from
	 * @return the number of solutions for the passed in {@code SudokuGame}
	 * @see #countSolutions(SudokuGame, Point)
	 */
	private static int numSolutions(SudokuGame g, Point p) {
		if (g.isWon())
			return 1;
		else {
			ArrayList<Integer> poss = calculatePossible(g, p);
			Integer prev = 0;
			Integer sum = 0;

			for (Integer i : poss) {
				prev = g.set(p.y, p.x, i);
				int num = numSolutions(g, nextPoint(g));
				g.set(p.y, p.x, prev);
				sum += num;
			}

			return sum;
		}
	}

	/**
	 * For the given {@code SudokuGame} determines the next {@code Point} that
	 * doesn't contain any significant value.
	 * 
	 * @param g
	 *            the {@code SudokuGame} to find the next relevant point for
	 * @return the next relevant {@code Point}
	 */
	private static Point nextPoint(SudokuGame g) {
		for (Cell[] ca : g.cells)
			for (Cell c : ca)
				if (c.getContent() == 0)
					return c.getPoint();
		return null;
	}

	/**
	 * This method determines the list of possible values for a {@code Cell}
	 * within a {@code SudokuGame} by looking at the {@code Regions} that the
	 * {@code Cell} belongs to.
	 * 
	 * @param g
	 *            the {@code SudokuGame} to look in to find the list of
	 *            possibilites
	 * @param p
	 *            the {@code Point} that will designate the {@code Cell} within
	 *            the {@code SudokuGame}
	 * @return an {@code ArrayList} of type {@code Integer} containing all of
	 *         the possible values for the {@code Cell} designated by
	 *         {@code Point} onbject
	 */
	public static ArrayList<Integer> calculatePossible(SudokuGame g, Point p) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		res.addAll(Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }));
		Cell sel = g.cells[p.y][p.x];
		if (sel.getContent() != 0) {
			res.clear();
			return res;
		}
		for (Region r : sel.regions) {
			for (Cell c : r.getCells()) {
				if (res.contains(c.getContent()) && c.getContent() != 0)
					res.remove(res.indexOf(c.getContent()));
			}
		}
		return res;
	}

}
