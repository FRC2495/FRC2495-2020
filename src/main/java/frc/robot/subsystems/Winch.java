/**
 * 
 */
package frc.robot.subsystems;

import java.util.Timer;
import java.util.TimerTask;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import edu.wpi.first.wpilibj.Joystick;

import frc.robot.interfaces.*;
//import frc.robot.Ports;
import frc.robot.Robot;

/**
 * @author Christian 
 * 
 */
public class Winch implements IWinch {

	/**
	 * 
	 */
	static final double MAX_PCT_OUTPUT = 1.0;
	static final int WAIT_MS = 1000;
	static final int TIMEOUT_MS = 5000;

	static final int TALON_TIMEOUT_MS = 10;
	
	protected static final long WINCH_STOP_DELAY_MS = 500; // TODO tune

	BaseMotorController winch; 
	
	boolean isWinchingUp;
	boolean isWinchingDown;
	
	Robot robot;
	
	
	public Winch(BaseMotorController winch_in, Robot robot_in) {
		
		winch = winch_in;
				
		robot = robot_in;
		
		// Mode of operation during Neutral output may be set by using the setNeutralMode() function.
		// As of right now, there are two options when setting the neutral mode of a motor controller,
		// brake and coast.
		winch.setNeutralMode(NeutralMode.Brake);
				
		// Motor controller output direction can be set by calling the setInverted() function as seen below.
		// Note: Regardless of invert value, the LEDs will blink green when positive output is requested (by robot code or firmware closed loop).
		// Only the motor leads are inverted. This feature ensures that sensor phase and limit switches will properly match the LED pattern
		// (when LEDs are green => forward limit switch and soft limits are being checked).
		//this might me wrong =j
		winch.setInverted(true);
		
		// Both the Talon SRX and Victor SPX have a follower feature that allows the motor controllers to mimic another motor controller's output.
		// Users will still need to set the motor controller's direction, and neutral mode.
		// The method follow() allows users to create a motor controller follower of not only the same model, but also other models
		// , talon to talon, victor to victor, talon to victor, and victor to talon.
		
		// set peak output to max in case if had been reduced previously
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT);
	}
	

	public synchronized void winchUp() {
		winch.set(ControlMode.PercentOutput, MAX_PCT_OUTPUT);
						
		isWinchingUp = true;		
	}
	
	public synchronized void winchDown() {
		winch.set(ControlMode.PercentOutput, -MAX_PCT_OUTPUT);
		
		isWinchingDown = true;
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
