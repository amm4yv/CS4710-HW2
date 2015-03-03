import world.World;

public class PathFinding {

	public static void main(String[] args) {
		try {
			boolean uncertainty = false;
			
			World myWorld = new World("input.txt", uncertainty);
			CustomRobot robot = new CustomRobot(myWorld, uncertainty);
			robot.addToWorld(myWorld);
			robot.travelToDestination();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
