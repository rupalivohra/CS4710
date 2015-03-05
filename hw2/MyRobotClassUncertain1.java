import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import world.Robot;
import world.World;

public class MyRobotClassUncertain1 extends Robot {
	private String[][] map;
	private static HashMap<Point, ArrayList<Node>> adjList;
	private static HashMap<Point, Node> nodeMap;
	private static HashMap<Point, String> known;
	private static int destx;
	private static int desty;
	private PriorityQueue<Node> q;
	private static int startx;
	private static int starty;
	private HashSet<Node> closed;
	private static int maxCheby;
	private static ArrayList<Node> moveList;

	public MyRobotClassUncertain1(int r, int c, Point start, Point dest) {
		/*
		 * int r = numRows of map int c = numCols of map Point start = start
		 * location Point dest = destination location
		 */
		map = new String[r][c];
		adjList = new HashMap<Point, ArrayList<Node>>();
		nodeMap = new HashMap<Point, Node>();
		known = new HashMap<Point, String>();
		closed = new HashSet<Node>();
		startx = start.x;
		starty = start.y;
		destx = dest.x;
		desty = dest.y;
		Comparator<Node> comparator = new NodeCostComparator();
		q = new PriorityQueue<Node>(r * c, comparator);
		maxCheby = 7;
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
	
	//TODO: Implement this
	public String poll(Point p){
	   return "";
	}

	@Override
	public void travelToDestination() {
	    known.put(new Point(startx,starty), "S");
	    nodeMap.put(new Point(startx,starty), new Node(startx,starty, calcChebyshev(startx,starty)));
	    //Push start onto queue
	    q.add(nodeMap.get(new Point(startx,starty)));
	    //while queue not empty and not at finish
	    Node cur_node = nodeMap.get(new Point(startx,starty));
	    Node start_node = nodeMap.get(new Point(startx,starty));
	    moveList = new ArrayList<Node>();
	    Point temp_p = null;
	    while(!q.isEmpty() && (cur_node.getX() != destx || cur_node.getY() != desty)){
	        //Pop off a node
	        cur_node = q.poll();
	        
	        if(calcChebyshev(new Point(start_node.getX(),start_node.getY()),new Point(cur_node.getX(),cur_node.getY())) > maxCheby){
                //If node = maxCheby + 1 away, move to its prevNode.
	            while(cur_node != start_node){
	                moveList.add(0,cur_node);
	                cur_node = cur_node.getPrevNode();
	            }
	            
                //As moving, add nodes hit to known set, to save pings
	            for(int i = 0; i < moveList.size(); i++){
	                temp_p = new Point(moveList.get(i).getX(),moveList.get(i).getY());
	                super.move(temp_p);
	                if(super.getPosition().equals(temp_p)){
	                    known.put(temp_p, "O");
	                }else{//If we get to the node or we hit an unexpected barrier, empty queue, add current position and start over
	                    start_node = nodeMap.get(temp_p);
	                    cur_node = nodeMap.get(temp_p);
	                    q.clear();
	                    q.add(start_node);
	                }
	            }
	        }else{
	            //Look for adjacent nodes. Do this by pinging according to poll()
	            //Create new node objects and add them to nodeMap
	            int i = cur_node.getX();
	            int j = cur_node.getY();
	            String temp_str = null;
	            
                for (int horz = i - 1; horz < i + 2; horz++) {
                    for (int vert = j - 1; vert < j + 2; vert++) {
                        if (inBounds(horz, vert)
                                && (i != horz || j != vert)) {
                            if(known.containsKey(new Point(horz,vert))){
                                temp_str = known.get(new Point(horz,vert));
                            }else{
                                temp_str = poll(new Point(horz,vert));
                            }
                            //TODO: If temp_str is valid, make a node for it and add it to nodeMap
                            //Add if is O and not in queue and not visited
                            //update cost and prev
                            //If in queue, update if less
                        }
                    }
                }
	            
	            
	        }

	    }
	    System.out.println("DONE");
	}

	public static void main(String[] args) {
		try {
			/*
			 * Create a world. Pass the input filename first. Second parameter
			 * is whether or not the world is uncertain.
			 */
			World myWorld = new World("bin/8x6.txt", false);

			/* Create a robot that will run around in the World */
			MyRobotClassUncertain1 myRobot = new MyRobotClassUncertain1(myWorld.numRows(),
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
