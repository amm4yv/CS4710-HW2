import world.World;

public class PathFinding {

	public static void main(String[] args) {
		try {
			World myWorld = new World("input.txt", false);
			CustomRobot robot = new CustomRobot(myWorld);
			robot.addToWorld(myWorld);
			robot.travelToDestination();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
