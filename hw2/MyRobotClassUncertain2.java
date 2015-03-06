import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

import world.Robot;
import world.World;

public class MyRobotClassUncertain2 extends Robot {
	private String[][] map;
	private static HashMap<Point, ArrayList<Node>> adjList;
	private static HashMap<Point, Node> nodeMap;
	private static HashMap<Point, String> known;
	private static int destx;
	private static int desty;
	private PriorityQueue<Node> q;
	private static int startx;
	private static int starty;
	private ArrayList<GridNode> partitioned; // stores all of the GridNodes in
												// the graph
	private static ArrayList<Node> moveList; // stores current path
	private double proportionDistanceWeight;
	private double proportionSamplingWeight;
	private int gridSize; // grid is a square; this is one side of that square
	private int numPings; // number of times to ping each point within a grid;
							// must be an odd number
	private int numSamples; // the number of Nodes to sample within each grid

	public MyRobotClassUncertain2(int r, int c, Point start, Point dest) {
		/*
		 * int r = numRows of map int c = numCols of map Point start = start
		 * location Point dest = destination location
		 */
		map = new String[r][c];
		adjList = new HashMap<Point, ArrayList<Node>>();
		nodeMap = new HashMap<Point, Node>();
		known = new HashMap<Point, String>();
		partitioned = new ArrayList<GridNode>();
		startx = start.x;
		starty = start.y;
		destx = dest.x;
		desty = dest.y;
		Comparator<Node> comparator = new NodeCostComparator();
		q = new PriorityQueue<Node>(r * c, comparator);
		proportionDistanceWeight = 0.5;
		proportionSamplingWeight = 0.5;
		gridSize = 7; // 49 squares/box
		numPings = 3; // take best of 3
		numSamples = 15; // lessen to 8, 3, or 1 depending on gridSize
	}

	public void setGridSize(int x) {
		this.gridSize = x;
	}

	public int getGridSize() {
		return this.gridSize;
	}

	public void setNumSamples(int x) {
		this.numSamples = x;
	}

	public int getNumSamples() {
		return this.numSamples;
	}

	public String getMap(int rowIndex, int colIndex) {
		return this.map[rowIndex][colIndex];
	}

	public void setMapIndex(int rowIndex, int colIndex, String s) {
		map[rowIndex][colIndex] = s;
		return;
	}

	public String[][] getMap() {
		return this.map;
	}

	public String[] getCol(int colIndex) {
		return this.map[colIndex];
	}

	public String[] getRow(int rowIndex) {
		String[] ret = new String[map[0].length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = map[rowIndex][i];
		}
		return ret;
	}

	public int getNumCols() {
		return this.map[0].length;
	}

	public int getNumRows() {
		return this.map.length;
	}

	public static int calcChebyshev(int x1, int y1) {
		/* calculates Chebyshev distance from specified location to destination */
		return Math.max(Math.abs(destx - x1), Math.abs(desty - y1));
	}

	public static int calcChebyshev(Point start, Point cur) {
		/* calculates Chebyshev distance from specified location to destination */
		return Math.max(Math.abs(start.x - cur.x), Math.abs(start.y - cur.y));
	}

	public static void addToValues(Node key, Node[] toAdd) {
		for (int i = 0; i < toAdd.length; i++) {
			if (!adjList.get(key).contains(toAdd[i])) {
				adjList.get(key).add(toAdd[i]);
			}
		}
	}

	public boolean inBounds(int i, int j) {
		if (i < 0 || i >= this.map.length) {
			return false;
		}
		if (j < 0 || j >= this.map[i].length) {
			return false;
		}
		return true;
	}

	// TODO: Implement this
	public String poll(Point p) {
		return "";
	}

	@Override
	public void travelToDestination() {
		Point start = new Point(startx, starty);
		Point end = new Point(destx, desty);
		known.put(start, "S");
		nodeMap.put(start,
				new Node(startx, starty, calcChebyshev(startx, starty)));
		nodeMap.put(end, new Node(destx, desty, 0));
		known.put(end, "F");

		for (int i = 0; i < this.getNumRows(); i++) {
			for (int j = 0; j < this.getNumCols(); j++) {
				Point t = new Point(i, j);
				if (known.get(t) != null
						&& (!(known.get(t).equals("S") || known.get(t).equals(
								"F")))) {
					known.put(t, "?");
				}
			}
		}

		// figure out the Grids
		int rows = getNumRows();
		// verify gridSize is at least half of numRows
		if (rows < gridSize * 2) {
			if (rows % 2 == 0) {
				gridSize = rows / 2;
			} else {
				gridSize = rows / 2 + 1;
			}
		}
		int rowMod = rows % gridSize;
		int cols = getNumCols();
		// verify gridSize is at least half of the numCols
		if (cols < gridSize * 2) {
			if (cols % 2 == 0) {
				gridSize = cols / 2;
			} else {
				gridSize = cols / 2 + 1;
			}
		}
		int colMod = cols % gridSize;

		if (gridSize >= 4 && gridSize < 6) {
			numSamples = 8;
		}
		if (gridSize == 2 || gridSize == 3) {
			numSamples = 3;
		}
		if (gridSize == 1) {
			numSamples = 1;
		}

		// put every node in a GridNode & compile the list of GridNodes
		int gridNodeIndexI = 0;
		int gridNodeIndexJ = 0;
		int indexOfSF = -1;
		int indexOfS = -1;
		int indexOfF = -1;
		for (int r = 0; r < this.getNumRows(); r += gridSize) {
			gridNodeIndexI = 0;
			for (int c = 0; c < this.getNumCols(); c += gridSize) {
				GridNode g = new GridNode(new Point(gridNodeIndexI,
						gridNodeIndexJ));
				partitioned.add(g);
				for (int i = r; i < r + gridSize; i++) {
					for (int j = c; j < c + gridSize; j++) {
						if (inBounds(i, j)) {
							Point t = new Point(i, j);
							Node n = new Node(i, j, calcChebyshev(start, t)
									+ calcChebyshev(end, t));
							g.addNode(n);
							if (t.equals(start)) {
								g.setStart(true);
								indexOfS = partitioned.indexOf(g);
								if (g.isFinish()) {
									indexOfSF = partitioned.indexOf(g);
								}
							}
							if (t.equals(end)) {
								g.setFinish(true);
								indexOfF = partitioned.indexOf(g);
								if (g.isStart()) {
									indexOfSF = partitioned.indexOf(g);
								}
							}
						}
					}
				}
				gridNodeIndexI++;
			}
			gridNodeIndexJ++;
		}

		// at this point, every node is in its respective Grid.
		// System.out.println("Grid size: " + gridSize);
		// System.out.println(partitioned);

		// calculate directional weight
		boolean finishNextDoor = false;
		if (indexOfSF == -1) { // start and end aren't in the same grid
			ArrayList<GridNode> neighbors = new ArrayList<GridNode>();
			GridNode containsStart = partitioned.get(indexOfS);
			GridNode containsFinish = partitioned.get(indexOfF);
			for (int i = 0; i < partitioned.size(); i++) {
				if (containsStart.isNeighbor(partitioned.get(i))) {
					neighbors.add(partitioned.get(i));
					if (containsStart.isNeighbor(containsFinish)) {
						finishNextDoor = true;
					}
				}
			}

			HashMap<GridNode, Integer> distances = new HashMap<GridNode, Integer>();
			for (int i = 0; i < neighbors.size(); i++) {
				distances.put(neighbors.get(i), GridNode.calcChebyshev(
						containsFinish, neighbors.get(i)));
			}
			
			// make the Chebyshev distances either 1, 2, or 3
			int min = Collections.min(distances.values());
//			System.out.println(min);
			// NOTE TO SELF: ERROR WHERE IT DOESNT PICK UP CONTAINSFINISH AS
			// CLOSEST NEIGHBOR
			System.out.println("grid size: " + gridSize);
			System.out.println(GridNode.calcChebyshev(containsFinish,
			 containsFinish));
			
			int toSubtract = min - 1;
			int totalCheby = 0;
			for (Map.Entry<GridNode, Integer> entry : distances.entrySet()) {
				entry.setValue(entry.getValue() - toSubtract);
				if (entry.getValue() == 3) {
					entry.setValue(1);
				} else if (entry.getValue() == 1) {
					entry.setValue(3);
				}
				totalCheby += entry.getValue();
			}

			for (Map.Entry<GridNode, Integer> entry : distances.entrySet()) {
				entry.getKey().setDistanceToFinishWeight(
						(double) entry.getValue() / totalCheby);
			}

			// at this point, the weights associated with the distance to finish
			// has been handled

			// TODO: handle sampling & related weight
		} else {
			// TODO: handle S&F within same grid (direct aim?)
		}

		// Push start onto queue
		// q.add(nodeMap.get(new Point(startx, starty)));
		// while queue not empty and not at finish

	}

	public static void main(String[] args) {
		try {
			/*
			 * Create a world. Pass the input filename first. Second parameter
			 * is whether or not the world is uncertain.
			 */
			World myWorld = new World("14x7.txt", false);

			/* Create a robot that will run around in the World */
			MyRobotClassUncertain2 myRobot = new MyRobotClassUncertain2(
					myWorld.numRows(), myWorld.numCols(),
					myWorld.getStartPos(), myWorld.getEndPos());
			myRobot.addToWorld(myWorld);

			/* Tell the robot to travel to the destination */
			myRobot.travelToDestination();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
