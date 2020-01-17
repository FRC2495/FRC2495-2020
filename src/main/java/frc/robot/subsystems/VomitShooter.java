/**
 * 
 */
package frc.robot.subsystems;

//import com.ctre.phoenix.motorcontrol.ControlMode;
//import com.ctre.phoenix.motorcontrol.NeutralMode;
//import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import frc.robot.Robot;
import frc.robot.commands.VomitShooterStop;


//import frc.robot.interfaces.*;
//import frc.robot.commands.*;
//import frc.robot.Robot;
//import frc.robot.sensors.Sonar;


//Controls the shooter motors to shoot the balls 
public class VomitShooter extends Subsystem {

    BaseMotorController shooterLeft, shooterRight;
    Robot robot;

    static final double WHEEL_POWER = -.95;

//The shooter left and shooter right are motors 
    public VomitShooter(BaseMotorController ShooterLeft_in, BaseMotorController shooterRight_in, Robot robot_in) {
        shooterLeft=ShooterLeft_in;
        shooterRight=shooterRight_in;
        robot=robot_in;

        shooterLeft.configFactoryDefault();
        shooterRight.configFactoryDefault();
        
        // Mode of operation during Neutral output may be set by using the setNeutralMode() function.
		// As of right now, there are two options when setting the neutral mode of a motor controller,
		// brake and coast.
		shooterLeft.setNeutralMode(NeutralMode.Coast);
		shooterRight.setNeutralMode(NeutralMode.Coast);

        // Motor controller output direction can be set by calling the setInverted() function as seen below.
		// Note: Regardless of invert value, the LEDs will blink green when positive output is requested (by robot code or firmware closed loop).
		// Only the motor leads are inverted. This feature ensures that sensor phase and limit switches will properly match the LED pattern
		// (when LEDs are green => forward limit switch and soft limits are being checked).
        shooterLeft.setInverted(true);
        shooterRight.setInverted(false);

        // Both the Talon SRX and Victor SPX have a follower feature that allows the motor controllers to mimic another motor controller's output.
		// Users will still need to set the motor controller's direction, and neutral mode.
		// The method follow() allows users to create a motor controller follower of not only the same model, but also other models
        // , talon to talon, victor to victor, talon to victor, and victor to talon.
        shooterRight.follow(shooterLeft);
    }

    public void initDefaultCommand() {
        setDefaultCommand(new VomitShooterStop()); //Makes the shooter stop when it has no other command available 
    }

    public void revBeforeFire() { //Starts spinning the wheels before shooting the ball
        shooterLeft.set(ControlMode.PercentOutput, WHEEL_POWER);
    }

    public void stop() { //Stops both of the wheels from spinning 
        shooterLeft.set(ControlMode.PercentOutput, 0);
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop

    }

}
