package frc.robot.interfaces;

import edu.wpi.first.wpilibj.Joystick;

public interface IGrasper {

	public void grasp();
	
	public void release();
	
	public void stop();
	
	public boolean tripleCheckGraspUsingSonar();
	
	public boolean tripleCheckReleaseUsingSonar();
		
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput);
	
	public boolean isGrasping();
	
	public boolean isReleasing();

	// for debug purpose only
	public void joystickControl(Joystick joystick);
}










