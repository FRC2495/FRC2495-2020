/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.subsystems.SpinnerRaiser.Position;

public class SpinnerRaiserUp extends InstantCommand {
	public SpinnerRaiserUp() {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.spinnerRaiser); //
	}

	// Called once when the command executes
	@Override
	protected void initialize() {
		System.out.println("SpinnerRaiserUp: initialize");
		Robot.spinnerRaiser.setPosition(Position.UP);
	}

}