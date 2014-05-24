package game;

import java.io.Serializable;

/**
 * A construct that represents a change in the value of a {@code Cell}. It
 * contains a reference to the {@code Cell} that was changed, as well as the
 * {@code int} value of the {@code Cell} before and after the change.
 * 
 * @author Declan
 *
 */
public class Turn implements Serializable {

	private static final long serialVersionUID = -3828969061449890559L;

	/**
	 * The {@code Cell} that underwent a change
	 */
	private final Cell changed;

	/**
	 * The previous value of the changed {@code Cell}
	 */
	private final int prevValue;

	/**
	 * The value of the {@code Cell} after it changed
	 */
	private final int postValue;

	/**
	 * Constructs and initializes a {@code Turn} object, which is used to track
	 * the history of the {@code SudokuGame}.
	 * 
	 * @param c
	 *            the {@code Cell} that was changed
	 * @param prev
	 *            the previous value of the {@code Cell}
	 */
	public Turn(Cell c, int prev) {
		changed = c;
		prevValue = prev;
		postValue = c.getContent();
	}

	/**
	 * @return the {@code Cell} that underwent a change
	 */
	public Cell getChanged() {
		return changed;
	}

	/**
	 * @return the previous value of the changed {@code Cell}
	 */
	public int getPrevValue() {
		return prevValue;
	}

	/**
	 * @return the value of the {@code Cell} after it changed
	 */
	public int getPostValue() {
		return postValue;
	}

	@Override
	public String toString() {
		return "(" + changed + ": " + prevValue + " -> " + postValue + ")";
	}
}
