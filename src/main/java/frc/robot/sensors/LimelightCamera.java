package frc.robot.sensors;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import frc.robot.interfaces.*;


public class LimelightCamera implements PIDSource, ICamera {
	private static final int BAD_INDEX = -1;
	
	NetworkTable nt;
	double[] area, width, height, centerX, centerY;
	int largeIndex = BAD_INDEX;

	public static final int HORIZONTAL_CAMERA_RES_PIXELS = 320;
	private static final int VERTICAL_CAMERA_RES_PIXELS = 240;
	private static final double VERTICAL_FOV_DEGREES = 45.7; // see https://www.andymark.com/products/limelight2
	private static final double HORIZONTAL_FOV_DEGREES = 59.6; 
	private static final double TARGET_HEIGHT_INCHES = 5.5; // TODO set proper value PROPER VALUE IS 5.75 inches
	private static final double TARGET_WIDTH_INCHES = 14.5;

	public static final double SAFE_DISTANCE_INCHES = 240;

	public static final double MIN_OFFSET_CAMERA_TARGET_INCHES = 0;
	public static final double DEFAULT_OFFSET_CAMERA_TARGET_INCHES = 10; // we need to leave some space between the camera and the target
	public static final double MAX_OFFSET_CAMERA_TARGET_INCHES = 36;

	public static final double OFFSET_CAMERA_HATCH_INCHES = 10;
	public static final double OFFSET_CAMERA_PORT_INCHES = 24;

	private static final int MAX_NT_RETRY = 5;
	private static final double CAMERA_CATCHUP_DELAY_SECS = 0.50;

	private double offsetCameraTargetInches = DEFAULT_OFFSET_CAMERA_TARGET_INCHES;

	public LimelightCamera() {
		nt = NetworkTableInstance.getDefault().getTable("limelight");
	}

	private void setLocalTables(double[] area, double[] width, double[] height, double[] centerX, double[] centerY) {
		this.area = area;
		this.width = width;
		this.height = height;
		this.centerX = centerX;
		this.centerY = centerY;
	}
	
	private void updateFromNT() {
		double def = 0.0; // 0.0 by default
		int retry_count = 0;
		setLocalTables(null, null, null, null, null);
		largeIndex = BAD_INDEX;

		// We cannot get arrays atomically but at least we can make sure they
		// have the same size
		do {
			// Get data from NetworkTable
			double ta = nt.getEntry("ta").getDouble(def);
			double thor = nt.getEntry("thor").getDouble(def);
			double tvert = nt.getEntry("tvert").getDouble(def);
			double tx = nt.getEntry("tx").getDouble(def);
			double ty = nt.getEntry("ty").getDouble(def);
			double tv = nt.getEntry("tv").getDouble(def);

			if (tv != 0.0) {
				double[] area = {ta};
				double[] width = {thor};
				double[] height = {tvert};
				double[] centerX = {tx * HORIZONTAL_CAMERA_RES_PIXELS / HORIZONTAL_FOV_DEGREES + (HORIZONTAL_CAMERA_RES_PIXELS / 2)}; // to use same units as HMCamera
				double[] centerY = {ty * VERTICAL_CAMERA_RES_PIXELS / VERTICAL_FOV_DEGREES + (VERTICAL_CAMERA_RES_PIXELS / 2)}; // to use same units as HMCamera
				
				setLocalTables(area,width,height,centerX,centerY);
			} else {
				double[] empty = {}; // empty array

				setLocalTables(empty,empty,empty,empty,empty);
			}		

			retry_count++;
		} while (!isCoherent() && retry_count < MAX_NT_RETRY);
	}

	private void processInformation() {
		double[] areaSave = area;
		if (areaSave.length >= 1) {
			largeIndex = 0;
	 
			//Checking remaining elements of input array
			for (int i = 1; i < areaSave.length; i++)
			{
				if(areaSave[i] > areaSave[largeIndex])
				{
					largeIndex = i;
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
		return getNumberOfTargets() > 0; // opening is at least one combined target
	}

	public synchronized double getDistanceToCompositeTargetUsingVerticalFov() {
		if (isCoherent() && largeIndex != BAD_INDEX) {
			double diagTargetDistance = TARGET_HEIGHT_INCHES * (VERTICAL_CAMERA_RES_PIXELS / height[largeIndex]) / 2.0
					/ Math.tan(Math.toRadians(VERTICAL_FOV_DEGREES / 2));
			return diagTargetDistance;
		} else
			return Double.POSITIVE_INFINITY;
	}
	
	public synchronized double getDistanceToCompositeTargetUsingHorizontalFov()
	{
		if (isCoherent() && largeIndex != BAD_INDEX) {
			double diagTargetDistance = TARGET_WIDTH_INCHES * (HORIZONTAL_CAMERA_RES_PIXELS / width[largeIndex]) / 2.0
					/ Math.tan(Math.toRadians(HORIZONTAL_FOV_DEGREES / 2));
			return diagTargetDistance;
		} else
			return Double.POSITIVE_INFINITY;
	}

	public synchronized double getAngleToTurnToCompositeTarget() {
		if (isCoherent() && largeIndex != BAD_INDEX) {
			double diff = (getCenterX()[largeIndex] - (HORIZONTAL_CAMERA_RES_PIXELS / 2))
					/ HORIZONTAL_CAMERA_RES_PIXELS;
			double angle = diff * HORIZONTAL_FOV_DEGREES;
			return angle;
		} else
			return 0;
	}
	
	public synchronized double getPixelDisplacementToCenterToCompositeTarget() {
		if (isCoherent() && largeIndex != BAD_INDEX) {
			double diff = (getCenterX()[largeIndex] - (HORIZONTAL_CAMERA_RES_PIXELS / 2));
			return diff;
		} else
			return 0;
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
