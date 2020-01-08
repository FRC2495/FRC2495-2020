package frc.robot.interfaces;

import edu.wpi.first.wpilibj.Joystick;

public interface IDrivetrain {
	
	// this method needs to be paired with checkTurnAngleUsingPidController()
	public void turnAngleUsingPidController(double angle);
	
	public boolean tripleCheckTurnAngleUsingPidController();
	
	// this method needs to be paired with checkMoveDistance()
	public void moveDistance(double dist);
	
	public void moveDistanceHighSpeed(double dist);
	
	public boolean tripleCheckMoveDistance();

	// this method needs to be paired with checkMoveDistance()
	public void moveDistanceAlongArc(int angle);
	
	// checks if drivetrain might be stalled
	public boolean tripleCheckIfStalled();
	
	public void stop();
	
	public void setPIDParameters();
	
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput);
	
	public void joystickControl(Joystick joyLeft, Joystick joyRight, boolean held);
	
	public int getRightEncoderPosition();

	public int getLeftEncoderPosition();

	public int getRightPosition();

	public int getLeftPosition();
	
	public int getRightEncoderVelocity();

	public int getLeftEncoderVelocity();
	
	public boolean isMoving();
	
	public boolean isTurning();
	
	// return if stalled
	public boolean isStalled();
		
	// MAKE SURE THAT YOU ARE NOT IN A CLOSED LOOP CONTROL MODE BEFORE CALLING THIS METHOD.
	// OTHERWISE THIS IS EQUIVALENT TO MOVING TO THE DISTANCE TO THE CURRENT ZERO IN REVERSE! 
	public void resetEncoders();
}


