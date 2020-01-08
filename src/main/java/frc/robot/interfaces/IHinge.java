package frc.robot.interfaces;

import edu.wpi.first.wpilibj.Joystick;

public interface IHinge {

	// returns the state of the limit switch
	public boolean getLimitSwitchState();
	
	// homes the hinge
	// This is done in two steps:
	// step 1: if not already at the switch, we go down slowly until we hit the limit switch.
	// step 2: we go back up a little and mark the position as the virtual/logical zero.
	public void home();
	
	// DO NOT TRY THIS AT HOME
	// This is to fake homing the hinge when we cannot home it for real (e.g. because we have a cube loaded).
	// It might be useful in auton... 
	// And unlike the real home there is no need to wait for this method.
	// THIS ASSUMES THAT THE HINGE IS ALL THE WAY DOWN!
	public void fakeHomeWhenDown();

	// this method need to be called to assess the homing progress
	// (and it takes care of going to step 2 if needed)
	public boolean checkHome();
	
	// This method should be called to assess the progress of a move
	public boolean tripleCheckMove();
	
	public void moveUp();
	
	public void moveMidway();
	
	public void moveDown();

	public double getPosition();

	public double getEncoderPosition();

	public boolean isHoming();

	public boolean isMoving();
	
	public boolean isUp();
	
	public boolean isDown();
	
	public boolean isMidway();

	public void stay();
	
	public void stop();
		
	// for debug purpose only
	public void joystickControl(Joystick joystick);
	
	public double getTarget();
	
	public boolean hasBeenHomed();

}
