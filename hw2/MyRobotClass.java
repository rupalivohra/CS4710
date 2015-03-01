import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import world.Robot;
import world.World;

public class MyRobotClass extends Robot {
	private String[][] map;
	private static HashMap<Node, ArrayList<Node>> adjList;
	private static int destx;
	private static int desty;
	private PriorityQueue<Node> q;
	private static int startx;
	private static int starty;

	public MyRobotClass(int r, int c, Point start, Point dest) {
		/* int r = numRows of map
		 * int c = numCols of map
		 * Point start = start location
		 * Point dest = destination location
		 */
		map = new String[r][c];
		adjList = new HashMap<Node, ArrayList<Node>>();
		startx = start.x;
		starty = start.y;
		destx = dest.x;
		desty = dest.y;
		Comparator<Node> comparator = new NodeCostComparator();
		q = new PriorityQueue<Node>(r*c,comparator);
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
		/*calculates Chebyshev distance from specified location to destination*/
		return Math.max(Math.abs(destx-x1), Math.abs(desty-y1));
	}
	
	public static void addToValues(Node key, Node[] toAdd) {
		for (int i = 0; i < toAdd.length; i++) {
			if (!adjList.get(key).contains(toAdd[i])) {
				adjList.get(key).add(toAdd[i]);
			}
		}
	}

	@Override
	public void travelToDestination() {

		for (int i = 0; i < this.getNumRows(); i++) {
			for (int j = 0; j < this.getNumCols(); j++) {
				Point t = new Point(i, j);
				this.setMapIndex(i, j, this.pingMap(t));
				if(!map[i][j].equals("X")) {
					Node n = new Node(i,j,calcChebyshev(i, j));
					if (!adjList.containsKey(n)) { //if the node has not been processed
						ArrayList<Node> arr = new ArrayList<Node>();
						adjList.put(n,arr);
					} 
					//add adjacent nodes to hashmap
					if (i > 0 && i < this.getNumRows() - 1 && j > 0 && j < this.getNumCols() - 1) { //middle
						Node tl = new Node(calcChebyshev(i-1, j-1));
						Node top = new Node(calcChebyshev(i-1, j));
						Node tr = new Node(calcChebyshev(i-1, j+1));
						Node l = new Node(calcChebyshev(i, j-1));
						Node r = new Node(calcChebyshev(i, j+1));
						Node bl = new Node(calcChebyshev(i+1, j-1));
						Node bot = new Node(calcChebyshev(i+1, j));
						Node br = new Node(calcChebyshev(i+1, j+1));
						Node[] tempArr = {tl, top, tr, l, r, bl, bot, br};
						addToValues(n, tempArr);
					} else if (i == 0) { //first row
						if (j > 0 && j < this.getNumCols() - 1) { //not corner
							Node l = new Node(calcChebyshev(i, j-1));
							Node r = new Node(calcChebyshev(i, j+1));
							Node bl = new Node(calcChebyshev(i+1, j-1));
							Node bot = new Node(calcChebyshev(i+1, j));
							Node br = new Node(calcChebyshev(i+1, j+1));
							Node[] tempArr = {l, r, bl, bot, br};
							addToValues(n, tempArr);
						} else if (j == 0) { //top left corner
							Node r = new Node(calcChebyshev(i, j+1));
							Node bot = new Node(calcChebyshev(i+1, j));
							Node br = new Node(calcChebyshev(i+1, j+1));
							Node[] tempArr = {r, bot, br};
							addToValues(n, tempArr);
						} else { //top right corner
							Node l = new Node(calcChebyshev(i, j-1));
							Node bl = new Node(calcChebyshev(i+1, j-1));
							Node bot = new Node(calcChebyshev(i+1, j));
							Node[] tempArr = {l, bl, bot};
							addToValues(n, tempArr);
						}
					} else if (i == this.getNumRows()-1) { //last row
						if (j > 0 && j < this.getNumCols() - 1) { //not corner
							Node tl = new Node(calcChebyshev(i-1, j-1));
							Node top = new Node(calcChebyshev(i-1, j));
							Node tr = new Node(calcChebyshev(i-1, j+1));
							Node l = new Node(calcChebyshev(i, j-1));
							Node r = new Node(calcChebyshev(i, j+1));
							Node[] tempArr = {tl, top, tr, l, r};
							addToValues(n, tempArr);
						} else if (j == 0) { //bottom left corner
							Node top = new Node(calcChebyshev(i-1, j));
							Node tr = new Node(calcChebyshev(i-1, j+1));
							Node r = new Node(calcChebyshev(i, j+1));
							Node[] tempArr = {top, tr, r};
							addToValues(n, tempArr);
						} else { //bottom right corner
							Node tl = new Node(calcChebyshev(i-1, j-1));
							Node top = new Node(calcChebyshev(i-1, j));
							Node l = new Node(calcChebyshev(i, j-1));
							Node[] tempArr = {tl, top, l};
							addToValues(n, tempArr);
						}
					} else if (j == 0) { //first column
						if (i != this.getNumRows()-1) { //not bottom left corner
							Node top = new Node(calcChebyshev(i-1, j));
							Node tr = new Node(calcChebyshev(i-1, j+1));
							Node r = new Node(calcChebyshev(i, j+1));
							Node bot = new Node(calcChebyshev(i+1, j));
							Node br = new Node(calcChebyshev(i+1, j+1));
							Node[] tempArr = {top, tr, r, bot, br};
							addToValues(n, tempArr);
						} else { //bottom left corner
							Node top = new Node(calcChebyshev(i-1, j));
							Node tr = new Node(calcChebyshev(i-1, j+1));
							Node r = new Node(calcChebyshev(i, j+1));
							Node[] tempArr = {top, tr, r};
							addToValues(n, tempArr);
						}
					} else { //right column
						if (i != this.getNumRows()-1) { //not bottom right corner
							Node tl = new Node(calcChebyshev(i-1, j-1));
							Node top = new Node(calcChebyshev(i-1, j));
							Node l = new Node(calcChebyshev(i, j-1));
							Node bl = new Node(calcChebyshev(i+1, j-1));
							Node bot = new Node(calcChebyshev(i+1, j));
							Node[] tempArr = {tl, top, l, bl, bot};
							addToValues(n, tempArr);
						} else { //bottom right corner
							Node tl = new Node(calcChebyshev(i-1, j-1));
							Node top = new Node(calcChebyshev(i-1, j));
							Node l = new Node(calcChebyshev(i, j-1));
							Node[] tempArr = {tl, top, l};
							addToValues(n, tempArr);
						}
					}
						
				}
			}
		}
		for (int i = 0; i < this.getNumRows(); i++) {
			for (int j = 0; j < this.getNumCols(); j++) {
				System.out.print(this.getMap(i, j));
			}
			System.out.println();
		}

		// heuristic 1: calculate distance from current to dest by going
		// diagonal to reach same x, and then horizontally. STOP moving
		// horizontally if reach same y, and then just move closer in
		// y-direction.
		
		
		

		/* You can call pingMap if you want to see a part of the map */
		super.pingMap(new Point(5, 3));

		/* You can call move to move your robot to a new location */
		super.move(new Point(3, 7));
	}

	public static void main(String[] args) {
		try {
			/*
			 * Create a world. Pass the input filename first. Second parameter
			 * is whether or not the world is uncertain.
			 */
			World myWorld = new World("150x24.txt", false);

			/* Create a robot that will run around in the World */
			MyRobotClass myRobot = new MyRobotClass(myWorld.numRows(),
					myWorld.numCols(),myWorld.getStartPos(),myWorld.getEndPos());
			myRobot.addToWorld(myWorld);

			/* Tell the robot to travel to the destination */
			myRobot.travelToDestination();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
