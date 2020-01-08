package frc.robot.util;
import  edu.wpi.first.wpilibj.*;

/**
 * This class allows to use a second PIDController
 */
public class PIDOutput2Adapter implements PIDOutput {

	private PIDOutput2 m_pidOutput;

	public PIDOutput2Adapter(PIDOutput2 output) {
		m_pidOutput = output;
	}

	public void pidWrite(double output) {
		m_pidOutput.pidWrite2(output);
	}

}
