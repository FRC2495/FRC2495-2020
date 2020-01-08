/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.sensors;

import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.Ports;

/**
 * Add your docs here.
 */
public class PressureSensor {
	AnalogInput analogPressureSwitch;
	
	public PressureSensor() {
		analogPressureSwitch = new AnalogInput(Ports.Analog.PRESSURE_SENSOR); // TODO: Change port for this
	}

	public double getPressurePSI(){
		return (analogPressureSwitch.getVoltage() * 200.0) / 5.0; // 5 volts corresponds to 200 PSI
	}
	
}
