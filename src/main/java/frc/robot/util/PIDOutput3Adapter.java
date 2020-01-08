package frc.robot.util;
import  edu.wpi.first.wpilibj.*;

/**
 * This class allows to use a third PIDController
 */
public class PIDOutput3Adapter implements PIDOutput {

	private PIDOutput3 m_pidOutput;

	public PIDOutput3Adapter(PIDOutput3 output) {
		m_pidOutput = output;
	}

	public void pidWrite(double output) {
		m_pidOutput.pidWrite3(output);
	}

}
