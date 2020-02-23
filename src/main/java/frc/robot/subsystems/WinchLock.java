package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

import frc.robot.Ports;
import frc.robot.commands.WinchStopperSetStop;


//Activates the piston to shoot the ball into the shooter 
public class WinchLock extends Subsystem {
	
	static final int WAIT_MS = 1000;
	
    DoubleSolenoid lockedNot;
    
    public enum Position {
		LOCKED, // The winch is stopped
		UNLOCKED, // The winch is free
		UNKNOWN;
	}

	public WinchLock() {
		// the double solenoid valve will send compressed air from the tank wherever needed
		lockedNot = new DoubleSolenoid(Ports.CAN.PCM, Ports.PCM.WINCH_LOCK_LOCKED, Ports.PCM.WINCH_LOCK_UNLOCKED); // make sure ports are properly sets in Ports.java	
	}
	
	@Override
	public void initDefaultCommand() {

	}

	@Override
	public void periodic() {
		// Put code here to be run every loop

	}

	public void setPosition(Position pos) //Telling the piston to be in the selected position 
	{
		switch(pos)
		{
			case LOCKED: //Telling the solenoid to have the piston go up
			{
				lockedNot.set(DoubleSolenoid.Value.kReverse); // adjust direction if needed
				break;
			}
			case UNLOCKED: //Telling the solenoid to have the piston go down
			{
				lockedNot.set(DoubleSolenoid.Value.kForward); // adjust direction if needed
				break;
			}
			default:
			{
				// do nothing
			}
		}
	}

	public Position getPosition() //Getting the current gear
	{
		DoubleSolenoid.Value value = lockedNot.get();
		
		switch(value)
		{
			case kReverse: //Checking if the piston is in the kReverse position (high)
			{
				return Position.LOCKED;
			}
			case kForward: //Checking if the piston is in the kFoward position (low)
			{
				return Position.UNLOCKED;
			}
			default: //gear unknown
			{
				return Position.UNKNOWN;
			}
		}
	}
}