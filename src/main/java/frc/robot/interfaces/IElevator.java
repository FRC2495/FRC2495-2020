package frc.robot.interfaces;

import edu.wpi.first.wpilibj.Joystick;

public interface IElevator {

	// returns the state of the limit switch
	public boolean getLimitSwitchState();
	
	// This method should be called to assess the progress of a move
	public boolean tripleCheckMove();
	
	public void moveUp();

	public void moveUpPlus();
	
	public void moveMidway();

	public void moveMidwayPlus();
	
	public void moveDown();

	public void moveDownPlus();

	public double getPosition();

	public double getEncoderPosition();
	
	public boolean isMoving();
	
	public boolean isUp();
	
	public boolean isDown();
	
	public boolean isMidway();

	public void stay();
	
	public void stop();
		
	// for debug purpose only
	public void joystickControl(Joystick joystick);
	
	public double getTarget();
}
