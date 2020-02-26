package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
//import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import frc.robot.auton.CustomAuton;
import frc.robot.commands.GamepadRumbleUsingCamera;
import frc.robot.commands.IndicatorTimedScrollRainbow;
import frc.robot.interfaces.*;
import frc.robot.sensors.*;
//import frc.robot.commands.*;
import frc.robot.subsystems.*;
import frc.robot.util.*;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
	// IMPORTANT MAKE SURE THAT THIS CONSTANT IS SET TO TRUE IF USING COMPETITION BOT!
	// use this constant to switch between competition and practice bot
	public static final boolean COMPETITION_BOT_CONFIG = true;

	public static OI oi;
	
	Command m_autonomousCommand;
	Command gamepadRumbleUsingCameraCommand;
	Command indicatorTimedScrollRainbow;
	
	// choosers (for auton)
	
	public static final String AUTON_DO_NOTHING = "Do Nothing";
	public static final String AUTON_CUSTOM = "My Auto";
	private String autonSelected;
	private SendableChooser<String> autonChooser = new SendableChooser<>();
	
	public static final String START_POSITION_1 = "Starting Position 1";
	public static final String START_POSITION_2 = "Starting Position 2";
	public static final String START_POSITION_3 = "Starting Position 3";
	private String startPosition;
	private SendableChooser<String> startPositionChooser = new SendableChooser<>();

	public static final String MAIN_TARGET_OPPONENT_TRENCH= "Opponent Trench";
	public static final String MAIN_TARGET_ALLIANCE_TRENCH= "Alliance Trench";
	public static final String MAIN_TARGET_RENDEZVOUS_POINT= "Rendezvous Point";
	public static final String MAIN_TARGET_NOWHERE = "Nowhere";
	private String mainTarget;
	private SendableChooser<String> mainTargetChooser = new SendableChooser<>();
	
	public static final String CAMERA_OPTION_USE_ALWAYS = "Always";
	public static final String CAMERA_OPTION_USE_OPEN_LOOP_ONLY = "Open Loop Only";
	public static final String CAMERA_OPTION_USE_CLOSED_LOOP_ONLY = "Closed Loop Only";
	public static final String CAMERA_OPTION_USE_NEVER = "Never";
	private String cameraOption;
	private SendableChooser<String> cameraOptionChooser = new SendableChooser<>();
	
	public static final String SONAR_OPTION_USE_ALWAYS = "Always";
	public static final String SONAR_OPTION_USE_RELEASE_ONLY = "Release Only";
	public static final String SONAR_OPTION_USE_GRASP_ONLY = "Grasp Only";
	public static final String SONAR_OPTION_USE_NEVER = "Never";
	private String sonarOption;
	private SendableChooser<String> sonarOptionChooser = new SendableChooser<>();
	
	public static final String GRASPER_OPTION_RELEASE = "Release";
	public static final String GRASPER_OPTION_DONT_RELEASE = "Don't Release"; 
	private String releaseSelected;
	private SendableChooser<String> releaseChooser = new SendableChooser<>();

	public static final String AUTON_OPTION_RELOAD = "Reload";
	public static final String AUTON_OPTION_DONT_RELOAD = "Don't Reload"; 
	private String autonOption;
	private SendableChooser<String> autonOptionChooser = new SendableChooser<>();

	// sensors
	
	public static ICamera camera;
	
	public static ADXRS450_Gyro gyro; // gyro
	static boolean hasGyroBeenManuallyCalibratedAtLeastOnce = false;
	
	public static Sonar sonar;
	
	public static HMAccelerometer accelerometer;

	public static PressureSensor pressureSensor;

	public static ColorSensor colorSensor;
	
	// motorized devices
	
	public static /*I*/Drivetrain drivetrain;

	WPI_TalonSRX frontLeft;
	WPI_TalonSRX frontRight;
	BaseMotorController rearLeft; 
	BaseMotorController rearRight;

	public static /*I*/Grasper grasper;
	
	//BaseMotorController shooterLeft;
	//BaseMotorController shooterRight;
	
	public static /*I*/Hinge hingeControl;
	
	WPI_TalonSRX hinge_master;
	BaseMotorController hinge_follower;

	WPI_TalonSRX grasp;

	//public BaseMotorController spinnerMotor;
	public static Spinner spinnerWheel;

	WPI_TalonSRX winch_master;
	BaseMotorController winch_follower;

	public static /*I*/Winch winchControl;
	
	// pneumatic devices
	
	Compressor compressor; // the compressor's lifecycle needs to be the same as the robot

	public static VomitShooter vomitShooter;
	public static ShooterPusher shooterPusher;
	public static Gearbox gearbox;

	public static WinchStopper winchStopperControl; 
	public static WinchLock winchLockControl;

	public static SpinnerRaiser spinnerRaiser;

	// misc

	public static Indicator indicator;

	public static GameData gameData;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		// choosers (for auton)
		
		autonChooser.setDefaultOption("Do Nothing", AUTON_DO_NOTHING);
		autonChooser.addOption("My Auto", AUTON_CUSTOM);
		SmartDashboard.putData("Auto choices", autonChooser);

		startPositionChooser.addOption("Starting Position 1", START_POSITION_1);
		startPositionChooser.setDefaultOption("Starting Position 2", START_POSITION_2);
		startPositionChooser.addOption("Starting Position 3", START_POSITION_3);
		SmartDashboard.putData("Start positions", startPositionChooser);

		mainTargetChooser.setDefaultOption("To Nowhere", MAIN_TARGET_NOWHERE);
		mainTargetChooser.addOption("Alliance Trench", MAIN_TARGET_ALLIANCE_TRENCH);
		mainTargetChooser.addOption("Opponent Trench", MAIN_TARGET_OPPONENT_TRENCH);
		mainTargetChooser.addOption("Rendezvous Point", MAIN_TARGET_RENDEZVOUS_POINT);
		SmartDashboard.putData("Main targets", mainTargetChooser);
		
		cameraOptionChooser.setDefaultOption("Always", CAMERA_OPTION_USE_ALWAYS);
		cameraOptionChooser.addOption("Open Loop Only", CAMERA_OPTION_USE_OPEN_LOOP_ONLY);
		cameraOptionChooser.addOption("Closed Loop Only", CAMERA_OPTION_USE_CLOSED_LOOP_ONLY);
		cameraOptionChooser.addOption("Never", CAMERA_OPTION_USE_NEVER);		
		SmartDashboard.putData("Camera options", cameraOptionChooser);
		
		sonarOptionChooser.setDefaultOption("Always", SONAR_OPTION_USE_ALWAYS);
		sonarOptionChooser.addOption("Release Only", SONAR_OPTION_USE_RELEASE_ONLY);
		sonarOptionChooser.addOption("Grasp Only", SONAR_OPTION_USE_GRASP_ONLY);		
		sonarOptionChooser.addOption("Never", SONAR_OPTION_USE_NEVER);
		SmartDashboard.putData("Sonar options", sonarOptionChooser);
		
		releaseChooser.setDefaultOption("Release", GRASPER_OPTION_RELEASE);
		releaseChooser.addOption("Don't release", GRASPER_OPTION_DONT_RELEASE);
		SmartDashboard.putData("Release options", releaseChooser);

		autonOptionChooser.setDefaultOption("Reload", AUTON_OPTION_RELOAD);
		autonOptionChooser.addOption("Don't Reload", AUTON_OPTION_DONT_RELOAD);
		SmartDashboard.putData("Auton options", autonOptionChooser);		


		// sensors
			
		sonar = new Sonar(Ports.Analog.SONAR); 
			
		gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0); // we want to instantiate before we pass to drivetrain	

		gyro.calibrate(); 
		gyro.reset();

		//camera = new HMCamera("GRIP/myContoursReport");
		camera = new LimelightCamera();

		accelerometer = new HMAccelerometer();

		pressureSensor = new PressureSensor();

		colorSensor = new ColorSensor();


		// motorized devices

		frontLeft = new WPI_TalonSRX(Ports.CAN.LEFT_FRONT);
		frontRight = new WPI_TalonSRX(Ports.CAN.RIGHT_FRONT);
		rearLeft = new WPI_TalonFX(Ports.CAN.LEFT_REAR);
		rearRight = new WPI_TalonFX(Ports.CAN.RIGHT_REAR);

		hinge_master = new WPI_TalonSRX(Ports.CAN.HINGE_MASTER);
		hinge_follower = new WPI_TalonSRX(Ports.CAN.HINGE_FOLLOWER);

		grasp = new WPI_TalonSRX(Ports.CAN.GRASPER);

		drivetrain = new Drivetrain( frontLeft, frontRight, rearLeft, rearRight, gyro, this, camera);	
		
		hingeControl = new Hinge(hinge_master, hinge_follower, this);
		
		grasper = new Grasper(grasp, this);

		//spinnerMotor = new WPI_TalonSRX(Ports.CAN.SPINNER);
		//spinnerWheel = new Spinner(spinnerMotor,this);

		//vomitShooter = new VomitShooter(shooterLeft, shooterRight, this); 

		winch_master = new WPI_TalonSRX(Ports.CAN.WINCH_MASTER);
		winch_follower = new WPI_TalonSRX(Ports.CAN.WINCH_FOLLOWER);

		winchControl = new Winch(winch_master, winch_follower, this);


		// pneumatic devices

		compressor = new Compressor();
		compressor.checkCompressor();
   
		//shooterPusher = new ShooterPusher();

		gearbox = new Gearbox();

		winchStopperControl = new WinchStopper();
		winchLockControl = new WinchLock();

		spinnerRaiser = new SpinnerRaiser();


		// misc

		indicator = new Indicator(camera);

		gameData = new GameData();
		
		// OI must be constructed after subsystems. If the OI creates Commands
		//(which it very likely will), subsystems are not guaranteed to be
		// constructed yet. Thus, their requires() statements may grab null
		// pointers. Bad news. Don't move it.
		oi = new OI();

		gamepadRumbleUsingCameraCommand = new GamepadRumbleUsingCamera(); // must be created after OI
		//gamepadRumbleUsingCameraCommand.start();

		indicatorTimedScrollRainbow = new IndicatorTimedScrollRainbow(1);
		indicatorTimedScrollRainbow.start();
	} 

	/**
	 * This function is called every robot packet, no matter the mode. Use
	 * this for items like diagnostics that you want ran during disabled,
	 * autonomous, teleoperated and test.
	 *
	 * <p>This runs after the mode specific periodic functions, but before
	 * LiveWindow and SmartDashboard integrated updating.
	 */
	@Override
	public void robotPeriodic() {
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings and commands.
	 */
	@Override
	public void autonomousInit() {
		autonSelected = autonChooser.getSelected();
		System.out.println("Auton selected: " + autonSelected);	

		startPosition = startPositionChooser.getSelected();
		System.out.println("Start position: " + startPosition);

		mainTarget = mainTargetChooser.getSelected();
		System.out.println("Main target: " + mainTarget);
		
		cameraOption = cameraOptionChooser.getSelected();
		System.out.println("Camera option: " + cameraOption);
		
		sonarOption = sonarOptionChooser.getSelected();
		System.out.println("Sonar option: " + sonarOption);
		
		releaseSelected = releaseChooser.getSelected();
		System.out.println("Release chosen: " + releaseSelected);

		autonOption = autonOptionChooser.getSelected();
		System.out.println("Auton option: " + autonOption);
		
		//At this point we should know what auto run, where we started, and where our plates are located.
		//So we are ready for autonomousPeriodic to be called.
		updateToSmartDash();

		switch (autonSelected) {
			case Robot.AUTON_CUSTOM:
				m_autonomousCommand = new CustomAuton(startPosition, mainTarget, cameraOption, sonarOption, autonOption);
				break;

			case Robot.AUTON_DO_NOTHING:
				m_autonomousCommand = null;
			
				break;
				
			default:
				// nothing
				break;
		} // end switch

		camera.setLedMode(ICamera.LedMode.PIPELINE);
	
		// schedule the autonomous command (example)
		if (m_autonomousCommand != null) {
			m_autonomousCommand.start();
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();

		//camera.acquireTargets(false);
		updateToSmartDash();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
		}

		camera.setLedMode(ICamera.LedMode.PIPELINE);

		gamepadRumbleUsingCameraCommand.start();

		
		gearbox.setGear(Gearbox.Gear.LOW);
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		camera.acquireTargets(false);
		
		Scheduler.getInstance().run();
		
		camera.acquireTargets(false);

		colorSensor.updateColorSensor();

		updateToSmartDash();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
		gamepadRumbleUsingCameraCommand.cancel();

		camera.setLedMode(ICamera.LedMode.FORCE_OFF);
	}

	@Override
	public void disabledPeriodic() {	
		camera.acquireTargets(false);

		Scheduler.getInstance().run();
		
		camera.acquireTargets(false);

		colorSensor.updateColorSensor();

		updateToSmartDash();

		indicator.updateRainbow();
		
	}
	
	public void updateToSmartDash()
	{
		// Send Gyro val to Dashboard
		SmartDashboard.putNumber("Gyro Value", gyro.getAngle());
		SmartDashboard.putNumber("Pressure", pressureSensor.getPressurePSI());
			
		SmartDashboard.putNumber("Right Value", drivetrain.getRightPosition());
		SmartDashboard.putNumber("Left Value", drivetrain.getLeftPosition());
		SmartDashboard.putNumber("Right Enc Value", drivetrain.getRightEncoderPosition());
		SmartDashboard.putNumber("Left Enc Value", drivetrain.getLeftEncoderPosition());
		SmartDashboard.putNumber("Right Enc Velocity", drivetrain.getRightEncoderVelocity());
		SmartDashboard.putNumber("Left Enc Velocity", drivetrain.getLeftEncoderVelocity());
		SmartDashboard.putBoolean("isMoving?", drivetrain.isMoving());
		SmartDashboard.putBoolean("isTurning?", drivetrain.isTurning());
		SmartDashboard.putBoolean("isStalled?", drivetrain.isStalled());
		
		SmartDashboard.putBoolean("isCompromised?", DriverStation.getInstance().isDisabled());
		
		SmartDashboard.putNumber("Distance to Target", camera.getDistanceToCompositeTargetUsingVerticalFov());
		SmartDashboard.putNumber("Angle to Target", camera.getAngleToTurnToCompositeTarget());
		SmartDashboard.putNumber("Distance to Target Using Horizontal FOV", camera.getDistanceToCompositeTargetUsingHorizontalFov());
		
	
		
		SmartDashboard.putBoolean("Hinge Limit Switch", hingeControl.getLimitSwitchState());
		SmartDashboard.putBoolean("Hinge Forward Limit Switch", hingeControl.getForwardLimitSwitchState());
		SmartDashboard.putNumber("Hinge Position", hingeControl.getPosition());
		SmartDashboard.putNumber("Hinge Enc Position", hingeControl.getEncoderPosition());
		SmartDashboard.putBoolean("Hinge IsMoving?", hingeControl.isMoving());
		SmartDashboard.putNumber("Hinge Target", hingeControl.getTarget());
		SmartDashboard.putBoolean("Hinge isDown", hingeControl.isDown());
		SmartDashboard.putBoolean("Hinge isMidway", hingeControl.isMidway());
		SmartDashboard.putBoolean("Hinge isUp", hingeControl.isUp());
		
		SmartDashboard.putBoolean("Gyro Manually Calibrated?",hasGyroBeenManuallyCalibratedAtLeastOnce);
		
		SmartDashboard.putNumber("Tilt", accelerometer.getTilt());
		
		SmartDashboard.putNumber("Range to target", sonar.getRangeInInches());
		SmartDashboard.putNumber("Sonar Voltage", sonar.getVoltage()); 
		
		SmartDashboard.putBoolean("Grasper IsGrasping?", grasper.isGrasping());
		SmartDashboard.putBoolean("Grasper IsReleasing?", grasper.isReleasing());

		//SmartDashboard.putBoolean("Spinner IsSpinning?", spinnerWheel.isSpinning());
		
		SmartDashboard.putString("Auton selected", autonChooser.getSelected());	
		SmartDashboard.putString("Start position", startPositionChooser.getSelected());
		SmartDashboard.putString("Main target", mainTargetChooser.getSelected());
		SmartDashboard.putString("Camera option", cameraOptionChooser.getSelected());
		SmartDashboard.putString("Sonar option", sonarOptionChooser.getSelected());
		//SmartDashboard.putString("Release chosen", releaseChooser.getSelected());
		SmartDashboard.putString("Auton option", autonOptionChooser.getSelected());

		SmartDashboard.putNumber("Color Sensor Red", colorSensor.getRed());
		SmartDashboard.putNumber("Color Sensor Blue", colorSensor.getBlue());
		SmartDashboard.putNumber("Color Sensor Green", colorSensor.getGreen());
		SmartDashboard.putString("Color Detected", colorSensor.getDetectedColor().toString()); 

		SmartDashboard.putNumber("Winch Encoder Value", winchControl.getEncoderPosition());
		SmartDashboard.putBoolean("Winch Forward Limit Switch", winchControl.getForwardLimitSwitchState());

		SmartDashboard.putString("Gearbox Position", gearbox.getGear().toString());
	}

	public static void setGyroHasBeenManuallyCalibratedAtLeastOnce(boolean flag) {
		hasGyroBeenManuallyCalibratedAtLeastOnce = flag;
	}
}