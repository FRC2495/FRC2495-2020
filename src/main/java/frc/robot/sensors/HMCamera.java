package frc.robot.sensors;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import frc.robot.interfaces.*;


public class HMCamera implements PIDSource, ICamera {
	private static final int BAD_INDEX = -1;
	
	NetworkTable nt;
	double[] area, width, height, centerX, centerY;
	int largeAIndex, largeBIndex = BAD_INDEX;

	public static final int HORIZONTAL_CAMERA_RES_PIXELS = 320;
	public static final int VERTICAL_CAMERA_RES_PIXELS = 240;
	private static final double VERTICAL_FOV_DEGREES = 47;
	private static final double HORIZONTAL_FOV_DEGREES = 56;
	private static final double TARGET_HEIGHT_INCHES = 5.5; // TODO set proper value
	private static final double TARGET_WIDTH_INCHES = 2; // TODO set proper value

	public static final double SAFE_DISTANCE_INCHES = 120;	

	public static final double MIN_OFFSET_CAMERA_TARGET_INCHES = 0;
	public static final double DEFAULT_OFFSET_CAMERA_TARGET_INCHES = 10; // we need to leave some space between the camera and the target
	public static final double MAX_OFFSET_CAMERA_TARGET_INCHES = 36;

	public static final double OFFSET_CAMERA_HATCH_INCHES = 10;
	public static final double OFFSET_CAMERA_PORT_INCHES = 24;

	private static final int MAX_NT_RETRY = 5;
	private static final double CAMERA_CATCHUP_DELAY_SECS = 0.250;

	private double offsetCameraTargetInches = DEFAULT_OFFSET_CAMERA_TARGET_INCHES;

	public HMCamera(String networktable) {
		// nt = NetworkTable.getTable(networktable);
		nt = NetworkTableInstance.getDefault().getTable(networktable);
	}

	private void setLocalTables(double[] area, double[] width, double[] height, double[] centerX, double[] centerY) {
		this.area = area;
		this.width = width;
		this.height = height;
		this.centerX = centerX;
		this.centerY = centerY;
	}

	private void updateFromNT() {
		double[] def = {}; // Return an empty array by default.
		int retry_count = 0;
		setLocalTables(null, null, null, null, null);
		largeAIndex = BAD_INDEX;
		largeBIndex = BAD_INDEX;

		// We cannot get arrays atomically but at least we can make sure they
		// have the same size
		do {
			// Get data from NetworkTable
			//setLocalTables(nt.getNumberArray("area", def), nt.getNumberArray("width", def),
			//		nt.getNumberArray("height", def), nt.getNumberArray("centerX", def),
			//		nt.getNumberArray("centerY", def));			
			setLocalTables(nt.getEntry("area").getDoubleArray(def), nt.getEntry("width").getDoubleArray(def),
					nt.getEntry("height").getDoubleArray(def), nt.getEntry("centerX").getDoubleArray(def),
					nt.getEntry("centerY").getDoubleArray(def));

			retry_count++;
		} while (!isCoherent() && retry_count < MAX_NT_RETRY);
	}

	private void processInformation() {
		double[] areaSave = area;
		if (areaSave.length >= 2) {
			largeAIndex = 0;
			largeBIndex = 0;
	        
	        //Checking first two elements of input array
	        if(areaSave[0] > areaSave[1])
	        {
	            //If first element is greater than second element
	            largeAIndex = 0;
	            largeBIndex = 1;
	        }
	        else
	        {
	            //If second element is greater than first element
	            largeAIndex = 1;
	            largeBIndex = 0;
	        }
	 
	        //Checking remaining elements of input array
	        for (int i = 2; i < areaSave.length; i++)
	        {
	            if(areaSave[i] > areaSave[largeAIndex])
	            {
	                //If element at 'i' is greater than 'firstLargest'
	                largeBIndex = largeAIndex;
	                largeAIndex = i;
	            }
	            else if (/*areaSave[i] < areaSave[largeAIndex] &&*/ areaSave[i] > areaSave[largeBIndex])
	            {
	                //If element at 'i' is smaller than 'firstLargest' and greater than 'secondLargest'
	                largeBIndex = i;
	            }
	        }
		}
	}

	public synchronized boolean isCoherent() {
		boolean result = (area != null && width != null && height != null && centerX != null && centerY != null
				&& area.length == width.length && area.length == height.length && area.length == centerX.length
				&& area.length == centerY.length);
		return result;
	}

	public synchronized int getNumberOfTargets() {
		if (isCoherent()) {
			int number = area.length;
			return number; // all tables have the same size so any length
								// can be used (might be zero)
		} else {
			//System.out.println("cannot get number of targets");
			return 0; // best answer in that case
		}
	}

	public synchronized boolean acquireTargets(boolean waitForNewInfo) {
		if (waitForNewInfo) {
			Timer.delay(CAMERA_CATCHUP_DELAY_SECS);
		}
		
		updateFromNT(); // gets the latest info

		if (isCoherent() && getNumberOfTargets() > 0) { // if we have targets
			processInformation();
			return true;
		} else {
			return false;
		}
	}

	public synchronized boolean checkForOpening() {
		return getNumberOfTargets() > 1; // opening is at least two targets
	}

	public synchronized double getDistanceToTargetAUsingVerticalFov() {
		if (isCoherent() && largeAIndex != BAD_INDEX) {
			double diagTargetDistance = TARGET_HEIGHT_INCHES * (VERTICAL_CAMERA_RES_PIXELS / height[largeAIndex]) / 2.0
					/ Math.tan(Math.toRadians(VERTICAL_FOV_DEGREES / 2));
			return diagTargetDistance;
		} else
			return Double.POSITIVE_INFINITY;
	}
	
	public synchronized double getDistanceToTargetAUsingHorizontalFov()
	{
		if (isCoherent() && largeAIndex != BAD_INDEX) {
			double diagTargetDistance = TARGET_WIDTH_INCHES * (HORIZONTAL_CAMERA_RES_PIXELS / width[largeAIndex]) / 2.0
					/ Math.tan(Math.toRadians(HORIZONTAL_FOV_DEGREES / 2));
			return diagTargetDistance;
		} else
			return Double.POSITIVE_INFINITY;
	}

	public synchronized double getDistanceToTargetBUsingVerticalFov() {
		if (isCoherent() && largeBIndex != BAD_INDEX) {
			double diagTargetDistance = TARGET_HEIGHT_INCHES * (VERTICAL_CAMERA_RES_PIXELS / height[largeBIndex]) / 2.0
					/ Math.tan(Math.toRadians(VERTICAL_FOV_DEGREES / 2));
			return diagTargetDistance;
		} else
			return Double.POSITIVE_INFINITY;
	}
	
	public synchronized double getDistanceToTargetBUsingHorizontalFov()
	{
		if (isCoherent() && largeBIndex != BAD_INDEX) {
			double diagTargetDistance = TARGET_WIDTH_INCHES * (HORIZONTAL_CAMERA_RES_PIXELS / width[largeBIndex]) / 2.0
					/ Math.tan(Math.toRadians(HORIZONTAL_FOV_DEGREES / 2));
			return diagTargetDistance;
		} else
			return Double.POSITIVE_INFINITY;
	}

	public synchronized double getAngleToTurnToTargetA() {
		if (isCoherent() && largeAIndex != BAD_INDEX) {
			double diff = (getCenterX()[largeAIndex] - (HORIZONTAL_CAMERA_RES_PIXELS / 2))
					/ HORIZONTAL_CAMERA_RES_PIXELS;
			double angle = diff * HORIZONTAL_FOV_DEGREES;
			return angle;
		} else
			return 0;
	}

	public synchronized double getAngleToTurnToTargetB() {
		if (isCoherent() && largeBIndex != BAD_INDEX) {
			double diff = (getCenterX()[largeBIndex] - (HORIZONTAL_CAMERA_RES_PIXELS / 2))
					/ HORIZONTAL_CAMERA_RES_PIXELS;
			double angle = diff * HORIZONTAL_FOV_DEGREES;
			return angle;
		} else
			return 0;
	}
	
	public synchronized double getDistanceToCompositeTargetUsingVerticalFov()
	{
		return (getDistanceToTargetAUsingVerticalFov() + getDistanceToTargetBUsingVerticalFov()) / 2;
	}
	
	public synchronized double getDistanceToCompositeTargetUsingHorizontalFov()
	{
		return ((getDistanceToTargetAUsingHorizontalFov() + getDistanceToTargetBUsingHorizontalFov()) /2);
	}
	
	public synchronized double getAngleToTurnToCompositeTarget()
	{
		return (getAngleToTurnToTargetA() + getAngleToTurnToTargetB()) / 2;
	}

	public synchronized double getPixelDisplacementToCenterToTargetA() {
		if (isCoherent() && largeAIndex != BAD_INDEX) {
			double diff = (getCenterX()[largeAIndex] - (HORIZONTAL_CAMERA_RES_PIXELS / 2));
			return diff;
		} else
			return 0;
	}

	public synchronized double getPixelDisplacementToCenterToTargetB() {
		if (isCoherent() && largeBIndex != BAD_INDEX) {
			double diff = (getCenterX()[largeBIndex] - (HORIZONTAL_CAMERA_RES_PIXELS / 2));
			return diff;
		} else
			return 0;
	}

	public synchronized double getPixelDisplacementToCenterToCompositeTarget()
	{
		return ((getPixelDisplacementToCenterToTargetA() + getPixelDisplacementToCenterToTargetB()) /2);
	}

	public synchronized double[] getArea() {
		return area;
	}

	public synchronized double[] getWidth() {
		return width;
	}

	public synchronized double[] getHeight() {
		return height;
	}

	public synchronized double[] getCenterX() {
		return centerX;
	}

	public synchronized double[] getCenterY() {
		return centerY;
	}
	
	public synchronized void setPIDSourceType(PIDSourceType pidSource)
	{
		// always displacement!
	}

	public synchronized PIDSourceType getPIDSourceType()
	{
		return PIDSourceType.kDisplacement;
	}
	
	// The PIDController that will call this method runs in a different thread than the scheduler thread,
	// so it is important that this class be made thread-safe. This is why all the public methods are synchronized.
	public synchronized double pidGet()
	{
		acquireTargets(false); // we don't want to wait but the lag might be problematic
		
		return -getPixelDisplacementToCenterToCompositeTarget(); // we are located at the opposite or the displacement we need to shift by
	}

	public synchronized void setPIDSource2Type(PIDSourceType pidSource)
	{
		// always displacement!
	}

	public synchronized PIDSourceType getPIDSource2Type()
	{
		return PIDSourceType.kDisplacement;
	}
	
	// The PIDController that will call this method runs in a different thread than the scheduler thread,
	// so it is important that this class be made thread-safe. This is why all the public methods are synchronized.
	public synchronized double pidGet2()
	{
		acquireTargets(false); // we don't want to wait but the lag might be problematic
		
		final double MAX_DISTANCE_TO_TARGET_INCHES = SAFE_DISTANCE_INCHES; // arbitrary very large distance
		
		//double distanceToTargetReportedByCamera = getDistanceToCompositeTargetUsingHorizontalFov();
		double distanceToTargetReportedByCamera = getDistanceToCompositeTargetUsingVerticalFov();

		double distance = 0;
		
		if (distanceToTargetReportedByCamera <= MAX_DISTANCE_TO_TARGET_INCHES) {
			if (distanceToTargetReportedByCamera >= offsetCameraTargetInches) {
				distance = distanceToTargetReportedByCamera - offsetCameraTargetInches;
			} else {
				System.out.println("WARNING: Already at the target!");
			}
		} else {
			System.out.println("ERROR: Cannot move to infinity and beyond!");
		}

		return -distance;
	}

	public synchronized void setOffsetBetweenCameraAndTarget(double offset) {
		offsetCameraTargetInches = Math.max(Math.min(offset,MAX_OFFSET_CAMERA_TARGET_INCHES),MIN_OFFSET_CAMERA_TARGET_INCHES);
	}

	public synchronized double getOffsetBetweenCameraAndTarget() {
		return offsetCameraTargetInches;
	}
}
