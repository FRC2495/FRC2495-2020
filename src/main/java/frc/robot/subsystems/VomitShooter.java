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

//import frc.robot.interfaces.*;
//import frc.robot.commands.*;
//import frc.robot.Robot;
//import frc.robot.sensors.Sonar;



public class VomitShooter extends Subsystem {

BaseMotorController shooterLeft, shooterRight;
Robot robot;

double wheelPower = -.95;

public VomitShooter(BaseMotorController ShooterLeft_in, BaseMotorController shooterRight_in, Robot robot_in) {
shooterLeft=ShooterLeft_in;
shooterRight=shooterRight_in;
robot=robot_in;

shooterRight.follow(shooterLeft);
shooterLeft.setInverted(true);
shooterRight.setInverted(false);
}

public void initDefaultCommand() {

}

public void revBeforeFire() {
    shooterLeft.set(ControlMode.PercentOutput, wheelPower);
}

public void stop() {
    shooterLeft.set(ControlMode.PercentOutput, 0);
}

@Override
public void periodic() {
    // Put code here to be run every loop

}

// for debug purpose only
public void joystickControl(Joystick joystick)
{

}
}
