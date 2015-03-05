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
		maxCheby = 3;
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
	   return "O";
	}

	@Override
	public void travelToDestination() {
	    //System.out.println("DEST: " + destx + "," + desty);
	    known.put(new Point(startx,starty), "S");
	    nodeMap.put(new Point(startx,starty), new Node(startx,starty, calcChebyshev(startx,starty)));
	    //Push start onto queue
	    q.add(nodeMap.get(new Point(startx,starty)));
	    //while queue not empty and not at finish
	    Node cur_node = nodeMap.get(new Point(startx,starty));
	    Node start_node = nodeMap.get(new Point(startx,starty));
	    moveList = new ArrayList<Node>();
	    Point temp_p = null;
	    
	    while(!q.isEmpty()){
	        //Pop off a node
	        cur_node = q.poll();
	        cur_node.setVisited();
	        
	        
	        if((calcChebyshev(new Point(start_node.getX(),start_node.getY()),new Point(cur_node.getX(),cur_node.getY())) > maxCheby) || (cur_node.getX() == destx && cur_node.getY() == desty)){
                //If node = maxCheby + 1 away, move to its prevNode.

	            //System.out.println("COMPARE" + start_node);
	            //System.out.println(start_node);
	            //System.out.println(cur_node);
	            while(!cur_node.equals(start_node)){
	                //System.out.println(cur_node);
	                moveList.add(0,cur_node);
	                cur_node = cur_node.getPrevNode();
	            }
	            
                //As moving, add nodes hit to known set, to save pings
	            for(int i = 0; i < moveList.size(); i++){
	                temp_p = new Point(moveList.get(i).getX(),moveList.get(i).getY());
	                super.move(temp_p);
	                //nodeMap.get(super.getPosition()).setVisited();
	                if(super.getPosition().equals(temp_p)){
	                    known.put(temp_p, "O");
	                }else{//If we get to the node or we hit an unexpected barrier, empty queue, add current position and start over
	                    known.put(temp_p, "X");
	                    start_node = nodeMap.get(super.getPosition());
	                    cur_node = nodeMap.get(super.getPosition());
	                    q.clear();
	                    q.add(start_node);
	                    break;
	                }
	                if(super.getPosition().equals(new Point(moveList.get(moveList.size() - 1).getX(),moveList.get(moveList.size() - 1).getY()))){
	                    //System.out.println("GOT TO TARGET");
	                    start_node = nodeMap.get(super.getPosition());
	                    cur_node = nodeMap.get(super.getPosition());
	                    q.clear();
	                    q.add(start_node);
	                }
	            }
	        }else{
	            //System.out.println(cur_node);
	            //Look for adjacent nodes. Do this by pinging according to poll()
	            //Create new node objects and add them to nodeMap
	            int i = cur_node.getX();
	            int j = cur_node.getY();
	            String temp_str = null;
	            Point temp_point= null;
	            
                for (int horz = i - 1; horz < i + 2; horz++) {
                    for (int vert = j - 1; vert < j + 2; vert++) {
                        if (inBounds(horz, vert)
                                && (i != horz || j != vert)) {
                            temp_point = new Point(horz,vert);
                            if(known.containsKey(temp_point)){
                                temp_str = known.get(temp_point);
                            }else{
                                temp_str = poll(temp_point);
                            }
                            //If temp_str is valid, make a node for it and add it to nodeMap
                            if(!temp_str.equals("X")){
                                if(!nodeMap.containsKey(temp_point)){
                                nodeMap.put(temp_point, new Node(horz,vert,calcChebyshev(horz,vert)));
                                }
                                //System.out.println(temp_point);
                                //Add to queue if is O and not in queue and not visited
                                //update cost and prev
                                if(!q.contains(nodeMap.get(temp_point))){
                                    if(!nodeMap.get(temp_point).getVisited()){
                                    nodeMap.get(temp_point).setPastCost(cur_node.getPastCost() + 1);
                                    nodeMap.get(temp_point).setPrevNode(cur_node);
                                    //System.out.println("Setting prev node for " + temp_point + " to " + cur_node);
                                    q.add(nodeMap.get(temp_point));
                                    }else{
                                        //System.out.println("Already Visited " + temp_point);
                                    }
                                }else{//If in queue, update if less
                                    if((cur_node.getPastCost() + 1 + calcChebyshev(temp_point.x,temp_point.y)) < nodeMap.get(temp_point).getCost()){
                                        q.remove(nodeMap.get(temp_point));
                                        nodeMap.get(temp_point).setPastCost(cur_node.getPastCost() + 1);
                                        nodeMap.get(temp_point).setPrevNode(cur_node);
                                        //System.out.println("Setting prev node for " + temp_point + " to " + cur_node);
                                        q.add(nodeMap.get(temp_point));
                                    }
                                }
                            }

                        }
                    }
                }
	            
	            
	        }

	    }    
	    System.out.println("The algorithm failed to find a path.");
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
