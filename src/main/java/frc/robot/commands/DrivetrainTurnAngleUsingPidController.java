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
import frc.robot.subsystems.Gearbox.Gear;
import frc.robot.subsystems.Gearbox;

/**
 *
 */
public class DrivetrainTurnAngleUsingPidController extends Command {

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
	private int m_angle;
	Gear prevSetting;
	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
	public DrivetrainTurnAngleUsingPidController(int angle) {
	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
		m_angle = angle;
		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
		requires(Robot.drivetrain);
		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		System.out.println("DrivetrainTurnAngleUsingPidController: initialize");
		prevSetting = Robot.gearbox.getGear(); //Saves previous gear setting

		Robot.gearbox.setGear(Gearbox.Gear.LOW);
		Robot.drivetrain.turnAngleUsingPidController(m_angle);
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		// nothing
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return !Robot.drivetrain.tripleCheckTurnAngleUsingPidController();
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		System.out.println("DrivetrainTurnAngleUsingPidController: end");
		Robot.drivetrain.stop();
		Robot.gearbox.setGear(prevSetting);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		System.out.println("DrivetrainTurnAngleUsingPidController: interrupted");
		end();
	}
}
