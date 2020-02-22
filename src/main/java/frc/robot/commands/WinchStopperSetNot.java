/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.subsystems.WinchStopper;

// To set stopped position
public class WinchStopperSetNot extends InstantCommand {
	public WinchStopperSetNot() {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.winchStopperControl); //
	}

	// Called once when the command executes
	@Override
	protected void initialize() {
		System.out.println("WinchStopperSetNot: initialize");
		Robot.winchStopperControl.setPosition(WinchStopper.Position.NOT);
	}

}