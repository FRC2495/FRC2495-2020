/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.*;

public class WinchWinchStopperJoystickControl extends CommandGroup {
	// It shoots the ball
	public WinchWinchStopperJoystickControl() {
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

		requires(Robot.winchStopperControl); // acquires reference to winchstopper so that its default command is not called prematurely
		
        addSequential(new WinchStopperSetNot());
        addSequential(new WinchJoystickControl());



		// note: the following links are useful to verify that the old command-based framework should allow parallel commands using the same subsystem
		// even if the new command-based framework does not...
		// https://docs.wpilib.org/en/latest/docs/software/old-commandbased/commands/synchronizing-two-commands.html
		// "Any command in the main sequence that requires a subsystem in use by a parallel command will cause the parallel command to be canceled."
		// https://docs.wpilib.org/en/latest/docs/software/commandbased/command-groups.html
		// "a parallel group may not contain multiple commands that require the same subsystem"
	}
}
