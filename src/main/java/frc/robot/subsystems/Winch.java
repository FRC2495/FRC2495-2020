/**
 * 
 */
package frc.robot.subsystems;

import java.util.Timer;
import java.util.TimerTask;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

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

	
	// general settings
	static final double MAX_PCT_OUTPUT = 1.0;
	static final int WAIT_MS = 1000;
	static final int TIMEOUT_MS = 5000;

	static final int TALON_TIMEOUT_MS = 20;

	// move settings
	static final int PRIMARY_PID_LOOP = 0;
	
	static final int SLOT_0 = 0;
	
	static final double REDUCED_PCT_OUTPUT = 0.5;
	
	static final double MOVE_PROPORTIONAL_GAIN = 0.06;
	static final double MOVE_INTEGRAL_GAIN = 0.0;
	static final double MOVE_DERIVATIVE_GAIN = 0.0;
	
	static final int TALON_TICK_THRESH = 256;//128;
	static final double TICK_THRESH = 4096;	
	
	private final static int MOVE_ON_TARGET_MINIMUM_COUNT= 10; // number of times/iterations we need to be on target to really be on target

	
	protected static final long WINCH_STOP_DELAY_MS = 500; // TODO tune

	WPI_TalonSRX winch; 
	BaseMotorController winch_follower;
	
	boolean isWinchingUp;
	boolean isWinchingDown;
	
	Robot robot;
	
	
	public Winch(WPI_TalonSRX winch_in, BaseMotorController winch_follower_in, Robot robot_in) {
		
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
				
		// Sensor phase is the term used to explain sensor direction.
		// In order for limit switches and closed-loop features to function properly the sensor and motor has to be in-phase.
		// This means that the sensor position must move in a positive direction as the motor controller drives positive output.
		winch.setSensorPhase(true);

		//Enable limit switches
		winch.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, TALON_TIMEOUT_MS);
		winch.overrideLimitSwitchesEnable(true);
	
		// Motor controller output direction can be set by calling the setInverted() function as seen below.
		// Note: Regardless of invert value, the LEDs will blink green when positive output is requested (by robot code or firmware closed loop).
		// Only the motor leads are inverted. This feature ensures that sensor phase and limit switches will properly match the LED pattern
		// (when LEDs are green => forward limit switch and soft limits are being checked).
		winch.setInverted(false);
		winch_follower.setInverted(false);
		
		// Both the Talon SRX and Victor SPX have a follower feature that allows the motor controllers to mimic another motor controller's output.
		// Users will still need to set the motor controller's direction, and neutral mode.
		// The method follow() allows users to create a motor controller follower of not only the same model, but also other models
		// , talon to talon, victor to victor, talon to victor, and victor to talon.
		winch_follower.follow(winch);

		//setPIDParameters();
		
		// set peak output to max in case if had been reduced previously
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT);

	
		// Sensors for motor controllers provide feedback about the position, velocity, and acceleration
		// of the system using that motor controller.
		// Note: With Phoenix framework, position units are in the natural units of the sensor.
		// This ensures the best resolution possible when performing closed-loops in firmware.
		// CTRE Magnetic Encoder (relative/quadrature) =  4096 units per rotation		
		winch.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,	PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
		
		// this will reset the encoder automatically when at or past the forward limit sensor
		winch.configSetParameter(ParamEnum.eClearPositionOnLimitF, 1, 0, 0, TALON_TIMEOUT_MS);
		
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
	
	private void setPIDParameters() {		
		winch.configAllowableClosedloopError(SLOT_0, TALON_TICK_THRESH, TALON_TIMEOUT_MS);
		
		// P is the proportional gain. It modifies the closed-loop output by a proportion (the gain value)
		// of the closed-loop error.
		// P gain is specified in output unit per error unit.
		// When tuning P, it's useful to estimate your starting value.
		// If you want your mechanism to drive 50% output when the error is 4096 (one rotation when using CTRE Mag Encoder),
		// then the calculated Proportional Gain would be (0.50 X 1023) / 4096 = ~0.125.
		
		// I is the integral gain. It modifies the closed-loop output according to the integral error
		// (summation of the closed-loop error each iteration).
		// I gain is specified in output units per integrated error.
		// If your mechanism never quite reaches your target and using integral gain is viable,
		// start with 1/100th of the Proportional Gain.
		
		// D is the derivative gain. It modifies the closed-loop output according to the derivative error
		// (change in closed-loop error each iteration).
		// D gain is specified in output units per derivative error.
		// If your mechanism accelerates too abruptly, Derivative Gain can be used to smooth the motion.
		// Typically start with 10x to 100x of your current Proportional Gain.
		
		winch.config_kP(SLOT_0, MOVE_PROPORTIONAL_GAIN, TALON_TIMEOUT_MS);
		winch.config_kI(SLOT_0, MOVE_INTEGRAL_GAIN, TALON_TIMEOUT_MS);
		winch.config_kD(SLOT_0, MOVE_DERIVATIVE_GAIN, TALON_TIMEOUT_MS);
		winch.config_kF(SLOT_0, 0, TALON_TIMEOUT_MS);
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
		if (!isWinchingUp && !isWinchingDown) // if we are already doing a move we don't take over
		{
			winch.set(ControlMode.PercentOutput, joystick.getY());
		}
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

	public boolean getForwardLimitSwitchState() {
		return winch.getSensorCollection().isFwdLimitSwitchClosed();
	}

}
