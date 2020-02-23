package frc.robot.sensors;

import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;

import edu.wpi.first.wpilibj.I2C;

// We're determining the colors from the spinny thing
public class ColorSensor {
    private ColorSensorV3 colSensor;
    private ColorMatch colMatch;
    private ColorMatchResult colResult;

    private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);
    // They are creating the 4 color values that are our target

    public DetectedColor savedCol = null;
    public Color detectedColor;

    public enum DetectedColor {
		UNKNOWN,
		BLUE,
		GREEN,
		RED,
		YELLOW
	}
	
	public ColorSensor() {
        colSensor = new ColorSensorV3(I2C.Port.kOnboard); // Creating a new color sensor on the onboard I2C Port
        
        colMatch = new ColorMatch();

        colMatch.addColorMatch(kBlueTarget);
        colMatch.addColorMatch(kGreenTarget);
        colMatch.addColorMatch(kRedTarget);
        colMatch.addColorMatch(kYellowTarget);
        // Adding color targets to our color matcher

        colMatch.setConfidenceThreshold(0.9);
        // How accurately the color we're reading is relating to the target color
    }
	
	// Everytime this method is color is called check the current color against color target
    public void updateColorSensor() {
        detectedColor = colSensor.getColor(); // We're saving our current color that our sensor is seeing
        colResult = colMatch.matchColor(detectedColor); // Checking the saved color against the target colors and saving the result

        // Checking to see if our saved result is equal to any of ur target collors and if so, saving thta color isa string 
        if (colResult != null && colResult.color == kBlueTarget) {
            savedCol = DetectedColor.BLUE;
        } else if (colResult != null && colResult.color == kRedTarget) {
            savedCol = DetectedColor.RED;
        } else if (colResult != null && colResult.color == kGreenTarget) {
            savedCol = DetectedColor.GREEN;
        } else if (colResult != null && colResult.color == kYellowTarget) {
            savedCol = DetectedColor.YELLOW;
        } else {
            savedCol = DetectedColor.UNKNOWN;
        }
    }
	
	public double getRed() {
        return colSensor.getRed();
	}
	
	public double getGreen() {
        return colSensor.getGreen();
    }
    
    public double getBlue() {
        return colSensor.getBlue();
    }
    // We are returning the saved result color
    public DetectedColor getDetectedColor() {
        return savedCol;
    }

}