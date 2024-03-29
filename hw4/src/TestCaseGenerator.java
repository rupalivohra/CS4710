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
		System.out.print("Enter num training cases (0-1000): ");
		Scanner key = new Scanner(System.in);
		int numTrain = key.nextInt();
		int numTest = 1499 - numTrain;
		key.close();
		//rotate the training set inside of modified_census.train
		//test everything that's not the training set, including those that were excluded
		ArrayList<Integer> minIndices = new ArrayList<Integer>(); //contains starting indices of training sets
		for (int i = 1; i <= 1001-numTrain; i+=numTrain) {
			minIndices.add(i);
		}
		//add last index
		if (1000%numTrain != 0) {
			minIndices.add(1001-numTrain);
		}
		
		double accuracy = 0;
		for (int minIndex : minIndices) { //change location of training set
			String trname = Integer.toString(numTrain) + "_train_part_" + Integer.toString(minIndex);
			String tsname = Integer.toString(numTrain) + "_test_part_" + Integer.toString(minIndex);
			String sname = Integer.toString(numTrain) + "_solution_part_" + Integer.toString(minIndex);
			Scanner tset = new Scanner(new File("modified_census.train"));
			PrintWriter training = new PrintWriter(trname);
			PrintWriter testing = new PrintWriter(tsname);
			PrintWriter solution = new PrintWriter(sname);
			
			int maxIndex = minIndex + numTrain;
			System.out.println("Training set: [" + minIndex + "," + maxIndex + ")");
			int lineNum = 1;
			while (tset.hasNextLine()) {
				String line = tset.nextLine();
				if (lineNum >= minIndex && lineNum < maxIndex) {
					training.println(line);
				} else {
					String[] split = line.split(" ");
					String toWrite = "";
					for (int i = 0; i < split.length-1; i++) {
						toWrite += split[i];
					}
					testing.println(toWrite);
					solution.println(split[split.length-1]);
				}
				lineNum++;
			}
			tset.close();
			training.close();
			Scanner excdat = new Scanner(new File("excluded_data"));
			while (excdat.hasNextLine()) {
				String[] line = excdat.nextLine().split(" ");
				String toWrite = "";
				for (int i = 0; i < line.length-1; i++) {
					toWrite += line[i];
					if (i != line.length - 1) {
						toWrite += " ";
					}
				}
				testing.println(toWrite);
				solution.println(line[line.length-1]);
			}
			solution.close();
			testing.close();
			excdat.close();
			
			hello.train(trname);
			hello.makePredictions(tsname);
			
			//compare output
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
			
			double propCorrect = (double) numCorr/(double) numTot;
			accuracy += propCorrect;
			
			System.out.println(numTrain + " training set, " + numTest + " test set");
			System.out.println("test section " + minIndices.indexOf(minIndex));
			System.out.println("Classifier accuracy: " + (propCorrect*100) + "%");
		}
		System.out.println("Average classifier accuracy: " + (accuracy/(double)minIndices.size())*100 + "%");
	}
}
