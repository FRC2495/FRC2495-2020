package frc.robot.util;
import  edu.wpi.first.wpilibj.*;

/**
 * This class allows to use a second PIDController
 */
public class PIDSource2Adapter implements PIDSource {

	private PIDSource2 m_pidInput;
	
	public PIDSource2Adapter(PIDSource2 source) {
		m_pidInput = source;
	 }
	
	public void setPIDSourceType(PIDSourceType pidSource) {
		m_pidInput.setPIDSource2Type(pidSource);
	}
	
	public PIDSourceType getPIDSourceType() {
		return m_pidInput.getPIDSource2Type();
	}

	public double pidGet() {
		return m_pidInput.pidGet2();
	}
}
