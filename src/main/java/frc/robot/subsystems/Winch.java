/**
 * 
 */
package frc.robot.subsystems;

import java.util.Timer;
import java.util.TimerTask;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;

import frc.robot.interfaces.*;
//import frc.robot.Ports;
import frc.robot.Robot;

/**
 * @author Christian 
 * 
 */
public class Winch extends Subsystem implements IWinch {

	/**
	 * 
	 */
	static final double MAX_PCT_OUTPUT = 1.0;
	static final int WAIT_MS = 1000;
	static final int TIMEOUT_MS = 5000;

	static final int TALON_TIMEOUT_MS = 10;

	static final int PRIMARY_PID_LOOP = 0;
	
	protected static final long WINCH_STOP_DELAY_MS = 500; // TODO tune

	BaseMotorController winch; 
	BaseMotorController winch_follower;
	
	boolean isWinchingUp;
	boolean isWinchingDown;
	
	Robot robot;
	
	
	public Winch(BaseMotorController winch_in, BaseMotorController winch_follower_in, Robot robot_in) {
		
		winch = winch_in;
		winch_follower = winch_follower_in;
				
		robot = robot_in;

		winch.configFactoryDefault();
		winch_follower.configFactoryDefault();
		
		// Mode of operation during Neutral output may be set by using the setNeutralMode() function.
		// As of right now, there are two options when setting the neutral mode of a motor controller,
		// brake and coast.
		winch.setNeutralMode(NeutralMode.Brake);
		winch_follower.setNeutralMode(NeutralMode.Brake);
				
		// Motor controller output direction can be set by calling the setInverted() function as seen below.
		// Note: Regardless of invert value, the LEDs will blink green when positive output is requested (by robot code or firmware closed loop).
		// Only the motor leads are inverted. This feature ensures that sensor phase and limit switches will properly match the LED pattern
		// (when LEDs are green => forward limit switch and soft limits are being checked).
		//this might me wrong =j
		winch.setInverted(true);
		winch_follower.setInverted(true);
		
		// Both the Talon SRX and Victor SPX have a follower feature that allows the motor controllers to mimic another motor controller's output.
		// Users will still need to set the motor controller's direction, and neutral mode.
		// The method follow() allows users to create a motor controller follower of not only the same model, but also other models
		// , talon to talon, victor to victor, talon to victor, and victor to talon.
		winch_follower.follow(winch);
		
		// set peak output to max in case if had been reduced previously
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT);

		// Sensors for motor controllers provide feedback about the position, velocity, and acceleration
		// of the system using that motor controller.
		// Note: With Phoenix framework, position units are in the natural units of the sensor.
		// This ensures the best resolution possible when performing closed-loops in firmware.
		// CTRE Magnetic Encoder (relative/quadrature) =  4096 units per rotation		
		winch.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,	PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
	}
	
	@Override
	public void initDefaultCommand() {
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
	}

	@Override
	public void periodic() {
		// Put code here to be run every loop

	}
	
	public synchronized void winchUp() {
		winch.set(ControlMode.PercentOutput, MAX_PCT_OUTPUT);
						
		isWinchingUp = true;		
	}
	
	public synchronized void winchDown() {
		winch.set(ControlMode.PercentOutput, -MAX_PCT_OUTPUT);
		
		isWinchingDown = true;
	}

	public double getEncoderPosition() {
		return winch.getSelectedSensorPosition(PRIMARY_PID_LOOP);
	}
	
	public synchronized void winchUpAndStop() {
		winchUp();
		
		Timer timer = new Timer();
		timer.schedule(new WinchStopTask(this), WINCH_STOP_DELAY_MS);
	}
	
	public synchronized void winchDownAndStop() {
		winchDown();
		
		Timer timer = new Timer();
		timer.schedule(new WinchStopTask(this), WINCH_STOP_DELAY_MS);
	}
	
	public synchronized void stop() {
		winch.set(ControlMode.PercentOutput, 0);
		
		isWinchingUp = false;
		isWinchingDown = false;
	}
	
	
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput)
	{
		winch.configPeakOutputForward(peakOutput, TALON_TIMEOUT_MS);
		winch.configPeakOutputReverse(-peakOutput, TALON_TIMEOUT_MS);
		
		winch.configNominalOutputForward(0, TALON_TIMEOUT_MS);
		winch.configNominalOutputReverse(0, TALON_TIMEOUT_MS);
	}
	
	public synchronized boolean isWinchingUp() {
		return isWinchingUp;
	}
	
	public synchronized boolean isWinchingDown(){
		return isWinchingDown;
	}

	// for debug purpose only
	public void joystickControl(Joystick joystick)
	{
		winch.set(ControlMode.PercentOutput, joystick.getY());
	}
	
	private class WinchStopTask extends TimerTask {
		Winch winch;

		public WinchStopTask(Winch winch_in) {
			this.winch = winch_in;
		}

		@Override
		public void run() {
			winch.stop();
		}
	}	

}
