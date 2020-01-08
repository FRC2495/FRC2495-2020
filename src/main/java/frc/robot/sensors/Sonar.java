package frc.robot.sensors;

import edu.wpi.first.wpilibj.*;

import frc.robot.Robot;


public class Sonar{
	
	private final double IN_TO_CM_CONVERSION = 2.54;
	
	private boolean use_units;    //Are we using units or just returning voltage?
	private double min_voltage;	  //Minimum voltage the ultrasonic sensor can return
	private double voltage_range; //The range of the voltages returned by the sensor (maximum - minimum)
	private double min_distance;  //Minimum distance the ultrasonic sensor can return in inches
	private double distance_range;//The range of the distances returned by this class in inches (maximum - minimum)
	private AnalogInput channel;
	
	//constructor
	public Sonar(int _channel) {
		channel = new AnalogInput(_channel);
		//default values
		use_units = true;
		
		if (Robot.COMPETITION_BOT_CONFIG) {
			min_voltage = .25;
			voltage_range = 5.0 - min_voltage;
			min_distance = 10.0; //accurate anywhere over 10in - maxdistance 
			distance_range = 360 - min_distance;
		} else { // practice bot
			min_voltage = .10; // TODO CALIBRATE THIS VALUE
			voltage_range = 5.0 - min_voltage;
			min_distance = 10.0; // TODO CALIBRATE THIS VALUE 
			distance_range = 360 - min_distance;
		}
	}
	
	//constructor
	public Sonar(int _channel, boolean _use_units, double _min_voltage,
			double _max_voltage, double _min_distance, double _max_distance) {
		channel = new AnalogInput(_channel);
		
		//only use unit-specific variables if we're using units
		if (_use_units) {
			use_units = true;
			min_voltage = _min_voltage;
			voltage_range = _max_voltage - _min_voltage;
			min_distance = _min_distance;
			distance_range = _max_distance - _min_distance;
		}
	}
	
	// Just get the voltage.
	public double getVoltage() {
		return channel.getVoltage();
	}
	
	/* getRangeInInches
	 * Returns the range in inches
	 * Returns -1.0 if units are not being used
	 * Returns -2.0 if the voltage is below the minimum voltage
	 */
	public double getRangeInInches() {
		double range;
		
		//if we're not using units, return -1, a range that will most likely never be returned
		if (!use_units) {
			return -1.0;
		}
		
		range = channel.getVoltage();
		
		if (range < min_voltage) {
			return -2.0;
		}
		
		//first, normalize the voltage
		range = (range - min_voltage) / voltage_range;
		//next, denormalize to the unit range
		range = (range * distance_range) + min_distance;
		
		return range;
	}
	
	/* getRangeInCM
	 * Returns the range in centimeters
	 * Returns -1.0 if units are not being used
	 * Returns -2.0 if the voltage is below the minimum voltage
	 */
	public double getRangeInCM() {
		double range;
		
		//if we're not using units, return -1, a range that will most likely never be returned
		if (!use_units) {
			return -1.0;
		}
		
		range = channel.getVoltage();
		
		if (range < min_voltage) {
			return -2.0;
		}
		
		//first, normalize the voltage
		range = (range - min_voltage) / voltage_range;
		//next, denormalize to the unit range
		range = (range * distance_range) + min_distance;
		//finally, convert to centimeters
		range *= IN_TO_CM_CONVERSION;
		
		return range;
	}
}