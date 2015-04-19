import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class V1 extends Classifier {

	// holds the first line in .names file
	String[] classifications;
	// list of all features in the order they should appear
	ArrayList<String> features;
	// list of potential options for each feature; does not include numeric
	HashMap<String, String[]> featureOptions;
	// maps all options of all non-numeric features to how many times they occur
	// for >50K
	HashMap<String, Integer> frequencyMapGreater;
	// maps all options of discrete features to how many times they occur for
	// <=50K
	HashMap<String, Integer> frequencyMapLess;
	// maps all numeric variables to the values that appear for them. Used for
	// mean/var calcs for >50K
	HashMap<String, ArrayList<Integer>> valueMapGreater;
	// maps all <=50K case numeric vars to the values that appear
	HashMap<String, ArrayList<Integer>> valueMapLess;
	// maps each feature option to the probability of its outcome given Y = 1
	HashMap<String, Double> featureProbGreater;
	// maps each feature option to the prob of its outcome given Y=0
	HashMap<String, Double> featureProbLess;
	// maps each numeric var option to its [mean,variance] for Y = 1
	HashMap<String, Double[]> numericValsGreater;
	// maps each numeric var option to its [mean,variance] for Y = 0
	HashMap<String, Double[]> numericValsLess;

	double pGreater; // P(Y = 1); P(Y=0) = 1-pGreater

	public V1(String namesFilepath) {
		super(namesFilepath);

		// read in file
		this.features = new ArrayList<String>();
		this.featureOptions = new HashMap<String, String[]>();
		this.frequencyMapGreater = new HashMap<String, Integer>();
		this.frequencyMapLess = new HashMap<String, Integer>();
		this.valueMapGreater = new HashMap<String, ArrayList<Integer>>();
		this.valueMapLess = new HashMap<String, ArrayList<Integer>>();
		this.featureProbGreater = new HashMap<String, Double>();
		this.featureProbLess = new HashMap<String, Double>();
		this.numericValsGreater = new HashMap<String, Double[]>();
		this.numericValsLess = new HashMap<String, Double[]>();
		Scanner s;
		try {
			s = new Scanner(new File(namesFilepath));
			if (s.hasNextLine()) {
				classifications = s.nextLine().split("\\s+");
				s.nextLine(); // burn the empty line
			}
			while (s.hasNextLine()) {
				String[] split = s.nextLine().split("\\s+");
				features.add(split[0]);
				String[] noTitle = Arrays.copyOfRange(split, 1, split.length);
				if (!noTitle[0].equals("numeric")) {
					featureOptions.put(split[0], noTitle);
				}
			}
			s.close();

			for (int i = 0; i < features.size(); i++) {
				if (featureOptions.containsKey(features.get(i))) { // discrete
					for (int j = 0; j < featureOptions.get(features.get(i)).length; j++) {
						frequencyMapGreater.put(featureOptions.get(features.get(i))[j], 0);
						frequencyMapLess.put(featureOptions.get(features.get(i))[j], 0);
					}
				} else { // continuous
					valueMapGreater.putIfAbsent(features.get(i), new ArrayList<Integer>());
					valueMapLess.putIfAbsent(features.get(i), new ArrayList<Integer>());
				}
			}
		} catch (FileNotFoundException e) {
			System.err.print("File not found. Exiting.");
			return;
			// e.printStackTrace();
		}
	}

	@Override
	public void train(String trainingDataFilpath) {
		// read in the file
		// add in a 0 for every feature that does not exist
		int numGreater = 0; // tracks how many people have > 50K; y=1 means >50K
		int numLessThan = 0;
		try {
			Scanner s = new Scanner(new File(trainingDataFilpath));
			while (s.hasNextLine()) {
				String[] split = s.nextLine().split(" "); // contains all case
															// data
				if (split.length != features.size() + 1) { // missing at least
															// one feature
					System.out.println(Arrays.toString(split));
				}
				for (int i = 0; i < features.size(); i++) {
					if (featureOptions.containsKey(features.get(i))) { // discrete
																		// var
						if (split[split.length - 1].equals(">50K")) { // y = 1
							frequencyMapGreater.replace(split[i], frequencyMapGreater.get(split[i]) + 1);
						} else { // y = 0
							frequencyMapLess.replace(split[i], frequencyMapLess.get(split[i]) + 1);
						}
					} else { // continuous var
						if (split[split.length - 1].equals(">50K")) { // y = 1
							valueMapGreater.get(features.get(i)).add(Integer.parseInt(split[i]));
						} else {
							valueMapLess.get(features.get(i)).add(Integer.parseInt(split[i]));
						}
					}
				}
				if (split[split.length - 1].equals(">50K")) {
					++numGreater;
				} else {
					++numLessThan;
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			System.err.print("File not found. Exiting.");
			return;
			// e.printStackTrace();
		}

		// calculate mean and standard deviations for all relevant numeric sets
		// probability that outcome is >50K
		pGreater = ((double) numGreater / (double) (numGreater + numLessThan));
		// calculate probabilities for all variables
		// discrete:
		for (String key : frequencyMapGreater.keySet()) { // >50K
			featureProbGreater.putIfAbsent(key, ((double) frequencyMapGreater.get(key) / (double) numGreater));
		}
		for (String key : frequencyMapLess.keySet()) { // <=50K
			featureProbLess.putIfAbsent(key, ((double) frequencyMapLess.get(key) / (double) numLessThan));
		}
		// continuous:
		for (String key : valueMapGreater.keySet()) { // >50K
			double mean = calcMean(valueMapGreater.get(key));
			double var = calcVar(valueMapGreater.get(key), mean);
			Double[] arr = { mean, var };
			numericValsGreater.put(key, arr);
		}
		for (String key : valueMapGreater.keySet()) { // <= 50K
			double mean = calcMean(valueMapLess.get(key));
			double var = calcVar(valueMapLess.get(key), mean);
			Double[] arr = { mean, var };
			numericValsLess.put(key, arr);
		}
	}

	@Override
	public void makePredictions(String testDataFilepath) {
		try {
			Scanner s = new Scanner(new File(testDataFilepath));
			while (s.hasNextLine()) {
				//one case at a time
				String[] split = s.nextLine().split(" ");
				if (split.length != features.size()) {
					// missing at least one feature
					split = fillInMissingVals(split);
				}
				//if ? in array, the value was missing
				double p1 = 1; //P(Y = 1)
				double p0 = 1; //P(Y = 0)
				for (int i = 0; i < split.length; i++) {
					if (!split[i].equals("?")) {
						//handle numeric
						if (numericValsGreater.containsKey(split[i])) {
							double sub = Double.parseDouble(split[i]) - numericValsGreater.get(split[i])[0];
							double exp = -1*Math.pow(sub,2)/(2*numericValsGreater.get(split[i])[1]);
							double numerator = Math.pow(Math.E, exp);
							double den = Math.sqrt(2*Math.PI*numericValsGreater.get(split[i])[1]);
							p1 = p1*(numerator/den);
						}
						if (numericValsLess.containsKey(split[i])) {
							double sub = Double.parseDouble(split[i]) - numericValsLess.get(split[i])[0];
							double exp = -1*Math.pow(sub,2)/(2*numericValsLess.get(split[i])[1]);
							double numerator = Math.pow(Math.E, exp);
							double den = Math.sqrt(2*Math.PI*numericValsLess.get(split[i])[1]);
							p1 = p1*(numerator/den);
						}
						//handle discrete
						if (featureProbGreater.containsKey(split[i])) {
							p1 = p1*featureProbGreater.get(split[i]);
						}
						if (featureProbLess.containsKey(split[i])) {
							p0 = p0*featureProbLess.get(split[i]);
						}
					}
				}
				p1 = p1*pGreater;
				p0 = p0*(1-pGreater);
				
				System.out.println("p1: " + p1 + ", p0: " + p0);
				//print results
				if (p1 > p0) {
					System.out.println(">50K");
				} else if (p1 < p0) {
					System.out.println("<=50K");
				} else { //if equal probabilities, it's a toss-up
					Random r = new Random();
					int i = r.nextInt(2);
					if (i == 0) {
						System.out.println(">50K");
					} else {
						System.out.println("<=50K");
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.err.print("Test file not found. Exiting.");
			return;
			// e.printStackTrace();
		}

	}

	/**
	 * @param an
	 *            ArrayList<Integer> for which the mean should be calculated
	 * @return the mean of the values in the parameter list
	 */
	public double calcMean(ArrayList<Integer> vals) {
		double sum = 0;
		for (int i = 0; i < vals.size(); i++) {
			sum += vals.get(i);
		}
		return (sum / vals.size());
	}

	/**
	 * @param vals
	 *            : The ArrayList<Integer> for which you want the mean
	 *            calculated
	 * @param mean
	 *            : The mean of the ArrayList<Integer>
	 * @return the sample variance of the given ArrayList<Integer>
	 */
	public double calcVar(ArrayList<Integer> vals, double mean) {
		double ss = 0; // sum of squares
		for (int i = 0; i < vals.size(); i++) {
			double diff = vals.get(i) - mean;
			ss += Math.pow(diff, 2);
		}
		return ss / vals.size();
	}

	/**
	 * @param in
	 *            : String[] of original file data
	 * @return String[] with question marks filled in for missing data
	 */
	public String[] fillInMissingVals(String[] in) {
		String[] out = new String[features.size()];
		int offset = 0; // incremented for every missing element replaced with a
						// ?; for indexing in[]
		while (Arrays.asList(out).contains(null)) {
			for (int i = 0; i < (in.length + offset); i++) {
				if (featureOptions.containsKey(features.get(i))) {
					// supposed to be a discrete var
					if (out[i] == null && !Arrays.asList(featureOptions.get(features.get(i))).contains(in[i - offset])) {
						out[i] = "?";
						offset++;
						break; // only one element is handled at a time
					} else {
						if (out[i] == null) {
							out[i] = in[i - offset];
						}
					}
				} else {
					if (out[i] == null) {
						try {
							Integer.parseInt(in[i - offset]);
							out[i] = in[i - offset];
						} catch (NumberFormatException n) { // not a number
							// String s = "?";
							out[i] = "?";
							offset++;
							break; // only one element is handled at a time
						}
					}
				}
				if (i == (in.length + offset - 1) && Arrays.asList(out).contains(null)) {
					// missing element is the last element
					out[i + 1] = "?";
				}
			}
		}
		System.out.println("Input was: " + Arrays.toString(in) + "\nOutput is: " + Arrays.toString(out));
		return out;
	}

	public static void main(String[] args) throws Exception {
		V1 hello = new V1("census.names");
		hello.train("census.train");
		hello.makePredictions("census.test");
	}

}
