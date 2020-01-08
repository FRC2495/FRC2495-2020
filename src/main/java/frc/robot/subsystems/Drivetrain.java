package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.command.Subsystem;

import frc.robot.interfaces.*;
import frc.robot.Robot;
import frc.robot.sensors.HMCamera;
import frc.robot.commands.DrivetrainJoystickControl;
import frc.robot.util.*;


public class Drivetrain extends Subsystem implements PIDOutput, PIDOutput2, PIDOutput3, IDrivetrain {

	// general settings
	static final double DIAMETER_WHEEL_INCHES = Robot.COMPETITION_BOT_CONFIG?6:6; //5; TODO set proper diameter
	public static final double PERIMETER_WHEEL_INCHES = DIAMETER_WHEEL_INCHES * Math.PI;
	
	static final int TIMEOUT_MS = 15000;	
	
	public static final double RADIUS_DRIVEVETRAIN_INCHES = 13; // 12.5;
	
	static final double MAX_PCT_OUTPUT = 1.0;
		
	static final int TALON_TIMEOUT_MS = 10;
	public static final int TICKS_PER_REVOLUTION = 4096;


	// turn using camera settings
	// NOTE: it might make sense to decrease the PID controller period below 0.02 sec (which is the period used by the main loop)
	public static final double TURN_USING_CAMERA_PID_CONTROLLER_PERIOD_SECONDS = .01; // 0.01 sec = 10 ms 	
	
	public static final double MIN_TURN_USING_CAMERA_PCT_OUTPUT = 0.4;
	public static final double MAX_TURN_USING_CAMERA_PCT_OUTPUT = 0.7;
	
	public static final double TURN_USING_CAMERA_PROPORTIONAL_GAIN = 0.005; // TODO tune 320 pixels -> 1.6 pct output
	public static final double TURN_USING_CAMERA_INTEGRAL_GAIN = 0.0;
	public static final double TURN_USING_CAMERA_DERIVATIVE_GAIN = 0.0;
	
	public static final int PIXEL_THRESHOLD = HMCamera.HORIZONTAL_CAMERA_RES_PIXELS / 40; // about 3/2 degrees - TODO adjust as needed
	
	public final static int TURN_USING_CAMERA_ON_TARGET_MINIMUM_COUNT = 10; //25; // number of times/iterations we need to be on target to really be on target

	public final static int TURN_USING_CAMERA_STALLED_MINIMUM_COUNT = TURN_USING_CAMERA_ON_TARGET_MINIMUM_COUNT * 2 + 10; // number of times/iterations we need to be stalled to really be stalled

	
	// turn settings
	// NOTE: it might make sense to decrease the PID controller period below 0.02 sec (which is the period used by the main loop)
	static final double TURN_PID_CONTROLLER_PERIOD_SECONDS = .01; // 0.01 sec = 10 ms 	
	
	static final double MIN_TURN_PCT_OUTPUT = Robot.COMPETITION_BOT_CONFIG?0.6:0.6;
	static final double MAX_TURN_PCT_OUTPUT = Robot.COMPETITION_BOT_CONFIG?0.9:0.9;
	
	static final double TURN_PROPORTIONAL_GAIN = 0.04;
	static final double TURN_INTEGRAL_GAIN = 0.0;
	static final double TURN_DERIVATIVE_GAIN = 0.0;
	
	static final int DEGREE_THRESHOLD = 3; //1;
	
	private final static int TURN_ON_TARGET_MINIMUM_COUNT = 10; // number of times/iterations we need to be on target to really be on target
	
	private final static int TURN_STALLED_MINIMUM_COUNT = TURN_ON_TARGET_MINIMUM_COUNT * 2 + 30; // number of times/iterations we need to be stalled to really be stalled
	

	// move using camera settings
	// NOTE: it might make sense to decrease the PID controller period below 0.02 sec (which is the period used by the main loop)
	public static final double MOVE_USING_CAMERA_PID_CONTROLLER_PERIOD_SECONDS = .01; // 0.01 sec = 10 ms 	
	
	public static final double MIN_MOVE_USING_CAMERA_PCT_OUTPUT = 0.1;
	public static final double MAX_MOVE_USING_CAMERA_PCT_OUTPUT = 0.5;
	
	public static final double MOVE_USING_CAMERA_PROPORTIONAL_GAIN = 0.01; // TODO tune 12 inches -> 0.12 pct output
	public static final double MOVE_USING_CAMERA_INTEGRAL_GAIN = 0.0;
	public static final double MOVE_USING_CAMERA_DERIVATIVE_GAIN = 0.0;
	
	public static final int DISTANCE_THRESHOLD_INCHES = 12; // TODO adjust as needed
	
	public final static int MOVE_USING_CAMERA_ON_TARGET_MINIMUM_COUNT = 10; //25; // number of times/iterations we need to be on target to really be on target

	public final static int MOVE_USING_CAMERA_STALLED_MINIMUM_COUNT = MOVE_USING_CAMERA_ON_TARGET_MINIMUM_COUNT * 2 + 10; // number of times/iterations we need to be stalled to really be stalled


	// move settings
	static final int PRIMARY_PID_LOOP = 0;
	
	static final int SLOT_0 = 0;
	
	static final double REDUCED_PCT_OUTPUT = Robot.COMPETITION_BOT_CONFIG?0.4:0.4;
	static final double HIGH_PCT_OUTPUT = Robot.COMPETITION_BOT_CONFIG?0.5:0.5;
	
	static final double MOVE_PROPORTIONAL_GAIN = 0.4;
	static final double MOVE_INTEGRAL_GAIN = 0.0;
	static final double MOVE_DERIVATIVE_GAIN = 0.0;
	
	static final int TALON_TICK_THRESH = 128;
	static final double TICK_THRESH = TALON_TICK_THRESH * 4;
	public static final double TICK_PER_100MS_THRESH = 64; // about a tenth of a rotation per second 

	private final static int MOVE_ON_TARGET_MINIMUM_COUNT = 10; // number of times/iterations we need to be on target to really be on target

	private final static int MOVE_STALLED_MINIMUM_COUNT = MOVE_ON_TARGET_MINIMUM_COUNT * 2 + 30; // number of times/iterations we need to be stalled to really be stalled
	
	
	// variables
	boolean isMoving; // indicates that the drivetrain is moving using the PID controllers embedded on the motor controllers 
	boolean isTurning;  // indicates that the drivetrain is turning using the PID controller hereunder
	boolean isTurningUsingCamera;  // indicates that the drivetrain is turning using the second PID controller hereunder
	boolean isMovingUsingCamera;  // indicates that the drivetrain is turning using the third PID controller hereunder
	boolean isReallyStalled;
	
	double ltac, rtac; // target positions 
	
	private int onTargetCountMoving; // counter indicating how many times/iterations we were on target
	private int onTargetCountTurning; // counter indicating how many times/iterations we were on target
	private int onTargetCountTurningUsingCamera; // counter indicating how many times/iterations we were on target
	private int onTargetCountMovingUsingCamera; // counter indicating how many times/iterations we were on target
	private int stalledCount; // counter indicating how many times/iterations we were stalled

	WPI_TalonSRX masterLeft, masterRight; // motor controllers
	BaseMotorController followerLeft, followerRight; // motor controllers
	
	ADXRS450_Gyro gyro; // gyroscope
	
	DifferentialDrive differentialDrive; // a class to simplify tank or arcade drive (open loop driving) 
	
	Robot robot; // a reference to the robot
	
	PIDController turnPidController; // the PID controller used to turn

	ICamera camera;
	PIDController turnUsingCameraPidController; // the PID controller used to turn using camera
	PIDController moveUsingCameraPidController; // the PID controller used to turn
	
	
	public Drivetrain(WPI_TalonSRX masterLeft_in ,WPI_TalonSRX masterRight_in , BaseMotorController followerLeft_in ,BaseMotorController followerRight_in, ADXRS450_Gyro gyro_in, Robot robot_in, ICamera camera_in) 
	{
		masterLeft = masterLeft_in;
		masterRight = masterRight_in;
		followerLeft = followerLeft_in;
		followerRight = followerRight_in;
		gyro = gyro_in;	
		robot = robot_in;
		camera = camera_in;

		masterLeft.configFactoryDefault();
		followerLeft.configFactoryDefault();
		masterRight.configFactoryDefault();
		followerRight.configFactoryDefault();
		
		// Mode of operation during Neutral output may be set by using the setNeutralMode() function.
		// As of right now, there are two options when setting the neutral mode of a motor controller,
		// brake and coast.
		masterLeft.setNeutralMode(NeutralMode.Brake); // sets the talons on brake mode
		followerLeft.setNeutralMode(NeutralMode.Brake);	
		masterRight.setNeutralMode(NeutralMode.Brake);
		followerRight.setNeutralMode(NeutralMode.Brake);
		
		// Sensors for motor controllers provide feedback about the position, velocity, and acceleration
		// of the system using that motor controller.
		// Note: With Phoenix framework, position units are in the natural units of the sensor.
		// This ensures the best resolution possible when performing closed-loops in firmware.
		// CTRE Magnetic Encoder (relative/quadrature) =  4096 units per rotation
		masterLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
				PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
				
		masterRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
				PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
		
		// Sensor phase is the term used to explain sensor direction.
		// In order for limit switches and closed-loop features to function properly the sensor and motor has to be in-phase.
		// This means that the sensor position must move in a positive direction as the motor controller drives positive output.  

		masterLeft.setSensorPhase(true);
		masterRight.setSensorPhase(true);
		
		// Disables limit switches
		masterLeft.overrideLimitSwitchesEnable(false);
		masterRight.overrideLimitSwitchesEnable(false);
		
		// Motor controller output direction can be set by calling the setInverted() function as seen below.
		// Note: Regardless of invert value, the LEDs will blink green when positive output is requested (by robot code or firmware closed loop).
		// Only the motor leads are inverted. This feature ensures that sensor phase and limit switches will properly match the LED pattern
		// (when LEDs are green => forward limit switch and soft limits are being checked). 
		masterLeft.setInverted(true);
		masterRight.setInverted(false);
		followerLeft.setInverted(true); 
		followerRight.setInverted(false);
		
		// motors will turn in opposite directions if not inverted 
		
		// Both the Talon SRX and Victor SPX have a follower feature that allows the motor controllers to mimic another motor controller's output.
		// Users will still need to set the motor controller's direction, and neutral mode.
		// The method follow() allows users to create a motor controller follower of not only the same model, but also other models
		// , talon to talon, victor to victor, talon to victor, and victor to talon.
		followerLeft.follow(masterLeft);
		followerRight.follow(masterRight);
		
		// set peak output to max in case if had been reduced previously
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT);

		
		//creates a PID controller
		turnPidController = new PIDController(TURN_PROPORTIONAL_GAIN, TURN_INTEGRAL_GAIN, TURN_DERIVATIVE_GAIN, gyro, this, TURN_PID_CONTROLLER_PERIOD_SECONDS);
		
		turnPidController.setInputRange(-180, 180); // valid input range 
		turnPidController.setOutputRange(-MAX_TURN_PCT_OUTPUT, MAX_TURN_PCT_OUTPUT); // output range NOTE: might need to change signs
		
		turnPidController.setContinuous(true); // because -180 degrees is the same as 180 degrees (needs input range to be defined first)
		turnPidController.setAbsoluteTolerance(DEGREE_THRESHOLD); // 1 degree error tolerated

		//creates a second PID controller
		turnUsingCameraPidController = new PIDController(TURN_USING_CAMERA_PROPORTIONAL_GAIN, TURN_USING_CAMERA_INTEGRAL_GAIN, TURN_USING_CAMERA_DERIVATIVE_GAIN, camera, new PIDOutput2Adapter(this), TURN_USING_CAMERA_PID_CONTROLLER_PERIOD_SECONDS);
		
		turnUsingCameraPidController.setInputRange(-HMCamera.HORIZONTAL_CAMERA_RES_PIXELS/2, HMCamera.HORIZONTAL_CAMERA_RES_PIXELS/2); // valid input range 
		turnUsingCameraPidController.setOutputRange(-MAX_TURN_USING_CAMERA_PCT_OUTPUT, MAX_TURN_USING_CAMERA_PCT_OUTPUT); // output range NOTE: might need to change signs
		
		turnUsingCameraPidController.setAbsoluteTolerance(PIXEL_THRESHOLD); // error tolerated

		//creates a third PID controller
		moveUsingCameraPidController = new PIDController(MOVE_USING_CAMERA_PROPORTIONAL_GAIN, MOVE_USING_CAMERA_INTEGRAL_GAIN, MOVE_USING_CAMERA_DERIVATIVE_GAIN, new PIDSource2Adapter(camera), new PIDOutput3Adapter(this), MOVE_USING_CAMERA_PID_CONTROLLER_PERIOD_SECONDS);

		moveUsingCameraPidController.setInputRange(-HMCamera.SAFE_DISTANCE_INCHES, HMCamera.SAFE_DISTANCE_INCHES); // valid input range 
		moveUsingCameraPidController.setOutputRange(-MAX_MOVE_USING_CAMERA_PCT_OUTPUT, MAX_MOVE_USING_CAMERA_PCT_OUTPUT); // output range NOTE: might need to change signs
		
		moveUsingCameraPidController.setAbsoluteTolerance(DISTANCE_THRESHOLD_INCHES); // error tolerated
		
		
		differentialDrive = new DifferentialDrive(masterLeft, masterRight);
		differentialDrive.setSafetyEnabled(false); // disables the stupid timeout error when we run in closed loop
	}
	
	@Override
	public void initDefaultCommand() {
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

		// Set the default command for a subsystem here.
		setDefaultCommand(new DrivetrainJoystickControl());
	}

	@Override
	public void periodic() {
		// Put code here to be run every loop

	}  

	// this method needs to be paired with checkTurnAngleUsingPidController()
	public void turnAngleUsingPidController(double angle) {
		// switches to percentage vbus
		stop(); // resets state
		
		gyro.reset(); // resets to zero for now
		//double current = gyro.getAngle();
		double heading = angle; //+ current; // calculates new heading
		
		turnPidController.setSetpoint(heading); // sets the heading
		turnPidController.enable(); // begins running
		
		isTurning = true;
		onTargetCountTurning = 0;
		isReallyStalled = false;
		stalledCount = 0;
	}

	// this method needs to be paired with checkTurnAngleUsingPidController()
	public void turnToPreviousKnownHeadingUsingPidController() {
		// switches to percentage vbus
		stop(); // resets state
		
		// we do NOT reset the gyro since we want to go back to zero

		double heading = 0; // go back to zero
		
		turnPidController.setSetpoint(heading); // sets the heading
		turnPidController.enable(); // begins running
		
		isTurning = true;
		onTargetCountTurning = 0;
		isReallyStalled = false;
		stalledCount = 0;
	}	
		
	// This method checks that we are within target up to ON_TARGET_MINIMUM_COUNT times
	// It relies on its own counter
	public boolean tripleCheckTurnAngleUsingPidController() {	
		if (isTurning) {
			boolean isOnTarget = turnPidController.onTarget();
			
			if (isOnTarget) { // if we are on target in this iteration 
				onTargetCountTurning++; // we increase the counter
			} else { // if we are not on target in this iteration
				if (onTargetCountTurning > 0) { // even though we were on target at least once during a previous iteration
					onTargetCountTurning = 0; // we reset the counter as we are not on target anymore
					System.out.println("Triple-check failed (turning).");
				} else {
					// we are definitely turning
				}
			}
			
			if (onTargetCountTurning > TURN_ON_TARGET_MINIMUM_COUNT) { // if we have met the minimum
				isTurning = false;
			}
			
			if (!isTurning) {
				System.out.println("You have reached the target (turning).");
				stop();				 
			}
		}
		return isTurning;
	}

	// this method needs to be paired with checkTurnUsingCameraPidController()
	public void turnUsingCameraPidController()
	{
		// switches to percentage vbus
		stop(); // resets state 
		
		turnUsingCameraPidController.setSetpoint(0); // we want to end centered
		turnUsingCameraPidController.enable(); // begins running
		
		isTurningUsingCamera = true;
		onTargetCountTurningUsingCamera = 0;
		isReallyStalled = false;
		stalledCount = 0;
	}
		
	public boolean tripleCheckTurnUsingCameraPidController()
	{
		if (isTurningUsingCamera) {
			boolean isOnTarget = turnUsingCameraPidController.onTarget();
			
			if (isOnTarget) { // if we are on target in this iteration 
				onTargetCountTurningUsingCamera++; // we increase the counter
			} else { // if we are not on target in this iteration
				if (onTargetCountTurningUsingCamera > 0) { // even though we were on target at least once during a previous iteration
					onTargetCountTurningUsingCamera = 0; // we reset the counter as we are not on target anymore
					System.out.println("Triple-check failed (turning using camera).");
				} else {
					// we are definitely turning
				}
			}
			
			if (onTargetCountTurningUsingCamera > TURN_USING_CAMERA_ON_TARGET_MINIMUM_COUNT) { // if we have met the minimum
				isTurningUsingCamera = false;
			}
			
			if (!isTurningUsingCamera) {
				System.out.println("You have reached the target (turning using camera).");
				stop();				 
			}
		}
		return isTurningUsingCamera;
	}

	// this method needs to be paired with checkMoveUsingCameraPidController()
	public void moveUsingCameraPidController()
	{
		// switches to percentage vbus
		stop(); // resets state 
		
		moveUsingCameraPidController.setSetpoint(0); // we want to end centered
		moveUsingCameraPidController.enable(); // begins running
		
		isMovingUsingCamera = true;
		onTargetCountMovingUsingCamera = 0;
		isReallyStalled = false;
		stalledCount = 0;
	}
		
	public boolean tripleCheckMoveUsingCameraPidController()
	{
		if (isMovingUsingCamera) {
			boolean isOnTarget = moveUsingCameraPidController.onTarget();
			
			if (isOnTarget) { // if we are on target in this iteration 
				onTargetCountMovingUsingCamera++; // we increase the counter
			} else { // if we are not on target in this iteration
				if (onTargetCountMovingUsingCamera > 0) { // even though we were on target at least once during a previous iteration
					onTargetCountMovingUsingCamera = 0; // we reset the counter as we are not on target anymore
					System.out.println("Triple-check failed (moving using camera).");
				} else {
					// we are definitely turning
				}
			}
			
			if (onTargetCountMovingUsingCamera > MOVE_USING_CAMERA_ON_TARGET_MINIMUM_COUNT) { // if we have met the minimum
				isMovingUsingCamera = false;
			}
			
			if (!isMovingUsingCamera) {
				System.out.println("You have reached the target (moving using camera).");
				stop();				 
			}
		}
		return isMovingUsingCamera;
	}
		
	public void moveDistance(double dist) // moves the distance in inch given
	{
		moveDistance(dist, REDUCED_PCT_OUTPUT);
	}
	
	public void moveDistanceHighSpeed(double dist) // moves the distance in inch given
	{
		moveDistance(dist, HIGH_PCT_OUTPUT);
	}
	
	// this method needs to be paired with checkMoveDistance()
	public void moveDistance(double dist, double percentOutput) // moves the distance in inch given
	{
		stop(); // in case we were still doing something
		
		resetEncoders();
		setPIDParameters();
		setNominalAndPeakOutputs(percentOutput); //this has a global impact, so we reset in stop()
		
		rtac = dist / PERIMETER_WHEEL_INCHES * TICKS_PER_REVOLUTION;
		ltac = dist / PERIMETER_WHEEL_INCHES * TICKS_PER_REVOLUTION;
		
		rtac = - rtac; // account for fact that front of robot is back from sensor's point of view
		ltac = - ltac;
		
		System.out.println("rtac, ltac: " + rtac + ", " + ltac);
		masterRight.set(ControlMode.Position, rtac);
		masterLeft.set(ControlMode.Position, ltac);

		isMoving = true;
		onTargetCountMoving = 0;
		isReallyStalled = false;
		stalledCount = 0;
	}
	
	public boolean tripleCheckMoveDistance() {
		if (isMoving) {
			
			double rerror = masterRight.getClosedLoopError(PRIMARY_PID_LOOP);
			double lerror = masterLeft.getClosedLoopError(PRIMARY_PID_LOOP);
			
			boolean isOnTarget = (Math.abs(rerror) < TICK_THRESH && Math.abs(lerror) < TICK_THRESH);
			
			if (isOnTarget) { // if we are on target in this iteration 
				onTargetCountMoving++; // we increase the counter
			} else { // if we are not on target in this iteration
				if (onTargetCountMoving > 0) { // even though we were on target at least once during a previous iteration
					onTargetCountMoving = 0; // we reset the counter as we are not on target anymore
					System.out.println("Triple-check failed (moving).");
				} else {
					// we are definitely moving
					//System.out.println("ltac, rtac: " + ltac + ", " + rtac);
					//System.out.println("encoder left: " + masterLeft.getSelectedSensorPosition(PRIMARY_PID_LOOP));
					//System.out.println("encoder right: " + masterRight.getSelectedSensorPosition(PRIMARY_PID_LOOP));
					
					//System.out.println("moving error left: " + lerror);
					//System.out.println("moving error right: " + rerror);
				}
			}
			
			if (onTargetCountMoving > MOVE_ON_TARGET_MINIMUM_COUNT) { // if we have met the minimum
				isMoving = false;
			}
			
			if (!isMoving) {
				System.out.println("You have reached the target (moving).");
				stop();				 
			}
		}
		return isMoving;
	}
	
	private double arclength(int angle) // returns the inches needed to be moved
	// to turn the specified angle
	{
		return Math.toRadians(angle) * RADIUS_DRIVEVETRAIN_INCHES;
	}

	// this method needs to be paired with checkMoveDistance()
	public void moveDistanceAlongArc(int angle) {
		stop(); // in case we were still doing something
		
		double dist = arclength(angle);
		double ldist, rdist;
		
		ldist = dist;
		rdist = -dist;
		
		resetEncoders();
		setPIDParameters();
		
		rtac = rdist / PERIMETER_WHEEL_INCHES * TICKS_PER_REVOLUTION;
		ltac = ldist / PERIMETER_WHEEL_INCHES * TICKS_PER_REVOLUTION;
		System.out.println("rtac, ltac: " + rtac + ", " + ltac);
		masterRight.set(ControlMode.Position, -rtac);
		masterLeft.set(ControlMode.Position, -ltac);
		
		isMoving = true;
		onTargetCountMoving = 0;
		isReallyStalled = false;
		stalledCount = 0;
	}
	
	// return if drivetrain might be stalled
	public boolean tripleCheckIfStalled() {
		if (isMoving || isTurning || isMovingUsingCamera || isTurningUsingCamera) {
			
			double rvelocity = getRightEncoderVelocity();
			double lvelocity = getLeftEncoderVelocity();
			
			boolean isStalled = (Math.abs(rvelocity) < TICK_PER_100MS_THRESH && Math.abs(lvelocity) < TICK_PER_100MS_THRESH);
			
			if (isStalled) { // if we are stalled in this iteration 
				stalledCount++; // we increase the counter
			} else { // if we are not stalled in this iteration
				if (stalledCount > 0) { // even though we were stalled at least once during a previous iteration
					stalledCount = 0; // we reset the counter as we are not stalled anymore
					System.out.println("Triple-check failed (detecting stall).");
				} else {
					// we are definitely not stalled
					
					//System.out.println("moving velocity left: " + lvelocity);
					//System.out.println("moving velocity right: " + rvelocity);
				}
			}
			
			if (isMoving && stalledCount > MOVE_STALLED_MINIMUM_COUNT) { // if we have met the minimum
				isReallyStalled = true;
			}
			
			if (isTurning && stalledCount > TURN_STALLED_MINIMUM_COUNT) { // if we have met the minimum
				isReallyStalled = true;
			}

			if (isMovingUsingCamera && stalledCount > MOVE_USING_CAMERA_STALLED_MINIMUM_COUNT) { // if we have met the minimum
				isReallyStalled = true;
			}
			
			if (isTurningUsingCamera && stalledCount > TURN_USING_CAMERA_STALLED_MINIMUM_COUNT) { // if we have met the minimum
				isReallyStalled = true;
			}
			
			if (isReallyStalled) {
				System.out.println("WARNING: Stall detected!");
				stop(); // WE STOP IF A STALL IS DETECTED				 
			}
		}
		
		return isReallyStalled;
	}
	
	public void stop() {
		turnPidController.disable(); // exits PID loop
		turnUsingCameraPidController.disable(); // exits PID loop
		moveUsingCameraPidController.disable(); // exits PID loop
		 
		masterLeft.set(ControlMode.PercentOutput, 0);
		masterRight.set(ControlMode.PercentOutput, 0);
		
		isMoving = false;
		isTurning = false;
		isMovingUsingCamera = false;
		isTurningUsingCamera = false;
		
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT); // we undo what me might have changed
	}
	
	public void setPIDParameters()
	{
		masterRight.configAllowableClosedloopError(SLOT_0, TALON_TICK_THRESH, TALON_TIMEOUT_MS);
		masterLeft.configAllowableClosedloopError(SLOT_0, TALON_TICK_THRESH, TALON_TIMEOUT_MS);
		
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
		
		masterRight.config_kP(SLOT_0, MOVE_PROPORTIONAL_GAIN, TALON_TIMEOUT_MS);
		masterRight.config_kI(SLOT_0, MOVE_INTEGRAL_GAIN, TALON_TIMEOUT_MS);
		masterRight.config_kD(SLOT_0, MOVE_DERIVATIVE_GAIN, TALON_TIMEOUT_MS);
		masterRight.config_kF(SLOT_0, 0, TALON_TIMEOUT_MS);
		
		masterLeft.config_kP(SLOT_0, MOVE_PROPORTIONAL_GAIN, TALON_TIMEOUT_MS);
		masterLeft.config_kI(SLOT_0, MOVE_INTEGRAL_GAIN, TALON_TIMEOUT_MS);
		masterLeft.config_kD(SLOT_0, MOVE_DERIVATIVE_GAIN, TALON_TIMEOUT_MS);	
		masterLeft.config_kF(SLOT_0, 0, TALON_TIMEOUT_MS);
	}
	
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput)
	{
		masterLeft.configPeakOutputForward(peakOutput, TALON_TIMEOUT_MS);
		masterLeft.configPeakOutputReverse(-peakOutput, TALON_TIMEOUT_MS);
		masterRight.configPeakOutputForward(peakOutput, TALON_TIMEOUT_MS);
		masterRight.configPeakOutputReverse(-peakOutput, TALON_TIMEOUT_MS);
		
		masterRight.configNominalOutputForward(0, TALON_TIMEOUT_MS);
		masterLeft.configNominalOutputForward(0, TALON_TIMEOUT_MS);
		masterRight.configNominalOutputReverse(0, TALON_TIMEOUT_MS);
		masterLeft.configNominalOutputReverse(0, TALON_TIMEOUT_MS);
	}

	public void joystickControl(Joystick joyLeft, Joystick joyRight, boolean held) // sets talons to
	// joystick control
	{
		if (!isMoving && !isTurning && !isMovingUsingCamera && !isTurningUsingCamera) // if we are already doing a move or turn we don't take over
		{
			if(!held)
			{
				//masterRight.set(ControlMode.PercentOutput, joyRight.getY() * .75);
				//masterLeft.set(ControlMode.PercentOutput, joyLeft.getY() * .75);
				
				//differentialDrive.tankDrive(joyLeft.getY() * .75, -joyRight.getY() * .75); // right needs to be reversed
				
				differentialDrive.arcadeDrive(-joyRight.getX() * .80, joyLeft.getY() * .80); // right needs to be reversed
			}
			else
			{
				//masterRight.set(ControlMode.PercentOutput, joyRight.getY());
				//masterLeft.set(ControlMode.PercentOutput, joyLeft.getY());
				
				//differentialDrive.tankDrive(joyLeft.getY(), -joyRight.getY()); // right needs to be reversed
				
				differentialDrive.arcadeDrive(-joyRight.getX(), joyLeft.getY()); // right needs to be reversed
			}
		}
	}	

	// offers direct access to raw arcade drive functionality
	public void arcadeDrive(double movePctOutput, double turnPctOutput) {
		differentialDrive.arcadeDrive(-turnPctOutput, movePctOutput); // TODO double-check signs
	}
	
	public int getRightEncoderPosition() {
		return (int) (masterRight.getSelectedSensorPosition(PRIMARY_PID_LOOP));
	}

	public int getLeftEncoderPosition() {
		return (int) (masterLeft.getSelectedSensorPosition(PRIMARY_PID_LOOP));
	}

	public int getRightPosition() {
		return (int) (masterRight.getSelectedSensorPosition(PRIMARY_PID_LOOP)*PERIMETER_WHEEL_INCHES/TICKS_PER_REVOLUTION);
	}

	public int getLeftPosition() {
		return (int) (masterLeft.getSelectedSensorPosition(PRIMARY_PID_LOOP)*PERIMETER_WHEEL_INCHES/TICKS_PER_REVOLUTION);
	}
	
	public int getRightEncoderVelocity() {
		return (int) (masterRight.getSelectedSensorVelocity(PRIMARY_PID_LOOP));
	}

	public int getLeftEncoderVelocity() {
		return (int) (masterLeft.getSelectedSensorVelocity(PRIMARY_PID_LOOP));
	}
	
	public boolean isMoving() {
		return isMoving;
	}

	public boolean isMovingUsingCamera() {
		return isMovingUsingCamera;
	}
	
	public boolean isTurning(){
		return isTurning;
	}

	public boolean isTurningUsingCamera() {
		return isTurningUsingCamera;
	}
	
	// return if stalled
	public boolean isStalled() {
		return isReallyStalled;
	}

	@Override
	public void pidWrite(double output) {
		
		// calling disable() on controller will force a call to pidWrite with zero output
		// which we need to handle by not doing anything that could have a side effect 
		if (output != 0 && Math.abs(turnPidController.getError()) < DEGREE_THRESHOLD)
		{
			output = 0;
		}
		if (output != 0 && Math.abs(output) < MIN_TURN_PCT_OUTPUT)
		{
			output = Math.signum(output) * MIN_TURN_PCT_OUTPUT;
		}
		masterRight.set(ControlMode.PercentOutput, +output);
		masterLeft.set(ControlMode.PercentOutput, -output);		
	}
	
	@Override
	public void pidWrite2(double output) {

		// calling disable() on controller will force a call to pidWrite with zero output
		// which we need to handle by not doing anything that could have a side effect 
		if (output != 0 && Math.abs(turnUsingCameraPidController.getError()) < PIXEL_THRESHOLD)
		{
			output = 0;
		}
		if (output != 0 && Math.abs(output) < MIN_TURN_USING_CAMERA_PCT_OUTPUT)
		{
			output = Math.signum(output) * MIN_TURN_USING_CAMERA_PCT_OUTPUT;
		}
		masterRight.set(ControlMode.PercentOutput, +output); // TODO double-check signs
		masterLeft.set(ControlMode.PercentOutput, -output);		
	}

	@Override
	public void pidWrite3(double output) {

		// calling disable() on controller will force a call to pidWrite with zero output
		// which we need to handle by not doing anything that could have a side effect 
		if (output != 0 && Math.abs(moveUsingCameraPidController.getError()) < DISTANCE_THRESHOLD_INCHES)
		{
			output = 0;
		}
		if (output != 0 && Math.abs(output) < MIN_MOVE_USING_CAMERA_PCT_OUTPUT)
		{
			output = Math.signum(output) * MIN_MOVE_USING_CAMERA_PCT_OUTPUT;
		}
		masterRight.set(ControlMode.PercentOutput, -output); // TODO double-check signs
		masterLeft.set(ControlMode.PercentOutput, -output);		
	}
	
	// MAKE SURE THAT YOU ARE NOT IN A CLOSED LOOP CONTROL MODE BEFORE CALLING THIS METHOD.
	// OTHERWISE THIS IS EQUIVALENT TO MOVING TO THE DISTANCE TO THE CURRENT ZERO IN REVERSE! 
	public void resetEncoders() {
		masterRight.set(ControlMode.PercentOutput, 0); // we switch to open loop to be safe.
		masterLeft.set(ControlMode.PercentOutput, 0);			
		
		masterRight.setSelectedSensorPosition(0, PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
		masterLeft.setSelectedSensorPosition(0, PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
	}	
}


