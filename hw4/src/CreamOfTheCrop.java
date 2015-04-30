import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class CreamOfTheCrop {

	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<Integer> badLines = new ArrayList<Integer>();
		for (int i = 801; i < 1000; i++) {
			badLines.add(i);
		}
		for (int i = 1; i < 301; i++) {
			badLines.add(i);
		}
		Scanner s = new Scanner(new File("census.train"));
		int lineNum = 1;
		PrintWriter pw = new PrintWriter("modified_census.train");
		PrintWriter pwbad = new PrintWriter("excluded_data");
		while (s.hasNextLine()) {
			String line = s.nextLine();
			//System.out.println(line);
			if (!badLines.contains(lineNum)) {
				//System.out.println("here");
				pw.println(line);
			} else {
				pwbad.println(line);
			}
			lineNum++;
		}
		s.close();
		pw.close();
		pwbad.close();
	}

}
