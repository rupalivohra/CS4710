/* Used for A* traversal */

public class Node {
	private int pastCost;
	private int futureCost;
	private Node prevNode;
	private int x;
	private int y;
	
	public Node(int xLoc, int yLoc, int h) {
		this.x = xLoc;
		this.y = yLoc;
		this.pastCost = 0;
		this.futureCost = h;
		this.prevNode = null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Node other = (Node) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public void setPastCost(int x) {
		this.pastCost = x;
	}
	
	public void setFutureCost(int x) {
		this.futureCost = x;
	}
	
	public int getCost() {
		return pastCost + futureCost;
	}
	
	public int getPastCost() {
		return this.pastCost;
	}
	
	public int getFutureCost() {
		return this.futureCost;
	}
	
	public void setPrevNode(Node n) {
		this.prevNode = n;
	}
	
	public Node getPrevNode() {
		return this.prevNode;
	}
}
