package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {
	private XboxController _xBox;
	// add coment about from what perspective of robot 
	
	private WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;
	private SpeedControllerGroup rightSpeedController, leftSpeedController;
	private DifferentialDrive driveTrain;
	
	public DriveTrain() {
		// conts classss !!!!!!
		_xBox = new XboxController(Consts.xBoxComPort);
		frontLeft = new WPI_TalonSRX(Consts.frontLeftTalon);
		backLeft = new WPI_TalonSRX(Consts.backLeftTalon);
		frontRight = new WPI_TalonSRX(Consts.frontRightTalon);
		backRight = new WPI_TalonSRX(Consts.backRightTalon);
		leftSpeedController = new SpeedControllerGroup (frontLeft, new SpeedController[] {backLeft});
		rightSpeedController = new SpeedControllerGroup (frontRight, new SpeedController[] {backRight});
		driveTrain = new DifferentialDrive(leftSpeedController, rightSpeedController);
		
		frontLeft.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0,10);
		frontLeft.setSensorPhase(false);
		backLeft.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0,10);
		backLeft.setSensorPhase(false);
		frontRight.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0,10);
		frontRight.setSensorPhase(false);
		frontRight.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 1, 10);
		backRight.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0,10);
		backRight.setSensorPhase(false);
		
	}
		public void driveTrainPeriodic() {
			double speed = _xBox.getY(GenericHID.Hand.kLeft)*-1;
			double heading = _xBox.getX(GenericHID.Hand.kRight);
			driveTrain.arcadeDrive(speed, heading);
			
		}
		
		public void testPeriodic() {
			SmartDashboard.putNumber("Front Right Position", getRotations(frontRight));
			SmartDashboard.putNumber("Front Right Velocity", getVelocity(frontRight));
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
