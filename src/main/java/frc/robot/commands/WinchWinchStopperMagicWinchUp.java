// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package frc.robot.commands;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.WinchStopper;
import frc.robot.subsystems.WinchLock;

/**
 *
 */
public class WinchWinchStopperMagicWinchUp extends Command {

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
 
	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
	public WinchWinchStopperMagicWinchUp() {
	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
		requires(Robot.winchControl);
		requires(Robot.winchStopperControl);
		requires(Robot.winchLockControl);
		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		System.out.println("WinchWinchStopperMagicWinchUp: initialize");
		Robot.winchLockControl.setPosition(WinchLock.Position.UNLOCKED); // TODO check if this makes sense
		Robot.winchStopperControl.setPosition(WinchStopper.Position.FREE);
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		Robot.winchControl.winchUp();
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		//return Robot.winchControl.getEncoderPosition() < -27100;
		return false;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		System.out.println("WinchWinchStopperMagicWinchUp: end");
		Robot.winchControl.stop();
		Robot.winchStopperControl.setPosition(WinchStopper.Position.STOPPED);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		System.out.println("WinchWinchStopperMagicWinchUp: interrupted");
		end(); // TODO check if this is a good idea
	}
}
