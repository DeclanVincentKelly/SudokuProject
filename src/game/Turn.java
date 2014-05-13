package game;

//TODO Finish Javadoc
//TODO Finish implementation

import java.io.Serializable;

public class Turn implements Serializable {

	private static final long serialVersionUID = -3828969061449890559L;
	
	private final Cell changed;
	private final int prevValue;
	private final int postValue;

	public Turn(Cell c, int prev, int post) {
		changed = c;
		prevValue = prev;
		postValue = post;
	}

	/**
	 * @return the cell that was changed
	 */
	public Cell getChanged() {
		return changed;
	}

	/**
	 * @return the previous value
	 */
	public int getPrevValue() {
		return prevValue;
	}

	/**
	 * @return the post-change value
	 */
	public int getPostValue() {
		return postValue;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "(" + changed + ": " + prevValue + " -> " + postValue + ")";
	}
}
