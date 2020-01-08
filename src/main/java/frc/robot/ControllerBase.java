/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 * The {@code ControllerBase} class handles all input coming from the 
 * gamepad, left joystick, and right joystick. This has various methods
 * to get input, buttons, etc.
 */
public class ControllerBase {
	private Joystick[] joysticks;
	
	private boolean[][] btn;
	private boolean[][] btnPrev;

	public static final int MAX_NUMBER_CONTROLLERS = 3;
	public static final int MAX_NUMBER_BUTTONS = 11;
		
	private double[] gamepadAxes;
	private double[] gamepadAxesPrev;
	
	public static final int MAX_NUMBER_GAMEPAD_AXES = 6;
	public static final double GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD = 0.5;
	
	/**
	 * The {@code GamepadButtons} class contains all the button bindings for the
	 * Gamepad.
	 */
	public static class GamepadButtons {
		public static final int
			A = 1,
			B = 2,
			X = 3,
			Y = 4,
			LB = 5,
			RB = 6,
			BACK = 7,
			START = 8,
			LS = 9,
			RS = 10;
		/**
		 * <pre>
		 * private GamepadButtons()
		 * </pre>
		 * 
		 * Unused constructor.
		 */
		private GamepadButtons() {
			
		}
	}
	
	// The purpose of this class is to consider gamepad axes as virtual buttons
	// (especially the left and right triggers).
	// and it works!
	public static class GamepadAxes {
		public static final int
			LX = 0,
			LY = 1,
			LT = 2,
			RT = 3,
			RX = 4,
			RY = 5;
		
		/**
		 * <pre>
		 * private GamepadButtons()
		 * </pre>
		 * 
		 * Unused constructor.
		 */
		private GamepadAxes() {
			
		}
	}
	
	/**
	 * The {@code JoystickButtons} class contains all the button bindings for the
	 * Joysticks.
	 */
	public static class JoystickButtons {
		// Well this defeats the purpose of constants, doesn't it?
		public static final int
			BTN1 = 1,
			BTN2 = 2,
			BTN3 = 3,
			BTN4 = 4,
			BTN5 = 5,
			BTN6 = 6,
			BTN7 = 7,
			BTN8 = 8,
			BTN9 = 9,
			BTN10 = 10,
			BTN11 = 11;
		
		/**
		 * <pre>
		 * private JoystickButtons()
		 * </pre>
		 * 
		 * Unused constructor.
		 */
		private JoystickButtons() { }
	}
	
	/**
	 * The {@code Joysticks} enum contains 
	 * namespaces for the gamepad, left joystick, and right joystick
	 */
	public enum Joysticks {
		GAMEPAD,	// 0
		LEFT_STICK,	// 1
		RIGHT_STICK	// 2
	}
	
	/**
	 * <pre>
	 * public ControllerBase(Joystick gamepad,
	 *                       Joystick leftStick,
	 *                       Joystick rightStick)
	 * </pre>
	 * Constructs a new {@code ControllerBase} with the specified {@code Joysticks}
	 * for the gamepad, left joystick, and right joystick.
	 * @param gamepad    the {@code Joystick} to use for the gamepad.
	 * @param leftStick  the {@code Joystick} to use for the left joystick.
	 * @param rightStick the {@code Joystick} to use for the right joystick.
	 */
	public ControllerBase(Joystick gamepad, Joystick leftStick, Joystick rightStick) {		
		btn = new boolean[ControllerBase.MAX_NUMBER_CONTROLLERS][ControllerBase.MAX_NUMBER_BUTTONS+1];
		btnPrev = new boolean[ControllerBase.MAX_NUMBER_CONTROLLERS][ControllerBase.MAX_NUMBER_BUTTONS+1];
		
		// CAUTION: joysticks are indexed according to order defined in Joysticks enum
		// Therefore changes in Joysticks enum need to be reflected here...
		joysticks = new Joystick[]{gamepad, leftStick, rightStick};
		
		gamepadAxes = new double[ControllerBase.MAX_NUMBER_GAMEPAD_AXES];
		gamepadAxesPrev = new double[ControllerBase.MAX_NUMBER_GAMEPAD_AXES];
	}
	
	/**
	 * <pre>
	 * public void update()
	 * </pre>
	 * Updates the {@code btn} and {@code btnPrev} arrays.
	 */
	public void update() {
		//Dealing with buttons on the different joysticks
		for (int i = 0; i < ControllerBase.MAX_NUMBER_CONTROLLERS; i++) {
			for (int j = 1; j < ControllerBase.MAX_NUMBER_BUTTONS+1; j++) {
				btnPrev[i][j] = btn[i][j];
			}
		}

		for (int i = 0; i < ControllerBase.MAX_NUMBER_CONTROLLERS; i++) {
			for (int j = 1; j < ControllerBase.MAX_NUMBER_BUTTONS+1; j++) {
				// the gamepad has only 10 buttons, so we don't read button 11 to avoid getting a warning...
				if (!(i == Joysticks.GAMEPAD.ordinal() && j == ControllerBase.MAX_NUMBER_BUTTONS)) {
					btn[i][j] = joysticks[i].getRawButton(j);
				}
			}
		}
		
		for (int j = 0; j < ControllerBase.MAX_NUMBER_GAMEPAD_AXES; j++) {
			gamepadAxesPrev[j] = gamepadAxes[j];
		}
		
		for (int j = 0; j < ControllerBase.MAX_NUMBER_GAMEPAD_AXES; j++) {
			gamepadAxes[j] = joysticks[Joysticks.GAMEPAD.ordinal()].getRawAxis(j);
		}
	}	

	/**
	 * <pre>
	 * public boolean getPressedDown(Joysticks contNum, 
	 *                               int buttonNum)
	 * </pre>
	 * Gets whether or not a button from the specified {@code Joystick} is pressed.
	 * @param contNum the {@code Joystick} to check the button from
	 * @param buttonNum the index of the button to test
	 * @return true if the button on the specified {@code Joystick} is pressed,
	 *         false otherwise
	 */
	public boolean getPressedDown(Joysticks contNum, int buttonNum) {
		return btn[contNum.ordinal()][buttonNum] && !btnPrev[contNum.ordinal()][buttonNum]; 
	}	
	
	public boolean getHeld(Joysticks contNum, int buttonNum)
	{
		return btn[contNum.ordinal()][buttonNum];
	}
	
	/**
	 * <pre>
	 * public boolean getReleased(Joysticks contNum, 
	 *                               int buttonNum)
	 * </pre>
	 * Gets whether or not a button from the specified {@code Joystick} was released.
	 * @param contNum the {@code Joystick} to check the button from
	 * @param buttonNum the index of the button to test
	 * @return true if the button on the specified {@code Joystick} was released,
	 *         false otherwise
	 */
	public boolean getReleased(Joysticks contNum, int buttonNum){
		return !btn[contNum.ordinal()][buttonNum] && btnPrev[contNum.ordinal()][buttonNum];
	}
	
	
	public boolean getGamepadAxisPressedDown(int axisNum, boolean polarity) {
		
		if (polarity) {
			return (Math.max(gamepadAxes[axisNum],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD) &&
				!(Math.max(gamepadAxesPrev[axisNum],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		} else  {
			return (-Math.min(gamepadAxes[axisNum],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD) &&
				!(-Math.min(gamepadAxesPrev[axisNum],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
	}	
	
	public boolean getGamepadAxisHeld(int axisNum, boolean polarity)
	{
		if (polarity) {
			return (Math.max(gamepadAxes[axisNum],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);				
		} else  {
			return (-Math.min(gamepadAxes[axisNum],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
	}
	
	public boolean getGamePadAxisReleased(int axisNum, boolean polarity){
		
		if (polarity) {
			return !(Math.max(gamepadAxes[axisNum],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD) &&
				(Math.max(gamepadAxesPrev[axisNum],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		} else  {
			return !(-Math.min(gamepadAxes[axisNum],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD) &&
				(-Math.min(gamepadAxesPrev[axisNum],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
	}
	
	/**
	 * <pre>
	 * public void rumble(boolean rumble)
	 * </pre>
	 * Rumbles the gamepad
	 * 
	 * @param rumble whether or not to set the rumble on
	 */
	public void rumble(boolean rumble) {
		if (rumble) {
			joysticks[Joysticks.GAMEPAD.ordinal()].setRumble(Joystick.RumbleType.kLeftRumble, 1);
			joysticks[Joysticks.GAMEPAD.ordinal()].setRumble(Joystick.RumbleType.kRightRumble, 1);
		} else {
			joysticks[Joysticks.GAMEPAD.ordinal()].setRumble(Joystick.RumbleType.kLeftRumble, 0);
			joysticks[Joysticks.GAMEPAD.ordinal()].setRumble(Joystick.RumbleType.kRightRumble, 0);
		}		
	}

	/**
	 * <pre>
	 * public void rumble(boolean rumble, Joystick gamepad)
	 * </pre>
	 * Rumbles the gamepad
	 * 
	 * @param rumble whether or not to set the rumble on
	 * @param gamepad reference to gamepad
	 */
	public static void rumble(boolean rumble, Joystick gamepad) {
		if (rumble) {
			gamepad.setRumble(Joystick.RumbleType.kLeftRumble, 1);
			gamepad.setRumble(Joystick.RumbleType.kRightRumble, 1);
		} else {
			gamepad.setRumble(Joystick.RumbleType.kLeftRumble, 0);
			gamepad.setRumble(Joystick.RumbleType.kRightRumble, 0);
		}		
	}
}
