package frc.robot.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.*;
import frc.robot.auton.AutonConstants;


public class StartingPositionThreeToNowhere extends CommandGroup {
	/**
	 * Add your docs here.
	 */
    final int TURN_DIRECTION = +1;
        //Left is equal to -1
        //Right is equal to +1

	public StartingPositionThreeToNowhere() {
        
        addSequential(new DrivetrainMoveDistanceWithStallDetection(AutonConstants.DISTANCE_FROM_STARTING_POINT_ONE_TO_DROP_ZONE - 24));
        //Moving forward from starting point to 24 inches away from opponent wall get auton points

        addSequential(new HingeMoveMidway());
        // lower hinge to midway

        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * 90));
        //The robot stays in the same starting spot, but turns 90 degrees to the right to face the wall. This sets up the drivers to make their way to the alliance trench.
    }

}