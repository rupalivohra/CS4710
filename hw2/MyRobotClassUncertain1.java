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
	                nodeMap.get(super.getPosition()).setVisited();
	                if(super.getPosition().equals(temp_p)){
	                    known.put(temp_p, "O");
	                }else{//If we get to the node or we hit an unexpected barrier, empty queue, add current position and start over
	                    known.put(temp_p, "X");
	                    start_node = nodeMap.get(temp_p);
	                    cur_node = nodeMap.get(temp_p);
	                    q.clear();
	                    q.add(start_node);
	                    break;
	                }
	                if(super.getPosition().equals(new Point(moveList.get(moveList.size() - 1).getX(),moveList.get(moveList.size() - 1).getY()))){
	                    System.out.println("GOT TO TARGET");
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
                                //Add to queue if is O and not in queue and not visited
                                //update cost and prev
                                if(!q.contains(nodeMap.get(temp_point))){
                                    if(!nodeMap.get(temp_point).getVisited()){
                                    nodeMap.get(temp_point).setPastCost(cur_node.getPastCost() + 1);
                                    nodeMap.get(temp_point).setPrevNode(cur_node);
                                    q.add(nodeMap.get(temp_point));
                                    }
                                }else{//If in queue, update if less
                                    if((cur_node.getPastCost() + 1 + calcChebyshev(temp_point.x,temp_point.y)) < nodeMap.get(temp_point).getCost()){
                                        q.remove(nodeMap.get(temp_point));
                                        nodeMap.get(temp_point).setPastCost(cur_node.getPastCost() + 1);
                                        nodeMap.get(temp_point).setPrevNode(cur_node);
                                        q.add(nodeMap.get(temp_point));
                                    }
                                }
                            }

                        }
                    }
                }
	            
	            
	        }

	    }
	    //Check if we ended because we go to final
	    //If so, move to final
	    //If not, print error.
	       if(cur_node.getX() != destx || cur_node.getY() != desty){
	            System.out.println("No path could be found!");
	        }else{
	            //System.out.println("Path Distance: " + dist);
	            //System.out.print("Finish: ");
	            
	            //TODO:The PrevNode values are somehow getting into a loop. This isn't the fault of the code here, it's above. Still, needs a fix
	            while(new Point(cur_node.getPrevNode().getX(),cur_node.getPrevNode().getY()) != super.getPosition()){
	                System.out.println(cur_node);
	                System.out.println(super.getPosition());
	                //System.out.println(tmp);
	                moveList.add(0,cur_node);
	               cur_node = cur_node.getPrevNode();
	            }
	            //System.out.println("Start: " + tmp);
	            
	            for(int i = 0; i < moveList.size(); i++){
	               temp_p = new Point(moveList.get(i).getX(),moveList.get(i).getY());
	               super.move(temp_p);
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
