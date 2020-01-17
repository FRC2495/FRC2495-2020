package frc.robot.sensors;

import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import edu.wpi.first.wpilibj.I2C;

public class ColorSensor {
    private ColorSensorV3 colSensor;
    private ColorMatch colMatch;
    private ColorMatchResult colResult;

    private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

    public String colorString = new String("");
    public Color detectedColor;
	
	public ColorSensor() {
        colSensor = new ColorSensorV3(I2C.Port.kOnboard);
        
        colMatch = new ColorMatch();

        colMatch.addColorMatch(kBlueTarget);
        colMatch.addColorMatch(kGreenTarget);
        colMatch.addColorMatch(kRedTarget);
        colMatch.addColorMatch(kYellowTarget);

        colMatch.setConfidenceThreshold(0.9);
    }
    
    public void updateColorSensor() {
        detectedColor = colSensor.getColor();
        colResult = colMatch.matchColor(detectedColor);

        
        if (colResult != null && colResult.color == kBlueTarget) {
            colorString = "Blue";
          } else if (colResult != null && colResult.color == kRedTarget) {
            colorString = "Red";
          } else if (colResult != null && colResult.color == kGreenTarget) {
            colorString = "Green";
          } else if (colResult != null && colResult.color == kYellowTarget) {
            colorString = "Yellow";
          } else {
            colorString = "Unknown";
          }
    }
	
	public double getRed(){
        return colSensor.getRed();
	}
	
	public double getGreen(){
        return colSensor.getGreen();
    }
    
    public double getBlue(){
        return colSensor.getBlue();
    }

    public String getDetectedColor(){
        return colorString;
    }

}