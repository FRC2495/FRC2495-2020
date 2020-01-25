/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.interfaces.ICamera;

import edu.wpi.first.wpilibj.command.Subsystem;

// see https://docs.wpilib.org/en/latest/docs/software/actuators/addressable-leds.html

/**
 * Add your docs here.
 */
public class WS2812 extends Subsystem {
	private AddressableLED led;
	private AddressableLEDBuffer ledBuffer;

	// Store what the last hue of the first pixel is
	private int rainbowFirstPixelHue;
	
	public WS2812() 
	{
		led = new AddressableLED(9); // TODO use constant to specify PWM port

		// Reuse buffer
		// Default to a length of 60, start empty output
		// Length is expensive to set, so only set it once, then just update data
		ledBuffer = new AddressableLEDBuffer(60);

		led.setLength(ledBuffer.getLength());

		// Set the data
		led.setData(ledBuffer);

		led.start();
	}

	@Override
	public void initDefaultCommand() {  
		// Set the default command for a subsystem here.
		// setDefaultCommand(new MySpecialCommand());
	}

	@Override
	public void periodic() {
		// Put code here to be run every loop
	}

	public void updateRainbow()
	{
		// For every pixel
		for (var i = 0; i < ledBuffer.getLength(); i++) {

			// Calculate the hue - hue is easier for rainbows because the color
			// shape is a circle so only one value needs to precess
			final var hue = (rainbowFirstPixelHue + (i * 180 / ledBuffer.getLength())) % 180;

			// Set the value
			ledBuffer.setHSV(i, hue, 255, 128);
		}

		// Increase by to make the rainbow "move"
		rainbowFirstPixelHue += 3;

		// Check bounds
		rainbowFirstPixelHue %= 180; 

		// Set the LEDs
		led.setData(ledBuffer);    
	}

	public void updateHue(int hue)
	{
		// For every pixel
		for (var i = 0; i < ledBuffer.getLength(); i++) {

			// Set the value
			ledBuffer.setHSV(i, hue, 255, 128);
		}

		// Set the LEDs
		led.setData(ledBuffer);    
	}

	public void updateRed()
	{
		updateHue(0);
	}

	public void updateYellow()
	{
		updateHue(30);
	}

	public void updateGreen()
	{
		updateHue(60);
	}

	public void updateBlue()
	{
		updateHue(120);
	}

	public void updateFromCamera(ICamera camera_in) {
		ICamera camera = camera_in;

		if (Math.abs(camera.getAngleToTurnToCompositeTarget()) < 5) { //Displays Green if in target
			updateGreen();
		}
		else if (Math.abs(camera.getAngleToTurnToCompositeTarget()) < 10) { //displays yellow if close to target
			updateYellow();
		}
		else { // displays red if far from target 
			updateRed();
		}
	}
}
