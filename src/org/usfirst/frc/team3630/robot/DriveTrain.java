package org.usfirst.frc.team3630.robot;


import com.ctre.phoenix.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class DriveTrain  {
	private XboxController _xBox;
	static final double kP = 0.1 ;
    static final double kI = 0.00;
    static final double kD = 0.00;
    static final double kF = 1; 
	  WPI_TalonSRX SlaveTwo, leftEncoderThree, rightSlaveFour, rightSlaveFive, leftSlaveOne, rightEncoderSix;
	 DifferentialDrive driveTrain ;
	 TankDrivePath path;
	public DriveTrain()  {
		_xBox = new XboxController(Consts.xBoxComPort);
	
		leftSlaveOne =new   WPI_TalonSRX(1);  
			 SlaveTwo = new   WPI_TalonSRX(2); 
			 
			 leftEncoderThree = new  WPI_TalonSRX(Consts.leftEncoderThree); 
			 		rightSlaveFour =  new  WPI_TalonSRX(Consts.rightSlaveFour);
			 		rightSlaveFive = new  WPI_TalonSRX(Consts.rightSlaveFive);
			 		rightEncoderSix = new WPI_TalonSRX(Consts.rightEncoderSix);
	
		
		
	    		configureTalon(leftEncoderThree);
			configureTalon(rightEncoderSix);
			
			path = new TankDrivePath(leftEncoderThree,rightEncoderSix);
			frontLeft.setInverted(false);
			backLeft.setInverted(false);
			frontRight.setInverted(true);		
}
	
	public void driveTrainPeriodic() {
		backLeft.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, frontLeft.getDeviceID());
		backRight.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, frontRight.getDeviceID());
		double speed = _xBox.getY(GenericHID.Hand.kLeft)*-1;
		double heading = _xBox.getX(GenericHID.Hand.kRight);
		driveTrain.arcadeDrive( speed, heading);

	}
	private void configureTalon(TalonSRX _talon) {
	
		_talon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0,Consts.timeOutMs);
		_talon.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, 0);
		_talon.configNominalOutputForward(0, Consts.timeOutMs);
		_talon.configNominalOutputReverse(0, Consts.timeOutMs);
		_talon.configPeakOutputForward(1, Consts.timeOutMs);
		_talon.configPeakOutputReverse(-1, Consts.timeOutMs);
		_talon.setSensorPhase(true);
		//_talon.configAllowableClosedloopError(0, 0, Consts.timeOutMs);
		//_talon.config_kP(0, 0, Consts.timeOutMs);
		//_talon.config_kI(0, Consts.kIencoder, Consts.timeOutMs);
		//_talon.config_kD(0, Consts.kDencoder, Consts.timeOutMs);
	}


	public void putData() {
	
	}
	public void stop(){
		driveTrain.arcadeDrive(0,0);
	}
	public void autoInit() {
		// resets encoders
		rightEncoderSix.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		leftEncoderThree.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		
		// need to put in robot init for speed 
		path.rEncoderFollower.reset();
		path.lEncoderFollower.reset();
		path.pathInit();
	}
	
		public void autoPeriodic() {
			leftSlaveOne.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftEncoderThree.getDeviceID());  
			 SlaveTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftEncoderThree.getDeviceID());
			 
			
			 		rightSlaveFour.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightEncoderSix.getDeviceID());  
			 		rightSlaveFive.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightEncoderSix.getDeviceID());
			
			
			path.autoPeriodic();
		}
	
		public double getRotations(TalonSRX _talon) {
			double distance_ticks = _talon.getSelectedSensorPosition(0);
			double distance_rotations = distance_ticks/Consts.ticksPerRotation;
			return distance_rotations;
			
		}
		 
		public double getVelocity(TalonSRX _talon) {
			double velocity_milliseconds = (double) _talon.getSelectedSensorVelocity(0)/Consts.ticksPerRotation;
			System.out.println(velocity_milliseconds);
			double velocity_seconds = velocity_milliseconds*Consts.millisecondsPerSecond;
			return velocity_seconds;
		}

	

}
