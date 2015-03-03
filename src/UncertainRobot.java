import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

import world.Robot;
import world.World;

public class UncertainRobot extends Robot {

	private World world;
	Point[][] adjMatrix;
	ArrayList<Node> openList;
	ArrayList<Node> closedList;
	ArrayList<Node> unmovableList;
	Node start;
	Node end;
	Node current;
	boolean uncertainty;

	public UncertainRobot(World world, boolean uncertainty) {
		this.world = world;
		this.uncertainty = uncertainty;

		this.openList = new ArrayList<Node>();
		this.closedList = new ArrayList<Node>();
		this.unmovableList = new ArrayList<Node>();

		this.start = new Node(null, world.getStartPos());
		this.end = new Node(null, world.getEndPos());
		this.current = start;

		openList.add(start);

	}

	@Override
	public void travelToDestination() {

		// if (uncertainty) {
		// move(this.end);
		// }

		// else {
		Stack<Point> path = getPath();

		if (path != null) {
			path.pop();
			while (!path.isEmpty()) {
				super.move(path.pop());
			}
		}
		// }
	}

	public Stack<Point> getPath() {

		while (!openList.isEmpty()) {

//			System.out.println("Open: \t\t" + openList);
//			System.out.println("Closed: \t" + closedList);

			// System.out.println("No: " + unmovableList);

			// Find minimum cost node
			double minCost = Double.MAX_VALUE;
			Node minCostNode = end;

			if (openList.size() > 1) {
				for (Node n : openList) {
					double temp = getGivenCost(n) + getHeuristicCost(n);
					//System.out.println(n + ": " + temp);
					if (!n.point.equals(start.point)
							&& !n.point.equals(super.getPosition())
							&& temp <= minCost) {
						minCost = temp;
						minCostNode = n;
					}
				}
			} else {
				minCostNode = openList.get(0);
			}

			// Reached end point, so get path taken
			if (minCostNode.point.equals(end.point))
				return reconstructPath(minCostNode);

			openList.remove(minCostNode);
			closedList.add(minCostNode);

//			Point currentSpot = super.getPosition();
//			System.out.println("Pos: " + currentSpot);
//			System.out.println("Min: " + minCostNode);

			// We are trying to go along this path, so need to move the robot
			// there, so the moves will work when checking the neighbor nodes of
			// minCostNode
			if (!minCostNode.equals(start) && uncertainty) {
				move(minCostNode);
			}
			
			current = minCostNode;
			
			//System.out.println(super.getPosition());

			ArrayList<Node> neighbors = neighborNodes(minCostNode);
			if (uncertainty)
				neighbors = neighborNodesUncertain(minCostNode);

			//System.out.println(neighbors);

			// Loop through neighboring nodes
			for (Node neighbor : neighbors) {
				if (closedList.contains(neighbor)) {
					continue;
				}

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
				// Neighbor is valid if it is not the current node and if it is
				// not in the closed list
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

	public ArrayList<Node> neighborNodesUncertain(Node curr) {
		ArrayList<Node> adjacent = new ArrayList<Node>();
		
		//double dist = getHeuristicCost(curr) * 3;
		
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				Point adjPoint = new Point(curr.point.x + x, curr.point.y + y);
				Node adjNode = new Node(curr, adjPoint);
				//System.out.println("("+x+","+y+") " + adjNode);
				// Neighbor is valid if it is not the current node and if it is
				// not in the closed list
				if (!(x == 0 && y == 0) && !closedList.contains(adjNode)) {
					//System.out.println("("+x+","+y+") " + adjNode);
					int Os = 0;
					int Xs = 0;
					String query = super.pingMap(adjPoint);
					if (query == null) continue;
					if (query.equals("F")) {
						adjacent.add(adjNode);
						continue;
					}
					//String finalQuery = super.pingMap(end.point);
					//if (!finalQuery.equals("F")){
						for (int i = 0; i < 3; i++) {
							query = super.pingMap(adjPoint);
							if (query.equals("O")) Os++;
							if (query.equals("X")) Xs++;
						}
						if (Os > Xs)
							adjacent.add(adjNode);
					//} 
//					else if (query.equals("O")) {
//						adjacent.add(adjNode);
//					}
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

	// Multiple by constant because more important if this distance is larger
	public double getHeuristicCost(Node n) {
		return n.point.distance(end.point) * 10.5;
	}
	
	
	
	
	
	/**
	 * Move the robot position to the node passed in
	 * 
	 * @param destinationNode
	 *            where the robot needs to move to
	 */
	public void move(Node destinationNode) {

		// Get the robot's current position
		Point current = super.getPosition();
		
		current = super.move(destinationNode.point);

		// Keep making moves until the robot has reached the desired node
		while (!destinationNode.point.equals(current)) {

			//System.out.println("c: " + current);
			//System.out.println("d: " + destinationNode);
			// 0 2, 1 6
			// Case where robot moves diagonally up left
			if ((current.getX() > destinationNode.point.getX())
					&& current.getY() > destinationNode.point.getY()) {
				Point toMove = super.move(new Point((int) current.x - 1,
						(int) current.y - 1));
				if (!toMove.equals(current)) {
					current = toMove;
					continue;
				}
			}
			// Case where robot moves diagonally up right
			if ((current.getX() > destinationNode.point.getX())
					&& current.getY() < destinationNode.point.getY()) {
				Point toMove = super.move(new Point((int) current.x - 1,
						(int) current.y + 1));
				if (!toMove.equals(current)) {
					current = toMove;
					continue;
				}
			}
			// Case where robot moves diagonally down left
			if ((current.getX() < destinationNode.point.getX())
					&& current.getY() > destinationNode.point.getY()) {
				Point toMove = super.move(new Point((int) current.x + 1,
						(int) current.y - 1));
				if (!toMove.equals(current)) {
					current = toMove;
					continue;
				}
			}
			// Case where robot moves diagonally down right
			if ((current.getX() < destinationNode.point.getX())
					&& current.getY() < destinationNode.point.getY()) {
				Point toMove = super.move(new Point((int) current.x + 1,
						(int) current.y + 1));
				if (!toMove.equals(current)) {
					current = toMove;
					continue;
				}
			}

			//System.out.println("c: " + current);

			// Case where robot moves up, may encounter a wall and need to move
			// left or right until successful. Use offset to see if it would
			// be better to go left or right first
			if (current.getX() > destinationNode.point.getX()) {

				int offset = 0;
				if (current.getY() < destinationNode.point.getY())
					offset = 1;
				else if (current.getY() > destinationNode.point.getY())
					offset = -1;

				Point temp = new Point((int) (current.x - 1), (int) (current.y));
				if (super.move(temp).equals(current)) {
					int increment = offset;
					if (offset == 0) {
						increment = 1;
					}
					while (super.move(temp).equals(current)) {
						Point temp2 = new Point(current.x,
								(int) (current.y + increment));
						temp = new Point((int) (current.x - 1),
								(int) (temp2.y + increment));

//						System.out.println("2: " + temp2);
//						System.out.println(temp);
						
						if (super.move(temp2) == null
								|| super.pingMap(temp2) == null) {
							
							temp.y -= increment;
							increment *= -1;
						} else if (!current.equals(super.move(temp2))) {
							current = temp2;
						} else {
							temp.y -= increment;
//							temp = new Point((int) (current.x - 1),
//									(int) (temp.y + (2 * increment)));
						}
					}
				} else {
					current = temp;
				}

			}
			// Case where robot moves down, may encounter a wall and need to
			// move left or right until successful. Use offset to see if it
			// would
			// be better to go left or right first

			else if (current.getX() < destinationNode.point.getX()) {

				int offset = 0;
				if (current.getY() < destinationNode.point.getY())
					offset = 1;
				else if (current.getY() > destinationNode.point.getY())
					offset = -1;

				Point oneDown = new Point((int) current.x + 1, current.y);
				if (super.move(oneDown).equals(current)) {
					int increment = offset;
					if (offset == 0)
						increment = 1;
					while (super.move(oneDown).equals(current)) {
						Point temp2 = new Point(current.x,
								(int) (current.y + increment));
						oneDown = new Point((int) (current.x + 1),
								(int) (temp2.y + increment));
						if (super.move(temp2) == null
								|| super.pingMap(temp2) == null) {
							// System.out.println(temp);
							oneDown.y -= increment;
							increment *= -1;
						} else if (!current.equals(super.move(temp2))) {
							current = temp2;
						} else {
							oneDown.y -= increment;
//							oneDown = new Point((int) (current.x + 1),
//									(int) (oneDown.y + (increment * 2)));
						}
					}
				} else {
					current = oneDown;
				}

			}
			// Case where robot moves left, may encounter a wall and need to
			// move up or down until successful. Use offset to see if it would
			// be better to go up or down first
			if (current.getY() > destinationNode.point.getY()) {

				int offset = 0;
				if (current.getX() < destinationNode.point.getX())
					offset = 1;
				else if (current.getX() > destinationNode.point.getX())
					offset = -1;

				Point temp = new Point((int) (current.x), (int) (current.y - 1));
				if (super.move(temp).equals(current)) {
					int increment = offset;
					if (offset == 0)
						increment = 1;
					while (super.move(temp).equals(current)) {
						Point temp2 = new Point((int) (current.x + increment),
								current.y);
						temp = new Point((int) (temp2.x + increment),
								(int) (current.y - 1));

						//System.out.println(temp);
						
						if (super.move(temp2) == null
								|| super.pingMap(temp2) == null) {
							
							temp.x -= increment;
							increment *= -1;
						} else if (!current.equals(super.move(temp2))) {
							current = temp2;
						} else {
							temp.x -= increment;
//							temp = new Point((int) (temp.x + (2 * increment)),
//									(int) (current.y - 1));
						}
					}
				} else {
					current = temp;
				}

			}
			// Case where robot moves right, may encounter a wall and need to
			// move up or down until successful. Use offset to see if it would
			// be better to go up or down first

			else if (current.getY() < destinationNode.point.getY()) {

				int offset = 0;
				if (current.getX() < destinationNode.point.getX())
					offset = 1;
				else if (current.getX() > destinationNode.point.getX())
					offset = -1;

				Point temp = new Point((int) (current.x), (int) (current.y + 1));
				if (super.move(temp).equals(current)) {
					int increment = offset;
					if (offset == 0)
						increment = 1;
					while (super.move(temp).equals(current)) {
						// System.out.println(current);
						Point temp2 = new Point((int) (current.x + increment),
								current.y);
						// System.out.println(temp2);
						temp = new Point((int) (temp2.x + increment),
								(int) (current.y + 1));
						if (super.move(temp2) == null
								|| super.pingMap(temp2) == null) {
							// System.out.println(temp);
							temp.x -= increment;
							increment *= -1;
							// System.out.println(temp);
						} else if (!current.equals(super.move(temp2))) {
							current = temp2;
						} else {
							temp.x -= increment;
							// temp = new Point((int) (temp.x + (2 *
							// increment)),
							// (int) (current.y + 1));
						}
					}
				} else {
					current = temp;
				}
			}
		}
	}

}
