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
import jaci.pathfinder.*;
import jaci.pathfinder.followers.*;
import jaci.pathfinder.modifiers.*;

public class DriveTrain {

	private XboxController _xBox;
	boolean pathFinderPeriodicCalled, TalonResetCall, EncodersReset;
	private WPI_TalonSRX leftThreeEncoder, rightSixEncoder, leftTwo, rightFive;
	public AHRS ahrs;
	PowerDistributionPanel panel;
	DifferentialDrive driveTrain;

	/**
	 * leftThree , right six master motors and drive train constru
	 */
	private BoxGrabber driveBox;
	
	  tankDrivePath path;
	public DriveTrain(BoxGrabber _boxGrabber) {
		ahrs = new AHRS(SPI.Port.kMXP); 
		driveBox = _boxGrabber;
		
		panel = new PowerDistributionPanel(0);
		_xBox = new XboxController(Consts.xBoxComPort);
		
		leftThreeEncoder = new WPI_TalonSRX(Consts.leftThree);
		leftTwo = new WPI_TalonSRX(Consts.leftTwo);
	
		rightSixEncoder = new WPI_TalonSRX(Consts.rightSix);
		rightFive = new WPI_TalonSRX(Consts.rightFive);
		
		configureTalon(leftThreeEncoder);
		configureTalon(rightSixEncoder);
		configureTalon(leftTwo);
		configureTalon(rightFive);

		
		rightFive.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightSixEncoder.getDeviceID());
		leftTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftThreeEncoder.getDeviceID());
		
	
		leftThreeEncoder.setSensorPhase(false);
		rightSixEncoder.setSensorPhase(false);
		
		driveTrain = new DifferentialDrive(leftThreeEncoder, rightSixEncoder);
		path = new tankDrivePath(leftThreeEncoder,rightSixEncoder,ahrs);
		
		driveTrain.setDeadband(0); // why set to zero and not at default ?.02
		
	}


	/**
	 *  set up for test init  */
	public void testInit() {
		rightFive.setInverted(true);
		rightSixEncoder.setInverted(true);
		leftThreeEncoder.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 10);
		rightSixEncoder.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 10);
		rightFive.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightSixEncoder.getDeviceID());
		leftTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftThreeEncoder.getDeviceID());
			if(leftThreeEncoder.getSelectedSensorPosition(0) < 238 && rightSixEncoder.getSelectedSensorPosition(0) <238){
				System.out.println("encoders were reset");
				EncodersReset= true ;
			}
			else{
				System.out.println("your encoders wern't reset");
				EncodersReset= false ;
			}
			TalonResetCall= true;

	}
	
	
	
	public void testPeriodic() {
//		rightFive.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightSixEncoder.getDeviceID());
//		leftTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftThreeEncoder.getDeviceID());
		
		
		path.autoPeriodic();

		

	}


	
	// add ahrs  congif method to see if calibating. It could be a good saftey checlk 
	public void teleopInit() {
		leftThreeEncoder.configOpenloopRamp(.2, Consts.timeOutMs);
		rightSixEncoder.configOpenloopRamp(.2, Consts.timeOutMs);
		
		rightFive.setInverted(false);
		rightSixEncoder.setInverted(false);

	}
	public void teleopPeriodic() {
		double speed = (_xBox.getY(GenericHID.Hand.kLeft))*-.6;
		double heading = (_xBox.getX(GenericHID.Hand.kRight))*.6;
		if(_xBox.getTriggerAxis(GenericHID.Hand.kLeft) > .85) {
			speed = (_xBox.getY(GenericHID.Hand.kLeft))*-1;
		}
		SmartDashboard.putNumber("heading acrcade drive", heading);
		driveTrain.arcadeDrive(speed, heading);
		getDiagnostics();

		// three are two missing bad? delted folowers set in constructor
		
	SmartDashboard.putNumber("Left three curent", leftThreeEncoder.getOutputCurrent());

	//	SmartDashboard.putNumber("talon left two ", panel.getCurrent(1));
		// moved over to driverStaton warnings 
		// are we still getting curent issues 
		
		//for graphing to get a sure sane kv, will put this in csv and graph
		System.out.print(leftThreeEncoder.getMotorOutputVoltage());
		System.out.print(",");
		
		System.out.print(getVelocity(leftThreeEncoder));System.out.print(",");
		
		
	
		System.out.print("\n");

		
			
		
	if(panel.getTotalCurrent()>300) {
		System.out.print("[WARNING] CURRENT DRAW IS AT ");
		System.out.print(panel.getTotalCurrent());
		System.out.print('\n');
	}

	}

	/**
	 * @param _talon
	 * set up  for talon configuration. should be done once during initialization
	 */
	private void configureTalon(TalonSRX _talon) {
		_talon.configNominalOutputForward(0, Consts.timeOutMs);
		_talon.configNominalOutputReverse(0, Consts.timeOutMs);
		_talon.configPeakOutputForward(1, Consts.timeOutMs);
		_talon.configPeakOutputReverse(-1, Consts.timeOutMs);
		_talon.configAllowableClosedloopError(0, 0, Consts.timeOutMs);

		_talon.configNeutralDeadband(0.05, Consts.timeOutMs); // Why do we have 0? 0.025 means a normal 2.5% deadband. might be worth looking at 
		_talon.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);

		
		// Peak current and duration must be exceeded before corrent limit is activated.
		// When activated, current will be limited to continuous current.
	    // Set peak current params to 0 if desired behavior is to immediately current-limit.
		_talon.enableCurrentLimit(true);
		_talon.configContinuousCurrentLimit(50,Consts.timeOutMs); // Must be 5 amps or more
		_talon.configPeakCurrentLimit(50, Consts.timeOutMs); // 100 A
		_talon.configPeakCurrentDuration(200,Consts.timeOutMs); // 200 ms
		
	}

	/**
	 *  diganoaric method for taon srx debuging 
	 */
	public void getDiagnostics() {		

		SmartDashboard.putNumber("Right Current", rightSixEncoder.getOutputCurrent());
		SmartDashboard.putNumber("Front Right Position", getRotations(rightSixEncoder));
		SmartDashboard.putNumber("Front Right Velocity", getVelocity(rightSixEncoder));
		SmartDashboard.putNumber("Front Left Position", getRotations(leftThreeEncoder));
		SmartDashboard.putNumber("Front Left Velocity", getVelocity(leftThreeEncoder));
		SmartDashboard.putNumber("Left position in ticks", getTicks(leftThreeEncoder));
		SmartDashboard.putNumber("Right position in ticks", getTicks(rightSixEncoder));


	}
	
	
	
	public void autoPeriodic() {
		
	
	}
	/**
	 * @param _talon
	 * @return actual rotation of talon in a rotation of the wheel 
	 * 
	 */
	public double getRotations(TalonSRX _talon) {
		double distance_ticks = _talon.getSelectedSensorPosition(0);
		// WARNING converstion seems fuzy needs to fix 
		double distance_rotations = distance_ticks / Consts.ticksPerRotation;
		return distance_rotations;
	}
	
	public double getTicks(TalonSRX _talon) {
		double distance_ticks = _talon.getSelectedSensorPosition(0);
		return distance_ticks;

	}

	/**
	 * @param _talon
	 * @return velocity in m/ second. from native talon units 
	 */
	public double getVelocity(TalonSRX _talon) {
		double velocity_milliseconds = (double) _talon.getSelectedSensorVelocity(0) / Consts.ticksPerRotation;
		double velocity_seconds = velocity_milliseconds *10* 6*Math.PI*.0254; 
		return velocity_seconds;
	}
	



}
