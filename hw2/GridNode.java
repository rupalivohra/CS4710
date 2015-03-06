import java.awt.Point;
import java.util.ArrayList;


public class GridNode {
	//a grid consisting of nodes
	private ArrayList<Node> components;
	private boolean isStart;
	private boolean isFinish;
	private int size; //numNodes (if all were O's)
	private Point relativeLoc;
	private double distanceToFinishWeight;
	private double samplingWeight;

	
	public GridNode (Point p) {
		this.relativeLoc = p;
		this.size = 0;
		this.components = new ArrayList<Node>();
		this.isStart = false;
		this.isFinish = false;
		this.distanceToFinishWeight = 0;
		this.samplingWeight = 0;
	}
	
	public void addNode(Node n) {
		this.components.add(n);
		this.size++;
	}
	@Override
	public String toString() {
		return "GridNode at [" + relativeLoc.x + "," + relativeLoc.y + "], finishWeight: " + distanceToFinishWeight + ", "+ components + "\n";
	}

	public double getDistanceToFinishWeight() {
		return distanceToFinishWeight;
	}
	public void setDistanceToFinishWeight(double distanceToFinishWeight) {
		this.distanceToFinishWeight = distanceToFinishWeight;
	}
	public double getSamplingWeight() {
		return samplingWeight;
	}
	public void setSamplingWeight(double samplingWeight) {
		this.samplingWeight = samplingWeight;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((relativeLoc == null) ? 0 : relativeLoc.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GridNode other = (GridNode) obj;
		if (relativeLoc == null) {
			if (other.relativeLoc != null)
				return false;
		} else if (!relativeLoc.equals(other.relativeLoc))
			return false;
		return true;
	}
	public Point getRelativeLoc() {
		return relativeLoc;
	}
	public void setRelativeLoc(Point relativeLoc) {
		this.relativeLoc = relativeLoc;
	}
	public ArrayList<Node> getComponents() {
		return components;
	}
	public void setComponents(ArrayList<Node> components) {
		this.components = components;
	}
	public boolean isStart() {
		return isStart;
	}
	public void setStart(boolean isStart) {
		//set = true if Start node in here
		this.isStart = isStart;
	}
	public boolean isFinish() {
		//set = true if Finish node in here
		return isFinish;
	}
	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public static int calcChebyshev(GridNode n1, GridNode n2) {
		/* calculates Chebyshev distance from specified location to destination */
		return Math.max(Math.abs(n1.relativeLoc.x - n2.relativeLoc.x), Math.abs(n1.relativeLoc.y - n2.relativeLoc.y));
	}
	
	public boolean isNeighbor(GridNode g) {
		if (calcChebyshev(this,g) == 1) {
			return true;
		}
		return false;
	}

}
