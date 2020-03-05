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
        
        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * 90));
        //Hard Right turn to become parallel to alliance station

        addSequential(new DrivetrainMoveDistance(66));
        //Move along alliance station 5.5' towards TA

        addSequential(new DrivetrainTurnAngleUsingPidController(TURN_DIRECTION * 90));
        //Hard Right turn to become parallel to alliance station

        addSequential(new DrivetrainMoveDistance(200));
        //Move forward 200" to get close to TA*/

        addParallel(new HingeMoveDown());
        //Brings the hinge down so we can start collecting at teleop
   
    }
}