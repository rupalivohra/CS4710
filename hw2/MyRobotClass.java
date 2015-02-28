import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import world.Robot;
import world.World;

public class MyRobotClass extends Robot {
	private String[][] map;
	private HashMap<Node, ArrayList<Node>> adjList;
	private int destx;
	private int desty;

	public MyRobotClass(int r, int c, Point dest) {
		map = new String[r][c];
		adjList = new HashMap<Node, ArrayList<Node>>();
		this.destx = dest.x;
		this.desty = dest.y;
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
	
	public static int calcChebyshev(int x1, int y1, int x2, int y2) {
		return Math.max(Math.abs(x2-x1), Math.abs(y2-y1));
	}

	@Override
	public void travelToDestination() {

		for (int i = 0; i < this.getNumRows(); i++) {
			for (int j = 0; j < this.getNumCols(); j++) {
				Point t = new Point(i, j);
				this.setMapIndex(i, j, this.pingMap(t));
				if(!map[i][j].equals("X")) {
					Node n = new Node(calcChebyshev(i, j, destx, desty));
					if (!adjList.containsKey(n)) {
						ArrayList<Node> arr = new ArrayList<Node>();
						adjList.put(n,arr);
					} else {
						//add adjacent nodes to hashmap
						
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
					myWorld.numCols(),myWorld.getEndPos());
			myRobot.addToWorld(myWorld);

			/* Tell the robot to travel to the destination */
			myRobot.travelToDestination();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
