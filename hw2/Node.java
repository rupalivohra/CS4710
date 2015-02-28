/* Used for A* traversal */

public class Node {
	private int pastCost;
	private int futureCost;
	
	public Node(int h) {
		this.pastCost = 0;
		h = this.futureCost;
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
}
