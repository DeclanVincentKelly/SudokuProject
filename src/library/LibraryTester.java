package library;

import java.io.File;
import java.util.Arrays;

public class LibraryTester {

	public static void main(String[] args) {
		SudokuLibrary lib = new SudokuLibrary(new File("C:/Users/Declan/workspace/SudokuProject/src/def/Default Library"));
		System.out.println(lib.getName());		
		System.out.println(lib.getCreator());
		System.out.println(lib.getDateCreated());
		System.out.println(lib.getGameCount());
		System.out.println(Arrays.toString(lib.getDifficulties()));
	}

}
