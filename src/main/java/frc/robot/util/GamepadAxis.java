/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.util;

import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.GenericHID;

/**
 * A {@link Button} that gets its state from a {@link GenericHID}.
 */
public class GamepadAxis extends Button {
	private final GenericHID m_joystick;
	private final int m_axisNumber;
	private final boolean m_polarity;
	private final double m_threshold;

	/**
	 * Create a gamepad axis for triggering commands as if it were a button.
	 *
	 * @param joystick     The GenericHID object that has the axis (e.g. Joystick, KinectStick,
	 *                     etc)
	 * @param axisNumber The axis number (see {@link GenericHID#getRawAxis(int) }
	 * 
	 * @param polarity The polarity of the axis (to specify which direction is considered the positive direction)
	 * 
	 * @param threshold The threshold above which the axis shall trigger a command
	 */
	public GamepadAxis(GenericHID joystick, int axisNumber, boolean polarity, double threshold) {
		m_joystick = joystick;
		m_axisNumber = axisNumber;
		m_polarity = polarity;
		m_threshold = threshold;
	}

	public GamepadAxis(GenericHID joystick, int axisNumber, boolean polarity) {
		this(joystick,axisNumber,polarity,0.5);
	}

	public GamepadAxis(GenericHID joystick, int axisNumber) {
		this(joystick,axisNumber,true,0.5);
	}

	/**
	 * Gets the value of the gamepad axis.
	 *
	 * @return The value of the gamepad axis
	 */
	@Override
	public boolean get() {

		double rawAxis = m_joystick.getRawAxis(m_axisNumber);

		if (m_polarity) {
			return (Math.max(rawAxis,0) > m_threshold);				
		} else  {
			return (-Math.min(rawAxis,0) > m_threshold);
		}

	}
}
