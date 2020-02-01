/**
 * 
 */
package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

import frc.robot.interfaces.*;
import frc.robot.commands.*;
import frc.robot.Robot;
import frc.robot.sensors.Sonar;


/**
 * @author Joshua
 *
 */
public class Spinner extends Subsystem{

	/**
	 * 
	 */
	static final double MAX_PCT_OUTPUT = 1.0;
	static final double ALMOST_MAX_PCT_OUTPUT = 1.0;
	static final double HALF_PCT_OUTPUT = 0.5;
	static final double REDUCED_PCT_OUTPUT = 0.6;
	
	static final int WAIT_MS = 1000;
	static final int TIMEOUT_MS = 5000;

	static final int TALON_TIMEOUT_MS = 10;

	BaseMotorController spinnerMotor; 
	Sonar sonar;
	
	// shared grasp and release settings
	private int onTargetCount; // counter indicating how many times/iterations we were on target
	private final static int ON_TARGET_MINIMUM_COUNT = 25; // number of times/iterations we need to be on target to really be on target
	
	boolean isSpinning;
	
	Robot robot;
	
	
	public Spinner(BaseMotorController spinnerMotor_in, Robot robot_in) {
		
		spinnerMotor = spinnerMotor_in;
		
		robot = robot_in;

		spinnerMotor.configFactoryDefault();
		
		// Mode of operation during Neutral output may be set by using the setNeutralMode() function.
		// As of right now, there are two options when setting the neutral mode of a motor controller,
		// brake and coast.
		spinnerMotor.setNeutralMode(NeutralMode.Brake);
		// Motor controller output direction can be set by calling the setInverted() function as seen below.
		// Note: Regardless of invert value, the LEDs will blink green when positive output is requested (by robot code or firmware closed loop).
		// Only the motor leads are inverted. This feature ensures that sensor phase and limit switches will properly match the LED pattern
		// (when LEDs are green => forward limit switch and soft limits are being checked).
		spinnerMotor.setInverted(false);
		
		// Both the Talon SRX and Victor SPX have a follower feature that allows the motor controllers to mimic another motor controller's output.
		// Users will still need to set the motor controller's direction, and neutral mode.
		// The method follow() allows users to create a motor controller follower of not only the same model, but also other models
		// , talon to talon, victor to victor, talon to victor, and victor to talon.
		
		// set peak output to max in case if had been reduced previously
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT);
	}
	
	@Override
	public void initDefaultCommand() {
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// Set the default command for a subsystem here.
		//setDefaultCommand(new SpinnerStop());
	}

	@Override
	public void periodic() {
		// Put code here to be run every loop

	}

	public void spin() {
		spinnerMotor.set(ControlMode.PercentOutput, REDUCED_PCT_OUTPUT);
		
		isSpinning = true;
		onTargetCount = 0;
    }
	
	public void stop() {
		spinnerMotor.set(ControlMode.PercentOutput, 0);
		
		isSpinning = false;
	}
	

	public void setNominalAndPeakOutputs(double peakOutput)
	{
        spinnerMotor.configPeakOutputForward(peakOutput, TALON_TIMEOUT_MS);
		spinnerMotor.configNominalOutputForward(0, TALON_TIMEOUT_MS);
	}
	
	public boolean isSpinning() {
		return isSpinning;
	}
	
	// for debug purpose only
	public void joystickControl(Joystick joystick)
	{
		spinnerMotor.set(ControlMode.PercentOutput, joystick.getY());
	}
}









