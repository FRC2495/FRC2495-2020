/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.commands.*;

public class CustomAuton extends CommandGroup {

	String startPosition;
	String mainTarget;
	String cameraOption;
	String sonarOption;
	String autonOption;

	/**
     * Add your docs here.
     * 
     * @param startPosition_in starting position
     * @param mainTarget_in    main target
     * @param cameraOption_in  camera option
     * @param sonarOption_in   sonar option
     * @param release_in       release option
     * @param autonOption_in   auton option
     * @return
     */
    public void CargoCustomAuton(String startPosition_in, String mainTarget_in, String cameraOption_in,
            String sonarOption_in, String autonOption_in) {

		startPosition = startPosition_in;
		mainTarget = mainTarget_in;
		cameraOption = cameraOption_in;
		sonarOption = sonarOption_in;
		autonOption = autonOption_in;

		// Add Commands here:
		// e.g. addSequential(new Command1());
		// addSequential(new Command2());
		// these will run in order.

		// To run multiple commands at the same time,
		// use addParallel()
		// e.g. addParallel(new Command1());
		// addSequential(new Command2());
		// Command1 and Command2 will run in parallel.

		// A command group will require all of the subsystems that each member
		// would require.
		// e.g. if Command1 requires chassis, and Command2 requires arm,
		// a CommandGroup containing them would require both the chassis and the
		// arm.

		addSequential(new HingeMoveUp()); // forces hinge back up just in case

		switch (startPosition) {
			case Robot.START_POSITION_1:
				switch (mainTarget) {
					case Robot.MAIN_TARGET_ALLIANCE_TRENCH:
						addSequential(new StartingPositionOneToAllianceTrench());
						break;
					case Robot.MAIN_TARGET_OPPONENT_TRENCH:
						addSequential(new StartingPositionOneToOpponentTrench());
						break;
                    case Robot.MAIN_TARGET_RENDEZVOUS_POINT:
						//addSequential(new StartingPositionOnetoRendevousPoint(autonOption));
						break;
					default:
						// nothing
						break;
				}
				break;

			case Robot.START_POSITION_2:
            switch (mainTarget) {
                case Robot.MAIN_TARGET_ALLIANCE_TRENCH:
                    addSequential(new StartingPositionTwoToAllianceTrench());
                    break;
                case Robot.MAIN_TARGET_OPPONENT_TRENCH:
                    addSequential(new StartingPositionTwoToOpponentTrench());
                    break;
                case Robot.MAIN_TARGET_RENDEZVOUS_POINT:
                    //addSequential(new StartingPositionTwotoRendevousPoint(autonOption));
                    break;
                default:
                    // nothing
                    break;
            }
            break;

				case Robot.START_POSITION_3:
				switch (mainTarget) {
					case Robot.MAIN_TARGET_ALLIANCE_TRENCH:
						addSequential(new StartingPositionThreeToAllianceTrench());
						break;
					case Robot.MAIN_TARGET_OPPONENT_TRENCH:
						addSequential(new StartingPositionThreeToOpponentTrench());
						break;
                    case Robot.MAIN_TARGET_RENDEZVOUS_POINT:
						//addSequential(new StartingPositionThreetoRendevousPoint(autonOption));
						break;
					default:
						// nothing
						break;
				}
				break;
            default:{
                //nothing
                break;
            }
		} // end switch
	}
}