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

public class SpinnerRaiserDown extends InstantCommand {

	public SpinnerRaiserDown() { //true is up, false is down
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.spinnerRaiser); // While using this command no other command can use it 
	}

	// Called once when the command executes
	@Override
	protected void initialize() {
		System.out.println("SpinnerRaiserDown: initialize ");
		Robot.spinnerRaiser.setPosition(Position.DOWN); // Making the piston go down
	}

}