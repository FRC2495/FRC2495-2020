/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.sensors;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

/**
 * Add your docs here.
 */
public class WS2812 {
    private AddressableLED led;
    private AddressableLEDBuffer ledBuffer;

    // Store what the last hue of the first pixel is
    private int rainbowFirstPixelHue;
    
    public WS2812() 
    {
        led = new AddressableLED(9); // TODO use constant

        // Reuse buffer
        // Default to a length of 60, start empty output
        // Length is expensive to set, so only set it once, then just update data
        ledBuffer = new AddressableLEDBuffer(60);

        led.setLength(ledBuffer.getLength());

        // Set the data
        led.setData(ledBuffer);

        led.start();
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

}
