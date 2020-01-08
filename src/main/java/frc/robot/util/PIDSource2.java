package frc.robot.util;
import  edu.wpi.first.wpilibj.*;

/**
 * This interface allows for PIDControllerAdapter to automatically read from this object.
 */
public interface PIDSource2 {
	/**
	 * Set which parameter of the device you are using as a process control variable.
	 *
	 * @param pidSource An enum to select the parameter.
	 */
	void setPIDSource2Type(PIDSourceType pidSource);

	/**
	 * Get which parameter of the device you are using as a process control variable.
	 *
	 * @return the currently selected PID source parameter
	 */
	PIDSourceType getPIDSource2Type();

	/**
	 * Get the result to use in PIDController.
	 *
	 * @return the result to use in PIDController
	 */
	double pidGet2();
}
