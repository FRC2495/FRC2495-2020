package frc.robot.util;

import edu.wpi.first.wpilibj.DriverStation;

import frc.robot.interfaces.*;


public class GameData {
	
	public enum SpecifiedColor {
		UNKNOWN,
		BLUE,
		GREEN,
		RED,
		YELLOW
	}

	private String gameData;
	
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

}
