import com.robotarm.RobotArm;

public class ServerMain {

	public static void main(String[] args) {

		RobotArm arm = new RobotArm(false);
		arm.runState(RobotArm.State.LISTEN_LOCAL);

	}

}
