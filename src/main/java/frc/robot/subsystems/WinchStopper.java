package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

import frc.robot.Ports;
import frc.robot.commands.WinchStopperSetStop;


//Activates the piston to shoot the ball into the shooter 
public class WinchStopper extends Subsystem {
	
	static final int WAIT_MS = 1000;
	
    DoubleSolenoid stopNot;
    
    public enum Position {
		STOPPED, // The winch is stopped
		FREE, // The winch is free
		UNKNOWN;
	}

	public WinchStopper() {
		// the double solenoid valve will send compressed air from the tank wherever needed
		stopNot = new DoubleSolenoid(Ports.CAN.PCM, Ports.PCM.WINCH_STOPPED, Ports.PCM.WINCH_NOT); // make sure ports are properly sets in Ports.java	
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
			case STOPPED: //Telling the solenoid to have the piston go up
			{
				stopNot.set(DoubleSolenoid.Value.kReverse); // adjust direction if needed
				break;
			}
			case FREE: //Telling the solenoid to have the piston go down
			{
				stopNot.set(DoubleSolenoid.Value.kForward); // adjust direction if needed
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
		DoubleSolenoid.Value value = stopNot.get();
		
		switch(value)
		{
			case kReverse: //Checking if the piston is in the kReverse position (high)
			{
				return Position.STOPPED;
			}
			case kForward: //Checking if the piston is in the kFoward position (low)
			{
				return Position.FREE;
			}
			default: //gear unknown
			{
				return Position.UNKNOWN;
			}
		}
	}
}