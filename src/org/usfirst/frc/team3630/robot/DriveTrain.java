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
		frontLeft.setSensorPhase(true);
		frontLeft.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, 0);
		frontLeft.configNominalOutputForward(0, Consts.timeOutMs);
		frontLeft.configNominalOutputReverse(0, Consts.timeOutMs);
		frontLeft.configPeakOutputForward(1, Consts.timeOutMs);
		frontLeft.configPeakOutputReverse(-1, Consts.timeOutMs);
		frontLeft.configAllowableClosedloopError(0, 0, Consts.timeOutMs);
		frontLeft.config_kP(0, 1.7, Consts.timeOutMs);
		frontLeft.config_kI(0, 0.0, Consts.timeOutMs);
		frontLeft.config_kD(0, 0.0, Consts.timeOutMs);
		SmartDashboard.putNumber("Front Left Setpoint", 1000);
		
		
		
		backLeft.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0,10);
		backLeft.setSensorPhase(false);
		frontRight.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0,10);
		frontRight.setSensorPhase(false);
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
			SmartDashboard.putNumber("Front Left Position", getRotations(frontLeft));
			SmartDashboard.putNumber("Front Left Velocity", getVelocity(frontLeft));
			SmartDashboard.putNumber("Back Right Position", getRotations(backRight));
			SmartDashboard.putNumber("Back Right Velocity", getVelocity(backRight));
			SmartDashboard.putNumber("Back Left Position", getRotations(backLeft));
			SmartDashboard.putNumber("Back Left Velocity", getVelocity(backLeft));
			SmartDashboard.putNumber("Target", frontLeft.getClosedLoopTarget(0));
			//SmartDashboard.putString("control mode",frontLeft.getControlMode() );
			frontLeft.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Front Left Setpoint", 1000));
			SmartDashboard.putNumber("Front Left Error", frontLeft.getClosedLoopError(0));
		}
		public void testInit() {
			frontLeft.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
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
