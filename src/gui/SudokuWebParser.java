package gui;

import game.Cell;
import game.SudokuGame;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is intended to create games for users who have no game loaded when
 * the game starts. It makes use of the website show.websudoku.com, and I'm
 * hopeful that by not using this for profit, it is possible that what I'm doing
 * it kinda legal. If not this section can be deleted.
 * 
 * @author Declan
 *
 */
public class SudokuWebParser {

	/**
	 * This helped method returns the HTML of the show.websudoku.com in all of
	 * its glory.
	 * 
	 * @param urlToRead
	 *            The URL that the method will GET the HTML from
	 * @return The HTML of the webpage specified
	 */
	private static String getHTML(String url) {
		URL u;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";

		try {
			u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
			result = "";
		}

		return result;
	}

	/**
	 * This method ties the other two in this class together and is responsible
	 * for parsing the HTML via regex.
	 * 
	 * @return A {@code SudokuGame} that is stripped from the websudoku.com
	 *         database
	 */
	public static SudokuGame stripGame() {
		String doc = getHTML("http://show.websudoku.com/?level=4&set_id=9582606826");
		Pattern p = Pattern.compile("READONLY\\sVALUE=\"([^<>])\"\\sID=([^<>]{3})", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(doc);
		ArrayList<Point> points = new ArrayList<>();
		ArrayList<Integer> values = new ArrayList<>();
		SudokuGame game = new SudokuGame("Unsaved Game");

		int count = 0;
		while (m.find()) {
			values.add(Integer.parseInt(m.group(1)));
			String temp = m.group(2);
			points.add(new Point(Integer.parseInt(temp.substring(1, 2)), Integer.parseInt(temp.substring(2, 3))));
			count += 1;
		}
		if (count == 0)
			return game;

		game = new SudokuGame("Web Game");
		fillGame(game, values, points);

		return game;
	}

	/**
	 * This method fill the blank {@code SudokuGame} with the values at the
	 * specified {@code Points} and sets those {@code Points} to be constant
	 * 
	 * @param daily
	 *            the blank game to fill
	 * @param values
	 *            the {@code ArrayList} of values to fill the {@code SudokuGame}
	 *            with
	 * @param points
	 *            the {@code ArrayList} of {@code Points} to fill the
	 *            {@code SudokuGame} with
	 */
	private static void fillGame(SudokuGame daily, ArrayList<Integer> values, ArrayList<Point> points) {
		for (int i = 0; i < values.size(); i++) {
			Cell c = daily.get(points.get(i).y, points.get(i).x);
			c.setContent(values.get(i));
			c.setEditable(false);
		}
	}

}
