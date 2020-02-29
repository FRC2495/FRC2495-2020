package frc.robot.util;

import edu.wpi.first.wpilibj.DriverStation;

//import frc.robot.interfaces.*;
import frc.robot.sensors.ColorSensor;
import frc.robot.sensors.ColorSensor.DetectedColor;


public class GameData {
	
	public enum SpecifiedColor {
		UNKNOWN,
		BLUE,
		GREEN,
		RED,
		YELLOW
	}

	private String gameData;
	private ColorSensor colSensor;
	
	// this method needs to be called to retrieve the data once available
	public void update() {
		DriverStation driverStation = DriverStation.getInstance();
		
		if (driverStation != null) {
			gameData = driverStation.getGameSpecificMessage();
		} else {
			gameData = null;
		}
	}
	
	public SpecifiedColor getSpecifiedColor() {
		
		if (gameData != null && gameData.length() >= 1) {
			if (gameData.charAt(0) == 'B') {
				return SpecifiedColor.BLUE;
			} else if (gameData.charAt(0) == 'R') {
				return SpecifiedColor.RED;
			} else if (gameData.charAt(0) == 'G') {
				return SpecifiedColor.GREEN;
			} else if (gameData.charAt(0) == 'Y') {
				return SpecifiedColor.YELLOW;
			}
		}
		
		return SpecifiedColor.UNKNOWN;
	}

	public boolean matchSpecificedWithDetectedColor(ColorSensor colSensor_in){
		// To check if the field that both us and the field are reading the right color
		colSensor = colSensor_in;

		colSensor.updateColorSensor(); // just in case nobody forced update already
		DetectedColor detectedColor = colSensor.getDetectedColor(); //color we are seeing

		update();
		SpecifiedColor specifiedColor = getSpecifiedColor();

		if (specifiedColor == SpecifiedColor.UNKNOWN || detectedColor == DetectedColor.UNKNOWN){
			return false;
		}
		else if (specifiedColor == SpecifiedColor.RED && detectedColor == DetectedColor.BLUE){
			return true;
		}
		else if (specifiedColor == SpecifiedColor.BLUE && detectedColor == DetectedColor.RED){
			return true;
		}
		else if (specifiedColor == SpecifiedColor.GREEN && detectedColor == DetectedColor.YELLOW){
			return true;
		}
		else if (specifiedColor == SpecifiedColor.YELLOW && detectedColor == DetectedColor.GREEN){
			return true;
		}
		else {
			return false;
		}
		
	}


	public DetectedColor convertSpecifiedColorToColorToDetect(){

		update();
		SpecifiedColor specifiedColor = getSpecifiedColor();

		if (specifiedColor == SpecifiedColor.UNKNOWN){
			return DetectedColor.UNKNOWN;
		}
		else if (specifiedColor == SpecifiedColor.RED){
			return DetectedColor.BLUE;
		}
		else if (specifiedColor == SpecifiedColor.BLUE){
			return DetectedColor.RED;
		}
		else if (specifiedColor == SpecifiedColor.GREEN){
			return DetectedColor.YELLOW;
		}
		else if (specifiedColor == SpecifiedColor.YELLOW){
			return DetectedColor.GREEN;
		}
		else {
			return DetectedColor.UNKNOWN;
		}
		
	}
}
