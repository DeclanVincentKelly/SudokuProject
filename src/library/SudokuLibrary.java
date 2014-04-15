package library;

import game.SudokuGame;
import gui.SudokuSerializable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class SudokuLibrary implements SudokuSerializable {

	private static final long serialVersionUID = -5931229505819086546L;

	private final File homeFolder;
	private final Properties libProperties;
	private final HashMap<String, List<SudokuGame>> games;

	private File save;

	public SudokuLibrary(File folder) {
		homeFolder = folder;
		libProperties = new Properties();
		games = new HashMap<String, List<SudokuGame>>();
		load();
		update();
		save();
		populateGames();
	}

	private void populateGames() {
		File[] difficulties = homeFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}

		});

		for (File f : difficulties) {
			File[] game = f.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					File t = new File(dir, name);
					return t.isFile() && t.getName().endsWith(".game");
				}

			});

			SudokuGame[] temp = new SudokuGame[game.length];
			for (int i = 0; i < temp.length; i++) {
				temp[i] = new SudokuGame(game[i].getName().substring(0, game[i].getName().indexOf(".")));
				temp[i].saveAt(game[i]);
			}

			games.put(f.getName(), Arrays.asList(temp));
		}
	}

	public File[] getDifficulties() {
		updateDifficulties();
		String[] n = libProperties.getProperty("difficulty").split(",");
		File[] temp = new File[n.length];
		for (int i = 0; i < temp.length; i++)
			temp[i] = new File(homeFolder, n[i]);
		return temp;
	}

	private void update() {
		updateDifficulties();
		updateCount();
	}

	private void updateDifficulties() {
		String[] directories = homeFolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		String temp = directories[0];
		if (directories.length > 1)
			for (int i = 1; i < directories.length; i++)
				temp += "," + directories[i];

		libProperties.setProperty("difficulty", temp);
	}

	private void updateCount() {
		int count = 0;
		for (Entry<String, List<SudokuGame>> e : games.entrySet())
			count += e.getValue().size();

		libProperties.put("gameCount", count);
	}

	public String getName() {
		return libProperties.getProperty("name");
	}

	public int getGameCount() {
		return Integer.parseInt(libProperties.getProperty("gameCount"));
	}

	public String getCreator() {
		return libProperties.getProperty("creator");
	}

	public Date getDateCreated() {
		SimpleDateFormat f = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
		try {
			return f.parse(libProperties.getProperty("createDate"));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public HashMap<String, List<SudokuGame>> getGames() {
		return games;
	}

	public void load() {
		try {
			libProperties.load(new FileInputStream(new File(homeFolder, ".sudlib")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			libProperties.store(new FileOutputStream(new File(homeFolder, ".sudlib")), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getSuffix() {
		return "sudlib";
	}

	@Override
	public boolean isSaved() {
		return save != null;
	}

	@Override
	public void saveAt(File f) {
		save = f;
	}

	@Override
	public File getSave() {
		// TODO Auto-generated method stub
		return save;
	}

}
