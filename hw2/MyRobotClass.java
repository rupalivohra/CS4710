import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
	private HashSet<Node> closed;

	public MyRobotClass(int r, int c, Point start, Point dest) {
		/*
		 * int r = numRows of map int c = numCols of map Point start = start
		 * location Point dest = destination location
		 */
		map = new String[r][c];
		adjList = new HashMap<Node, ArrayList<Node>>();
		closed = new HashSet<Node>();
		startx = start.x;
		starty = start.y;
		destx = dest.x;
		desty = dest.y;
		Comparator<Node> comparator = new NodeCostComparator();
		q = new PriorityQueue<Node>(r * c, comparator);
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

	@Override
	public void travelToDestination() {
		// populate graph
		for (int i = 0; i < this.getNumRows(); i++) {
			for (int j = 0; j < this.getNumCols(); j++) {
				Point t = new Point(i, j);
				this.setMapIndex(i, j, this.pingMap(t));
			}
		}
		
	      for (int i = 0; i < this.getNumRows(); i++) {
	            for (int j = 0; j < this.getNumCols(); j++) {
	                if (!map[i][j].equals("X")) {
	                    ArrayList<Node> arr = null;
	                    Node n = new Node(i, j, calcChebyshev(i, j));
	                    if (!adjList.containsKey(n)) { // if the node has not been
	                                                    // processed
	                        arr = new ArrayList<Node>();
	                        adjList.put(n, arr);
	                    }

	                    for (int horz = i - 1; horz < i + 2; horz++) {
	                        for (int vert = j - 1; vert < j + 2; vert++) {
	                            if (inBounds(horz, vert)
	                                    && (i != horz || j != vert)) {
	                                if (!map[horz][vert].equals("X")) {
	                                    arr.add(new Node(horz, vert, calcChebyshev(
	                                            horz, vert)));
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	      }
		
		for (int i = 0; i < this.getNumRows(); i++) {
			for (int j = 0; j < this.getNumCols(); j++) {
				System.out.print(this.getMap(i, j) + " ");
			}
			System.out.println();
		}

		// heuristic 1: calculate distance from current to dest by going
		// diagonal to reach same x, and then horizontally. STOP moving
		// horizontally if reach same y, and then just move closer in
		// y-direction.

		// A*

		Node start = new Node(startx, starty, 0);
		System.out.println("Start: " + start); // prints out [y,x]. I feel like it's a
									// simple fix to just reverse what we store
									// things in, but I could be wrong. Can you
									// check this? -R
		
		/*
		 * Someone asked on Piazza, but it's effectively (row, column) which is more like (y,x)
		 * We could change how we define nodes, or we could change the for loops. That is, call the outer one j and the inner i. Then it maps to x and y correctly. 
		 */
		ArrayList<Node> adj = adjList.get(start);
		q.add(start);
		Node tmp = null;
		
		int dist = 0;

		while (!q.isEmpty()) { //not right, but I wanted to push what I had started
			//closed.add(start);
		    tmp = q.poll();
		    adj = adjList.get(tmp);
		    tmp.setVisited();
		    if(tmp.getX() == destx && tmp.getY() == desty){
		        dist = tmp.getCost();
		        break;
		    }
		    //System.out.println(tmp);
		    //System.out.println(adj);
			for (int i = 0; i < adj.size(); i++) {
			    if(!adj.get(i).getVisited()){
    				adj.get(i).setPastCost(tmp.getPastCost() + 1);
    				adj.get(i).setFutureCost(calcChebyshev(adj.get(i).getX(),adj.get(i).getY()));
    				adj.get(i).setPrevNode(tmp);
    				q.add(adj.get(i));
			    }
				//System.out.println(q);
			}
		}
		System.out.println("Path Distance: " + dist);
		System.out.print("Finish: ");
		while(tmp.getPrevNode() != null){
		    System.out.println(tmp);
		    tmp = tmp.getPrevNode();
		}
	    System.out.println("Start: " + tmp);

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
			World myWorld = new World("bin/8x6.txt", false);

			/* Create a robot that will run around in the World */
			MyRobotClass myRobot = new MyRobotClass(myWorld.numRows(),
					myWorld.numCols(), myWorld.getStartPos(),
					myWorld.getEndPos());
			myRobot.addToWorld(myWorld);

			/* Tell the robot to travel to the destination */
			myRobot.travelToDestination();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
