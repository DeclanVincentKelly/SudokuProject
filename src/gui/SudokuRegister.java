package gui;

//TODO Finish Javadoc

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class SudokuRegister<T extends SudokuSerializable> implements Serializable {

	private static final long serialVersionUID = 5580281115018204171L;

	private ArrayList<T> registered = new ArrayList<T>();

	public void register(T g) {
		if (!registered.contains(g)) {
			String newName = g.getName();
			if (!isUnique(g.getName())) {
				int count = 1;
				String orig = newName;
				do {
					newName = orig + " (" + count + ")";
					count++;

				} while (!isUnique(newName));
			}
			g.setName(newName);

			registered.add(g);
		}
	}

	private boolean isUnique(String name) {
		for (T g : registered)
			if (g.getName().equals(name))
				return false;
		return true;
	}

	public void deregister(int i) {
		registered.remove(i);
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
		this.registered = temp.registered;
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

	public boolean isEmpty() {
		return registered.isEmpty();
	}

	public boolean contains(T obj) {
		return registered.contains(obj);
	}

	public void clear() {
		registered.clear();
	}

	public int indexOf(T obj) {
		return registered.indexOf(obj);
	}

	public Collection<T> values() {
		return registered;
	}

}
