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


public class StartingPositionTwoToOpponentTrench extends CommandGroup {
	/**
	 * Add your docs here.
	 */
    final int TURN_DIRECTION = -1;
        //Left is equal to -1
        //Right is equal to +1

	public StartingPositionTwoToOpponentTrench() {
    
        addSequential(new DrivetrainTurnAngleUsingPidController(-TURN_DIRECTION * AutonConstants.ANGLE_FROM_STARTING_POINT_TWO_TO_DROP_ZONE));
        //To Turning from starting point 2 to Drop Zone 

        addSequential(new DrivetrainMoveDistanceWithStallDetection(AutonConstants.DISTANCE_FROM_STARTING_POINT_TWO_TO_DROP_ZONE));
        //To move from the starting point to the Drop Zone

        addSequential(new HingeMoveMidway());

        addSequential(new GrasperTimedRelease(2));

        addParallel(new GrasperTimedRelease(2));
        //Continues releasing as we pull back

        addSequential(new DrivetrainMoveDistance(-24));

        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * (180 - AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_OPPONENT_TRENCH)));
        //To Turning from the Drop Zone to the Opponent Trench

        addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_OPPONENT_TRENCH_TO_DROP_ZONE));
        //To Move from the Drop Zone to the Opponent Trench
    
        addParallel(new HingeMoveDown());
        //Moves Intake to Intake Position

        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_OPPONENT_TRENCH));
        // Turn to allign with the Opponent Trench 

        addParallel(new GrasperTimedGrasp(5));
        //Starts Intake - will stop after 5 secs or explicit stop, whichever comes first

        addSequential(new DrivetrainMoveDistance (AutonConstants.DISTANCE_FROM_OPPONENT_TRENCH_TO_OPPONENT_TRENCH_END));
        //Moving foward, all the way to the end of the Opponent Trench  

        addSequential(new DrivetrainMoveDistance (- AutonConstants.DISTANCE_FROM_OPPONENT_TRENCH_TO_OPPONENT_TRENCH_END));
        //Move backwards all through the Opponent Trench (instead of turning) 

        addSequential(new GrasperStop());
        //Stops Intake

        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * (180 - AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_OPPONENT_TRENCH)));
        //To Turning to the Drop Zone from the Opponent Trench

        addParallel(new HingeMoveMidway());
        //Moves Intake to Scoring Position

        addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_OPPONENT_TRENCH_TO_DROP_ZONE));
        //To Move from the Opponent Trench to the Drop Zone 

        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_OPPONENT_TRENCH ));
        //Angling towards the Drop Zone 

        addSequential(new DrivetrainMoveUsingCameraPidController(18));

        addSequential(new GrasperTimedRelease(2));
        //Drops the Power Cells

    }
}