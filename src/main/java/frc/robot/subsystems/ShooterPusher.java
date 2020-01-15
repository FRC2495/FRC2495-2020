package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

import frc.robot.Ports;


// a class to raise the outer/main drivetrain (by lowering the inner/mini drivetrain)
public class ShooterPusher extends Subsystem {
	
	static final int WAIT_MS = 1000;
	
    DoubleSolenoid upDown;
    
    public enum Position {
		UP, // hook is facing up
		DOWN, // hook is facing downward 
		UNKNOWN;
	}

	public ShooterPusher() {
		// the double solenoid valve will send compressed air from the tank wherever needed
		upDown = new DoubleSolenoid(Ports.CAN.PCM, Ports.PCM.HOOK_UP, Ports.PCM.HOOK_DOWN); // make sure ports are properly sets in Ports.java	
	}
	
	@Override
	public void initDefaultCommand() {
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// Set the default command for a subsystem here0
	}

	@Override
	public void periodic() {
		// Put code here to be run every loop

	}

	public void setPosition(Position pos)
	{
		switch(pos)
		{
			case UP:
			{
				upDown.set(DoubleSolenoid.Value.kReverse); // adjust direction if needed
				break;
			}
			case DOWN:
			{
				upDown.set(DoubleSolenoid.Value.kForward); // adjust direction if needed
				break;
			}
			default:
			{
				// do nothing
			}
		}
	}

	public Position getPosition()
	{
		DoubleSolenoid.Value value = upDown.get();
		
		switch(value)
		{
			case kReverse:
			{
				return Position.UP;
			}
			case kForward:
			{
				return Position.DOWN;
			}
			default:
			{
				return Position.UNKNOWN;
			}
		}
	}
}