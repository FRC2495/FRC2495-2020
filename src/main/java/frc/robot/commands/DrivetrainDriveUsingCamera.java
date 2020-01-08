/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Drivetrain;

public class DrivetrainDriveUsingCamera extends Command {

	private double offsetCameraTarget;
	private int onTargetCountTurningUsingCamera; // counter indicating how many times/iterations we were on target
	private int onTargetCountMovingUsingCamera; // counter indicating how many times/iterations we were on target
	private int stalledCount;

	// offset should be LimelightCamera.OFFSET_CAMERA_PORT_INCHES or LimelightCamera.OFFSET_CAMERA_HATCH_INCHES
	public DrivetrainDriveUsingCamera(double offset) {

		offsetCameraTarget = offset;

		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.drivetrain);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		System.out.println("DrivetrainDriveUsingCamera: initialize");

		Robot.camera.setOffsetBetweenCameraAndTarget(offsetCameraTarget);

		onTargetCountTurningUsingCamera = 0;
		onTargetCountMovingUsingCamera = 0;
		stalledCount = 0;
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		// note: it is assumed that pidGet/Get2 return up-to-date information
		double pixelDisplacement = Robot.camera.pidGet(); // gets the pixel displacement *FROM*/compared to the target (error to use to turn/steer)
		double distance = Robot.camera.pidGet2(); // gets the distance/position *FROM*/compared to the target (error to use to move)

		double turnPctOutput = -pixelDisplacement * Drivetrain.TURN_USING_CAMERA_PROPORTIONAL_GAIN * 4; // TODO double-check sign
		double movePctOutput = +distance * Drivetrain.MOVE_USING_CAMERA_PROPORTIONAL_GAIN * 2; // TODO double-check sign

		if (Math.abs(pixelDisplacement) < Drivetrain.PIXEL_THRESHOLD)
		{
			turnPctOutput = 0;
			onTargetCountTurningUsingCamera++; // we increase the counter
		}
		else
		{
			onTargetCountTurningUsingCamera = 0; // we reset the counter as we are not on target anymore
		}

		if (Math.abs(turnPctOutput) < Drivetrain.MIN_TURN_USING_CAMERA_PCT_OUTPUT)
		{
			turnPctOutput = Math.signum(turnPctOutput) * Drivetrain.MIN_TURN_USING_CAMERA_PCT_OUTPUT;
		}

		if (Math.abs(distance) < Drivetrain.DISTANCE_THRESHOLD_INCHES)
		{
			movePctOutput = 0;
			onTargetCountMovingUsingCamera++; // we increase the counter
		}
		else
		{
			onTargetCountMovingUsingCamera = 0; // we reset the counter as we are not on target anymore
		}

		if (Math.abs(movePctOutput) < Drivetrain.MIN_MOVE_USING_CAMERA_PCT_OUTPUT)
		{
			movePctOutput = Math.signum(movePctOutput) * Drivetrain.MIN_MOVE_USING_CAMERA_PCT_OUTPUT;
		}

		Robot.drivetrain.arcadeDrive(movePctOutput, turnPctOutput);
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {

		double rvelocity = Robot.drivetrain.getRightEncoderVelocity();
		double lvelocity = Robot.drivetrain.getLeftEncoderVelocity();
		
		boolean isStalled = (Math.abs(rvelocity) < Drivetrain.TICK_PER_100MS_THRESH
			&& Math.abs(lvelocity) < Drivetrain.TICK_PER_100MS_THRESH);
		
		if (isStalled) { // if we are stalled in this iteration 
			stalledCount++; // we increase the counter
		} else { // if we are not stalled in this iteration
				stalledCount = 0; // we reset the counter as we are not stalled anymore
		}

		return (onTargetCountTurningUsingCamera > Drivetrain.TURN_USING_CAMERA_ON_TARGET_MINIMUM_COUNT
			&& onTargetCountMovingUsingCamera > Drivetrain.MOVE_USING_CAMERA_ON_TARGET_MINIMUM_COUNT)
		|| (stalledCount > Drivetrain.TURN_USING_CAMERA_STALLED_MINIMUM_COUNT
			&& stalledCount > Drivetrain.MOVE_USING_CAMERA_STALLED_MINIMUM_COUNT);
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		System.out.println("DrivetrainDriveUsingCamera: end");
		Robot.drivetrain.stop(); // to reset the state machine
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		System.out.println("DrivetrainDriveUsingCamera: interrupted");
		end();
	}
}
