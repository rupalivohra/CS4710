import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class TestCaseGenerator {

	public static void main(String[] args) throws FileNotFoundException {
		V1 hello = new V1("census.names");
		System.out.print("Enter num test cases (1-999): ");
		Scanner s = new Scanner(System.in);
		int numTrain = s.nextInt();
		int numTest = 1499 - numTrain;
		// always split into "thirds"
		int index = numTrain - 1;
		ArrayList<Integer> maxIndices = new ArrayList<Integer>();
		while (index < 1001) {
			maxIndices.add(index);
			index += numTrain;
		}
		if (index - numTrain < 1000) {
			maxIndices.add(1000); // accounts for uneven split
		}
		System.out.print("Select segment to be test cases: 0. [1-" + maxIndices.get(0) + "]");
		for (int i = 1; i < maxIndices.size(); i++) {
			System.out.print(", " + i + ". [" + (maxIndices.get(i - 1) + 1) + "-" + maxIndices.get(i) + "]");
		}
		System.out.print(": ");
		s.close();
		// int sectionTest = s.nextInt();
		int sectionTest;
		double accuracy = 0;
		double max = 0;
		double min = Double.MAX_VALUE;
		for (int loop = 0; loop < maxIndices.size(); loop++) {
			sectionTest = loop;
			int minIndexTest = 0;

			if (sectionTest > 0) {
				minIndexTest = maxIndices.get(sectionTest - 1) + 1;
			}
			if (sectionTest == (maxIndices.size() - 1)) {
				minIndexTest = maxIndices.get(sectionTest) - numTrain; //makes sure last section still has appropriate size
			}
			int maxIndexTest = maxIndices.get(sectionTest);

			// read in census.train
			Scanner f = new Scanner(new File("modified_census.train"));
			String trname = Integer.toString(numTrain) + "_train_part_" + Integer.toString(sectionTest);
			String tsname = Integer.toString(numTrain) + "_test_part_" + Integer.toString(sectionTest);
			String sname = Integer.toString(numTrain) + "_solution_part_" + Integer.toString(sectionTest);
			PrintWriter training = new PrintWriter(trname);
			PrintWriter testing = new PrintWriter(tsname);
			PrintWriter solution = new PrintWriter(sname);
			int position = 0;
			while (f.hasNextLine()) {
				String[] split = f.nextLine().split(" ");
				String toWrite = "";
				if (position >= minIndexTest && position <= maxIndexTest) { // test
																			// data
					for (int i = 0; i < split.length - 1; i++) {
						toWrite += split[i];
						if (i != split.length - 1) {
							toWrite += " ";
						}
					}
					solution.println(split[split.length - 1]);
					testing.println(toWrite);
				} else { // training data
					for (int i = 0; i < split.length; i++) {
						toWrite += split[i];
						if (i != split.length) {
							toWrite += " ";
						}
					}
					// System.out.println(toWrite);
					training.println(toWrite);
				}
				position++;
			}
			Scanner excdat = new Scanner(new File("excluded_data"));
			while (excdat.hasNextLine()) {
				String[] line = excdat.nextLine().split(" ");
				solution.println(line[line.length-1]);
			}
			training.close();
			testing.close();
			solution.close();
			f.close();

			hello.train(trname);
			hello.makePredictions(tsname);

			// compare output
			Scanner sol = new Scanner(new File(sname));
			Scanner mysol = new Scanner(new File("mysol"));

			int numCorr = 0;
			int numTot = 0;
			while (mysol.hasNextLine()) {
				if (sol.hasNextLine()) {
					String s1 = sol.nextLine();
					String s2 = mysol.nextLine();
					if (s1.equals(s2)) {
						numCorr++;
					}
					numTot++;
				}
			}
			sol.close();
			mysol.close();
			double prop = (double) numCorr / (double) numTot;
			accuracy += prop;
			if (prop < min) {
				min = prop;
			}
			if (prop > max) {
				max = prop;
			}
			System.out.println(numTrain + " test set, " + numTest + " training set");
			System.out.println("Predicted " + prop * 100 + "% correct for " + sname);
		}
		System.out.println("Average correct prediction rate: " + (accuracy/(double)maxIndices.size())*100 + "%");
		System.out.println("Max: " + max*100);
		System.out.println("Min: " + min*100);
	}
}
