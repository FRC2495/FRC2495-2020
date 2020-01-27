/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.robot.Robot;

/**
 * Add your docs here.
 */
public class IndicatorTimedScrollRainbow extends TimedCommand {
	/**
	 * Add your docs here.
	 */
	public IndicatorTimedScrollRainbow(double timeout) {
		super(timeout);

		setRunWhenDisabled(true); // allows running of command when robot is disabled
		
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.indicator);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		System.out.println("IndicatorTimedScrollRainbow: initialize");
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		Robot.indicator.updateRainbow();
	}

	// Called once after timeout
	@Override
	protected void end() {
		System.out.println("IndicatorTimedScrollRainbow: end");
		//Robot.indicator.stop();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		System.out.println("IndicatorTimedScrollRainbow: interrupted");
		end();
	}
}
