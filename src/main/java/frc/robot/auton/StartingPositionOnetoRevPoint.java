package frc.robot.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.*;
import frc.robot.auton.AutonConstants;


public class StartingPositionOnetoRevPoint extends CommandGroup {
    /**
     * Add your docs here.
     */
    final int TURN_DIRECTION = -1;
    // Left is equal to 1
    // Right is equal to -1

    public StartingPositionOnetoRevPoint() {

        addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_STARTING_POINT_ONE_TO_DROP_ZONE));
        //To move from the starting point to the Drop Zone

    }

}