package DataSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

public class FileHandler {

	public void writeToFile(String filePath, StringBuilder sb){
		try(PrintStream stream = new PrintStream(new FileOutputStream(filePath, false))) {
			stream.println(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
