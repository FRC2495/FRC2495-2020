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


public class StartingPositionTwoToAllianceTrench extends CommandGroup {
	/**
	 * Add your docs here.
	 */
    final int TURN_DIRECTION = 1;
        //Left is equal to 1
        //Right is equal to -1

	public StartingPositionTwoToAllianceTrench() {
    
        addSequential(new DrivetrainTurnAngleUsingPidController(AutonConstants.ANGLE_FROM_STARTING_POINT_TWO_TO_DROP_ZONE));
        //To Turning from starting point 2 to Drop Zone 

    addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_STARTING_POINT_TWO_TO_DROP_ZONE));
        //To move from the starting point to the Drop Zone

        addSequential(new GrasperRelease(), 2000); //TODO TestWaitFunction

        addSequential(new DrivetrainTurnAngleUsingPidController( -TURN_DIRECTION * 180 + ( AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_ALLIANCE_TRENCH)));
            //To Turning from the Drop Zone to the Alliance Trench
    
        addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_ALLIANCE_TRENCH_TO_DROP_ZONE));
            //To Move from the Drop Zone to the Alliance Trench
    
        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_ALLIANCE_TRENCH));
            // Turn to allign with the Alliance Trench 
    
        addParallel(new HingeMoveDown());
            //Moves Intake to Intake Position
    
        addSequential(new DrivetrainMoveDistance (AutonConstants.DISTANCE_FROM_ALLIANCE_TRENCH_TO_ALLIANCE_TRENCH_END));
            //Moving foward, all the way to the end of the Alliance Trench  
    
        addParallel(new GrasperGrasp());
        //Starts Intake
    
        addSequential(new DrivetrainMoveDistance (- AutonConstants.DISTANCE_FROM_ALLIANCE_TRENCH_TO_ALLIANCE_TRENCH_END));
         //Move backwards all through the Alliance Trench (instead of turning) 
    
        addSequential(new GrasperStop());
         //Stops Intake
    
        addSequential(new DrivetrainTurnAngleUsingPidController( -TURN_DIRECTION * 180 + ( AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_ALLIANCE_TRENCH)));
         //To Turning to the Drop Zone from the Alliance Trench
    
         addSequential(new DrivetrainMoveDistance(AutonConstants.DISTANCE_FROM_ALLIANCE_TRENCH_TO_DROP_ZONE));
        //To Move from the Alliance Trench to the Drop Zone
    
        addParallel(new HingeMoveUp());
        //Moves Intake to Scoring Position
    
        addSequential(new DrivetrainTurnAngleUsingPidController( -TURN_DIRECTION *  AutonConstants.ANGLE_BETWEEN_DROP_ZONE_AND_ALLIANCE_TRENCH ));
        //Angling towards the Drop Zone 
    
        addParallel(new GrasperRelease());
        //Drops the Power Cells

    }
}