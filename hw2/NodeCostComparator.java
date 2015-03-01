import java.util.Comparator;

public class NodeCostComparator implements Comparator<Node>{

	@Override
	public int compare(Node n0, Node n1) {
		return n0.getCost() - n1.getCost();
	}

}