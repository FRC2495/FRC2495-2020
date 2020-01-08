
package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.ControllerBase;

/**
 *
 */
public class GamepadRumble extends InstantCommand {

	private boolean m_rumble;

	public GamepadRumble(boolean rumble) {

		m_rumble = rumble;

		setRunWhenDisabled(true); // allows running of command when robot is disabled

		// ControllerBase only supports instant commands, so no need to reserve it
	}

	// Called once when this command runs
	@Override
	protected void initialize() {
		
		System.out.println("GamepadRumble: initialize");
		ControllerBase.rumble(m_rumble, Robot.oi.getGamepad());
	}

}
