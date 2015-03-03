import world.World;

public class PathFinding {

	public static void main(String[] args) {
		try {
			boolean uncertainty = true;
			
			World myWorld = new World("input2.txt", uncertainty);
			CustomRobot robot = new CustomRobot(myWorld, uncertainty);
			robot.addToWorld(myWorld);
			robot.travelToDestination();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
