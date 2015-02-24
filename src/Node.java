import java.awt.Point;


public class Node {
	
	Node parent;
	Point point;
	
	public Node(Node parent, Point point){
		this.parent = parent;
		this.point = point;
	}
	
	public String toString() {
		return point.toString();
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null)
        	return false;
        Node node = (Node)obj;
        return (this.point.equals(node.point));
    }

}




/**	public Node move(Node start) {
for (int x = -1; x < 2; x++) {
for (int y = -1; y < 2; y++) {
	Node adj = new Node(start, new Point(start.point.x + x,
			start.point.y + y));
	if (super.pingMap(adj.point) != null
			&& (super.pingMap(adj.point).equals("O") && !(x == 0 && y == 0))) {
		openList.add(adj);
	}
}
}

// System.out.println(openList.toString());

openList.remove(start);
closedList.add(start);

double minCost = Double.MAX_VALUE;
Node nextNode = end;

for (Node n : openList) {
// System.out.println(n.parent.point);
double temp = getGivenCost(n) + getHeuristicCost(n);
//System.out.println(n.point + " " + temp + " " + minCost);
if (temp < minCost) {
	minCost = temp;
	nextNode = n;
}
}

openList.clear();

System.out.println(super.move(nextNode.point));

return nextNode;
}
*/