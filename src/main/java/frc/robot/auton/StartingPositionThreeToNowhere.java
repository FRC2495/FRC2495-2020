package frc.robot.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.*;
import frc.robot.auton.AutonConstants;


public class StartingPositionThreeToNowhere extends CommandGroup {
	/**
	 * Add your docs here.
	 */
    final int TURN_DIRECTION = 1;
        //Left is equal to 1
        //Right is equal to -1

	public StartingPositionThreeToNowhere() {
        
        addSequential(new DrivetrainTurnAngleUsingPidController(-TURN_DIRECTION * AutonConstants.ANGLE_FROM_STARTING_POINT_THREE_TO_DROP_ZONE));
        //To Turning from starting point 3 to Drop Zone 

        addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_STARTING_POINT_THREE_TO_DROP_ZONE));
        //To move from the starting point to the Drop Zone

        addSequential(new GrasperRelease(), 2);

        addSequential(new DrivetrainMoveDistance(-24));
        //Move backwards from DZ 

        addSequential(new DrivetrainTurnAngleUsingPidController(-TURN_DIRECTION * 90));
        //Hard Right turn to become parallel to alliance station

        addSequential(new DrivetrainMoveDistance(66));
        //Move along alliance station 5.5' towards TA

        addSequential(new DrivetrainTurnAngleUsingPidController(-TURN_DIRECTION * 90));
        //Hard Right turn to become parallel to alliance station

        addSequential(new DrivetrainMoveDistance(200));
        //Move forward 200" to get close to TA

    }

}