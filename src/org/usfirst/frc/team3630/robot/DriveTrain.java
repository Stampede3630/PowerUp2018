
package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.*;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.Faults;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.livewindow.*;
import edu.wpi.first.wpilibj.*;
import com.kauailabs.navx.frc.AHRS;

public class DriveTrain {

	private XboxController _xBox;
	
	boolean pathFinderPeriodicCalled;

	double rotateToAngleRate;
	
	TankDrivePath path;
	// target angle degrees for straight on should not be a constant !
	double targetAngleDegrees = 0f;
	double kTargetDistanceInches = 1000;

	private WPI_TalonSRX leftThreeEncoder, rightSixEncoder, leftTwo, rightFive; //leftOne, rightFour;

	
	DifferentialDrive driveTrain;

	// defining PIDSource
	

	/**
	 * leftThree , right six master motors and drive train constru
	 */

	public DriveTrain() {
		pathFinderPeriodicCalled= false;

		leftThreeEncoder = new WPI_TalonSRX(3);
		leftTwo = new WPI_TalonSRX(2);
		//leftOne = new WPI_TalonSRX(Consts.leftOne);
		rightSixEncoder = new WPI_TalonSRX(6);
		rightFive = new WPI_TalonSRX(5);
		//rightFour = new WPI_TalonSRX(Consts.rightFour);
		
		// mabey rename to leftThreeMaster? nice more specific name 
		configureTalon(leftThreeEncoder);
		configureTalon(rightSixEncoder);
		configureTalon(leftTwo);
		configureTalon(rightFive);
		//configureTalon(leftOne);
		//configureTalon(rightFour);
		rightFive.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightSixEncoder.getDeviceID());
		leftTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftThreeEncoder.getDeviceID());
		//rightFour.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightSixEncoder.getDeviceID());
		//leftOne.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftThreeEncoder.getDeviceID());
		// why differ sensor phase diffrent would it be cosntant for both robots?
		leftThreeEncoder.setSensorPhase(false);
		rightSixEncoder.setSensorPhase(true);
	
		path = new TankDrivePath (leftThreeEncoder,rightSixEncoder);
		


	
		driveTrain = new DifferentialDrive(leftThreeEncoder, rightSixEncoder);
		driveTrain.setDeadband(0); // why set to zero and not at default ?.02
		

	}


	/**
	 *  set up for test init  */
	public void testInit() {
		leftThreeEncoder.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, Consts.timeOutMs);
		rightSixEncoder.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, Consts.timeOutMs);
		

	}
	
	public void testPeriodic() {
		path.autoPeriodic();
		pathFinderPeriodicCalled= true; 
		
	}


	// add ahrs  congif method to see if calibating. It could be a good saftey checlk 
	public void teleopInit() {
		leftThreeEncoder.configOpenloopRamp(.2, Consts.timeOutMs);
		rightSixEncoder.configOpenloopRamp(.2, Consts.timeOutMs);

	}
	public void teleopPeriodic() {
		double speed = (_xBox.getY(GenericHID.Hand.kLeft))*-1;
		double heading = (_xBox.getX(GenericHID.Hand.kRight));
		SmartDashboard.putNumber("heading acrcade drive", heading);
		driveTrain.arcadeDrive(speed, heading);
		getDiagnostics();
		// three are two missing bad? delted folowers set in constructor
		
		SmartDashboard.putNumber("Left three curent", leftThreeEncoder.getOutputCurrent());
	

		// moved over to driverStaton warnings 
		// are we still getting curent issues 
//		if(panel.getTotalCurrent()>300) {
//			System.out.print("[WARNING] CURRENT DRAW IS AT ");
//			System.out.print(panel.getTotalCurrent());
//			System.out.print('\n');
//		}

	}

	/**
	 * @param _talon
	 * set up  for tann initatioation
	 */
	private void configureTalon(TalonSRX _talon) {
		_talon.configNominalOutputForward(0, Consts.timeOutMs);
		_talon.configNominalOutputReverse(0, Consts.timeOutMs);
		_talon.configPeakOutputForward(1, Consts.timeOutMs);
		_talon.configPeakOutputReverse(-1, Consts.timeOutMs);
		_talon.configAllowableClosedloopError(0, 0, Consts.timeOutMs);

		_talon.configNeutralDeadband(0, Consts.timeOutMs); // Why do we have 0? 0.025 means a normal 2.5% deadband. might be worth looking at 
		_talon.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		_talon.setInverted(false);
	// Peak current and duration must be exceeded before corrent limit is activated.
	// When activated, current will be limited to continuous current.
    // Set peak current params to 0 if desired behavior is to immediately current-limit.
		_talon.enableCurrentLimit(true);
		_talon.configContinuousCurrentLimit(30,0); // Must be 5 amps or more
		_talon.configPeakCurrentLimit(30, 0); // 100 A
		_talon.configPeakCurrentDuration(200,0); // 200 ms
		
	}

	/**
	 *  diganoaric method for taon srx debuging 
	 */
	public void getDiagnostics() {		
	
		SmartDashboard.putNumber("Front Right Position", getRotations(rightSixEncoder));
		SmartDashboard.putNumber("Front Right Velocity", getVelocity(rightSixEncoder));
		SmartDashboard.putNumber("Front Left Position", getRotations(leftThreeEncoder));
		SmartDashboard.putNumber("Front Left Velocity", getVelocity(leftThreeEncoder));
		SmartDashboard.putNumber("Left position in ticks", getTicks(leftThreeEncoder));
		SmartDashboard.putNumber("Right position in ticks", getTicks(rightSixEncoder));
		
		SmartDashboard.putBoolean("pathFinderPeriodicCalled", pathFinderPeriodicCalled);


//			System.out.print("[WARNING] Talon Current is at ");
//			System.out.print(leftEncoder.getOutputCurrent());
//			System.out.print('\n');
//		}
	}


	/**
	 * @param _talon
	 * @return actual rrotation of talon in a rotation of the wheel 
	 */
	public double getRotations(TalonSRX _talon) {
		double distance_ticks = _talon.getSelectedSensorPosition(0);
		double distance_rotations = distance_ticks / Consts.ticksPerRotation;
		return distance_rotations;
	}
	
	public double getTicks(TalonSRX _talon) {
		double distance_ticks = _talon.getSelectedSensorPosition(0);
		return distance_ticks;

	}

	/**
	 * @param _talon
	 * @return velocity in in/ second. from native taon units 
	 */
	public double getVelocity(TalonSRX _talon) {
		double velocity_milliseconds = (double) _talon.getSelectedSensorVelocity(0) / Consts.ticksPerRotation;
		double velocity_seconds = velocity_milliseconds *10* 6*Math.PI*.0254; 
		return velocity_seconds;
	}

	


}
