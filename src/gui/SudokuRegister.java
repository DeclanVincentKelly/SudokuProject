package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class SudokuRegister<T extends SudokuSerializable> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5580281115018204171L;

	private HashMap<String, T> registeredGames = new HashMap<String, T>();

	public void register(String n, T g) {
		registeredGames.put(n, g);
	}

	public void deregister(String n) {
		registeredGames.remove(n);
	}

	public void saveState(String n) {
		try {
			FileOutputStream fos = new FileOutputStream(n);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void loadState(String n) {
		SudokuRegister<T> temp = null;
		try {
			FileInputStream fis = new FileInputStream(n);
			ObjectInputStream ois = new ObjectInputStream(fis);
			temp = (SudokuRegister<T>) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			temp = new SudokuRegister<T>();
		}
		this.registeredGames = temp.registeredGames;
	}

	public static SudokuSerializable load(File in) {
		SudokuSerializable re = null;
		
		try {
			FileInputStream fis = new FileInputStream(in);
			ObjectInputStream ois = new ObjectInputStream(fis);
			re = (SudokuSerializable) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return re;

	}

	public static void save(SudokuSerializable out) {
		try {
			File loc = out.isSaved() ? out.getSave() : new File(out.getName() + out.getSuffix());
			FileOutputStream fos = new FileOutputStream(loc);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(out);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 * @see java.util.HashMap#isEmpty()
	 */
	public boolean isEmpty() {
		return registeredGames.isEmpty();
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.HashMap#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return registeredGames.containsKey(key);
	}

	/**
	 * 
	 * @see java.util.HashMap#clear()
	 */
	public void clear() {
		registeredGames.clear();
	}

	/**
	 * @param value
	 * @return
	 * @see java.util.HashMap#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return registeredGames.containsValue(value);
	}

	/**
	 * @return
	 * @see java.util.HashMap#values()
	 */
	public Collection<T> values() {
		return registeredGames.values();
	}

}
