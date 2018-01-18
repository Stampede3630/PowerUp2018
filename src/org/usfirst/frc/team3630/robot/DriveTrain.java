package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveTrain {
	private XboxController _xBox;
	private WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;
	private SpeedControllerGroup rightSpeedController, leftSpeedController;
	private DifferentialDrive driveTrain;
	
	public DriveTrain() {
		_xBox = new XboxController(0);
		frontLeft = new WPI_TalonSRX(1);
		backLeft = new WPI_TalonSRX(3);
		frontRight = new WPI_TalonSRX(4);
		backRight = new WPI_TalonSRX(2);
		leftSpeedController = new SpeedControllerGroup (frontLeft, new SpeedController[] {backLeft});
		rightSpeedController = new SpeedControllerGroup (frontRight, new SpeedController[] {backRight});
		driveTrain = new DifferentialDrive(leftSpeedController, rightSpeedController);
	}
		public void driveTrainPeriodic() {
			double speed = _xBox.getY(GenericHID.Hand.kLeft);
			double heading = _xBox.getX(GenericHID.Hand.kRight);
			driveTrain.arcadeDrive(speed, heading);
			
		}
		
		
}
