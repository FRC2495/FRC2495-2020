package frc.robot.interfaces;


import edu.wpi.first.wpilibj.Joystick;

public interface IWinch {
	
	public void winchUp();
	
	public void winchDown();
	
	public void winchUpAndStop();
	
	public void winchDownAndStop();
	
	public void stop();
	
	
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput);
	
	public boolean isWinchingUp();
	
	public boolean isWinchingDown();

	// for debug purpose only
	public void joystickControl(Joystick joystick);
	
}
