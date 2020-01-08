
package frc.robot.util;

/**
 * This interface allows PIDController to write its results to its output.
 */
public interface PIDOutput2 {
	/**
	 * Set the output to the value calculated by PIDController.
	 *
	 * @param output the value calculated by PIDController
	 */
	void pidWrite2(double output);
}
