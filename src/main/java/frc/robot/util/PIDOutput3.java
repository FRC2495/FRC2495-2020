
package frc.robot.util;

/**
 * This interface allows PIDController to write its results to its output.
 */
public interface PIDOutput3 {
	/**
	 * Set the output to the value calculated by PIDController.
	 *
	 * @param output the value calculated by PIDController
	 */
	void pidWrite3(double output);
}
