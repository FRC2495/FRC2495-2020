/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.*;
import frc.robot.auton.AutonConstants;


public class StartingPositionOneToAllianceTrench extends CommandGroup {
	/**
	 * Add your docs here.
	 */
    final int TURN_DIRECTION = +1;
        //Left is equal to -1
        //Right is equal to +1

	public StartingPositionOneToAllianceTrench() {
    
        addSequential(new DrivetrainMoveDistanceWithStallDetection(AutonConstants.DISTANCE_FROM_STARTING_POINT_ONE_TO_DROP_ZONE));
        //Moving from starting point 1 to the drop zone

        addSequential(new HingeMoveMidway());

        addSequential(new GrasperTimedRelease(2));

        addParallel(new GrasperTimedRelease(2));
        //Continues releasing as we pull back

        addSequential(new DrivetrainMoveDistance(-24));
        //Moving backwards after delivering 

        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * 180 - ( AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_ALLIANCE_TRENCH)));
        //Turning from the drop zone to face the alliance trench run

        addParallel(new HingeMoveDown());
        //Moves Intake to Intake Position
        
        addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_ALLIANCE_TRENCH_TO_DROP_ZONE));
        //Moving from the drop zone to the alliance trench run 
        
        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_ALLIANCE_TRENCH));
        // Turning to align with the alliance trench run

        addParallel(new GrasperTimedGrasp(5));
        //Starts Intake - will stop after 5 secs or explicit stop, whichever comes first

        addSequential(new DrivetrainMoveDistance (AutonConstants.DISTANCE_FROM_ALLIANCE_TRENCH_TO_ALLIANCE_TRENCH_BALL_END));
        //Moving from the beginning of the trench run to the end of the last ball's distance on it 

        addSequential(new DrivetrainMoveDistance (TURN_DIRECTION * AutonConstants.DISTANCE_FROM_ALLIANCE_TRENCH_TO_ALLIANCE_TRENCH_BALL_END));
        //Move backwards all through the alliance trench run

        addSequential(new GrasperStop());
        //Stops Intake

        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * 180 + ( AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_ALLIANCE_TRENCH)));
        //Turning from the alliance trench run to the drop zone
        
        addParallel(new HingeMoveUp());
        //Moves Intake to Scoring Position

        addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_ALLIANCE_TRENCH_TO_DROP_ZONE));
        //To Move from the alliance trench to the drop zone

        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION *  AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_ALLIANCE_TRENCH ));
        //Angling towards the drop zone 

        addSequential(new DrivetrainMoveUsingCameraPidController(36));

        addSequential(new GrasperTimedRelease(1));
        //Delivering the power cells
   
    }
}