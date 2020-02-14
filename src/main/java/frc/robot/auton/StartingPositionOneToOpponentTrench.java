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


public class StartingPositionOneToOpponentTrench extends CommandGroup {
	/**
	 * Add your docs here.
	 */
    final int TURN_DIRECTION = 1;
        //Left is equal to 1
        //Right is equal to -1

	public StartingPositionOneToOpponentTrench() {
    
        


    addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_STARTING_POINT_ONE_TO_DROP_ZONE));
        //To move from the starting point to the Drop Zone

    // TODO AddSequentialScore

    addSequential(new DrivetrainTurnAngleUsingPidController( -TURN_DIRECTION * 180 - ( AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_OPPONENT_TRENCH)));
        //To Turning from the Drop Zone to the Alliance Trench

    addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_OPPONENT_TRENCH_TO_DROP_ZONE));
        //To Move from the Drop Zone to the Alliance Trench

    addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_OPPONENT_TRENCH));
        // Turn to allign with the Alliance Trench 

    addSequential(new DrivetrainMoveDistance (AutonConstants.DISTANCE_FROM_OPPONENT_TRENCH_TO_OPPONENT_TRENCH_END));
        //Moving foward, all the way to the end of the Opponent Trench  

     //TODO AddParallelIntake 

    addSequential(new DrivetrainMoveDistance (- AutonConstants.DISTANCE_FROM_OPPONENT_TRENCH_TO_OPPONENT_TRENCH_END));
     //Move backwards all through the Opponent Trench (instead of turning) 

     //TODO addSequencialStop

    addSequential(new DrivetrainTurnAngleUsingPidController( TURN_DIRECTION * 180 - ( AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_OPPONENT_TRENCH)));
     //To Turning to the Drop Zone from the Opponent Trench

     addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_OPPONENT_TRENCH_TO_DROP_ZONE));
        //To Move from the Opponent Trench to the Drop Zone


    addSequential(new DrivetrainTurnAngleUsingPidController( -TURN_DIRECTION *  AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_OPPONENT_TRENCH ));
        //Angling towards the Drop Zone 

        // TODO AddSequentialScore

    }
}