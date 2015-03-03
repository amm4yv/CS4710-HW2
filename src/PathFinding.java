import world.World;

public class PathFinding {

	public static void main(String[] args) {
		try {
			boolean uncertainty = true;
			boolean probability = false;
			boolean moveRobot = true;
			
			World myWorld = new World("input.txt", uncertainty);
			//CustomRobot robot = new CustomRobot(myWorld, uncertainty);
			//robot.addToWorld(myWorld);
			//robot.travelToDestination();
			
			UncertainRobot ur = new UncertainRobot(myWorld, uncertainty, probability, moveRobot);
			ur.addToWorld(myWorld);
			ur.travelToDestination();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
