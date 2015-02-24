import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

import world.Robot;
import world.World;

public class CustomRobot extends Robot {

	// // ping returns O, X, F or S
	// super.pingMap(new Point(5, 3));
	//
	// // if return from move == super.getPosition(), robot didn't move
	//
	// super.move(new Point(3, 7));

	private World world;
	Point[][] adjMatrix;
	ArrayList<Node> openList;
	ArrayList<Node> closedList;
	Node start;
	Node end;

	public CustomRobot(World world) {
		this.world = world;
		this.openList = new ArrayList<Node>();
		this.closedList = new ArrayList<Node>();

		this.start = new Node(null, world.getStartPos());
		this.end = new Node(null, world.getEndPos());

		openList.add(start);

		// System.out.println(start.point);

	}

	@Override
	public void travelToDestination() {

		Stack<Point> path = getPath();

		if (path != null) {
			path.pop();
			while (!path.isEmpty()) {
				super.move(path.pop());
			}
		}

	}

	public Stack<Point> getPath() {

		while (!openList.isEmpty()) {

			// Find minimum cost node
			double minCost = Double.MAX_VALUE;
			Node minCostNode = end;

			for (Node n : openList) {
				double temp = getGivenCost(n) + getHeuristicCost(n);
				if (temp < minCost) {
					minCost = temp;
					minCostNode = n;
				}
			}

			// Reached end point, so get path taken
			if (minCostNode.point.equals(end.point))
				return reconstructPath(minCostNode);

			// Otherwise, going to keep looking
			openList.remove(minCostNode);
			closedList.add(minCostNode);

			// Loop through neighboring nodes
			for (Node neighbor : neighborNodes(minCostNode)) {
				if (closedList.contains(neighbor))
					continue;

				// double calculatedNeighborCost = getGivenCost(minCostNode)
				// + minCostNode.point.distance(neighbor.point);

				// Neighbor is potential option to be explored in next loop of
				// while
				if (!openList.contains(neighbor)) {
					neighbor.parent = minCostNode;
					openList.add(neighbor);
				}
			}

		}
		return null;
	}

	public ArrayList<Node> neighborNodes(Node curr) {
		ArrayList<Node> adjacent = new ArrayList<Node>();
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				Point adjPoint = new Point(curr.point.x + x, curr.point.y + y);
				Node adjNode = new Node(curr, adjPoint);
				if (!(x == 0 && y == 0) && !closedList.contains(adjNode)) {
					String query = super.pingMap(adjPoint);
					if (query != null
							&& (query.equals("O") || query.equals("F"))) {
						adjacent.add(adjNode);
					}
				}
			}
		}
		return adjacent;
	}

	public Stack<Point> reconstructPath(Node goal) {
		Stack<Point> path = new Stack<Point>();
		path.push(goal.point);

		Node prev = goal.parent;
		while (prev != null) {
			path.push(prev.point);
			prev = prev.parent;
		}

		return path;
	}

	public double getGivenCost(Node n) {
		double cost = 0;
		while (n.parent != null) {
			cost += n.point.distance(n.parent.point);
			n = n.parent;
		}
		return cost;
	}

	public double getHeuristicCost(Node n) {
		return n.point.distance(end.point);
	}

}
