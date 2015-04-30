import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class TestCaseGeneratorOriginal {

	public static void main(String[] args) throws FileNotFoundException {
		V1 hello = new V1("census.names");
		System.out.print("Enter num training cases (1-1498): ");
		Scanner s = new Scanner(System.in);
		int numTrain = s.nextInt();
		int numTest = 1499 - numTrain;
		int index = numTrain - 1;
		ArrayList<Integer> minIndices = new ArrayList<Integer>();
		for (int i = 1; i <= 1499-numTrain; i+=numTrain) {
			minIndices.add(i);
		}
		//add last index
		if (1499%numTrain != 0) {
			minIndices.add(1499-numTrain);
		}
		System.out.println(minIndices);
		s.close();

		double accuracy = 0;
		for (int minIndex : minIndices) {
			int maxIndex = minIndex + numTrain; //max is exclusive
			// read in census.train
			Scanner f = new Scanner(new File("census.train"));
			String trname = Integer.toString(numTrain) + "_train_part_" + Integer.toString(minIndex);
			String tsname = Integer.toString(numTrain) + "_test_part_" + Integer.toString(minIndex);
			String sname = Integer.toString(numTrain) + "_solution_part_" + Integer.toString(minIndex);
			PrintWriter training = new PrintWriter(trname);
			PrintWriter testing = new PrintWriter(tsname);
			PrintWriter solution = new PrintWriter(sname);
			
			int lineNum = 1;
			while (f.hasNextLine()) {
				String line = f.nextLine();
				if (lineNum >= minIndex && lineNum < maxIndex) { //training set
					training.println(line);
				} else { // test
					String[] split = line.split(" ");
					String toWrite = "";
					for (int i = 0; i < split.length-1; i++) {
						toWrite += split[i];
						if (i != split.length) {
							toWrite += " ";
						}
					}
					solution.println(split[split.length - 1]);
					testing.println(toWrite);
				}
				lineNum++;
			}
			f.close();
			training.close();
			testing.close();
			solution.close();
			
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
			
			System.out.println(numTrain + " training set, " + numTest + " test set");
			System.out.println("test section " + minIndices.indexOf(minIndex));
			System.out.println("Classifier accuracy: " + (prop*100) + "%");
		}
		System.out.println("Average classifier accuracy: " + (accuracy/(double)minIndices.size())*100 + "%");

	}

}
