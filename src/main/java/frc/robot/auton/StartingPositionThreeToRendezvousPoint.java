package frc.robot.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.*;
import frc.robot.auton.AutonConstants;


public class StartingPositionThreeToRendezvousPoint extends CommandGroup {
    /**
     * Add your docs here.
     */
    final int TURN_DIRECTION = +1;
    // Left is equal to -1
    // Right is equal to +1

    public StartingPositionThreeToRendezvousPoint() {
    
        addSequential(new DrivetrainTurnAngleUsingPidController(-TURN_DIRECTION * AutonConstants.ANGLE_FROM_STARTING_POINT_THREE_TO_DROP_ZONE));
        //To Turning from starting point 3 to Drop Zone 

        addSequential(new DrivetrainMoveDistanceWithStallDetection(AutonConstants.DISTANCE_FROM_STARTING_POINT_THREE_TO_DROP_ZONE));

    }

}