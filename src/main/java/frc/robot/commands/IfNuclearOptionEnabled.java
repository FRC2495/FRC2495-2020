package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

import frc.robot.Robot;
import frc.robot.ControllerBase.JoystickButtons;

/**
 *
 */
public class IfNuclearOptionEnabled extends ConditionalCommand {

	public IfNuclearOptionEnabled(Command commandIfEnabled, Command commandIfNotEnabled) {
		super(commandIfEnabled, commandIfNotEnabled);
	}

	@Override
	protected boolean condition() {
		return Robot.oi.getRightJoystick().getRawButton(JoystickButtons.BTN11);
	}
}