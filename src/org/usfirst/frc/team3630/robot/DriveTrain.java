package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.kauailabs.navx.frc.AHRS;

public class DriveTrain {
	private XboxController _xBox;
	AHRS ahrs;
	
	// add coment about from what perspective of robot 
	// need to test
	static final double kP = 0.03;
    static final double kI = 0.00;
    static final double kD = 0.00;
    static final double kF = 0.00;
	// need to test later 
    static final double kToleranceDegrees = 0;
    static final double kTargetAngleDegrees = 0;
    PIDController angleController;
    
	private WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;
	private SpeedControllerGroup rightSpeedController, leftSpeedController;
	private DifferentialDrive driveTrain;
	
	public DriveTrain() {
		//
		_xBox = new XboxController(Consts.xBoxComPort);
		frontLeft = new WPI_TalonSRX(Consts.frontLeftTalon);
		backLeft = new WPI_TalonSRX(Consts.backLeftTalon);
		frontRight = new WPI_TalonSRX(Consts.frontRightTalon);
		backRight = new WPI_TalonSRX(Consts.backRightTalon);
		leftSpeedController = new SpeedControllerGroup (frontLeft, new SpeedController[] {backLeft});
		rightSpeedController = new SpeedControllerGroup (frontRight, new SpeedController[] {backRight});
		driveTrain = new DifferentialDrive(leftSpeedController, rightSpeedController);
		ahrs = new AHRS(SPI.Port.kMXP);
		angleController = new PIDController(kP, kI, kD, kF, ahrs, this );
		angleController.setInputRange(-180, 180);
		angleController.setOutputRange(-1, 1);
		angleController.setAbsoluteTolerance(kToleranceDegrees);
		angleController.setContinuous(true);
		angleController.disable();
		
	}
	
	
	// add auto drive straight peridodic 
		public void driveTrainPeriodic() {
			double speed = _xBox.getY(GenericHID.Hand.kLeft);
			double heading = _xBox.getX(GenericHID.Hand.kRight);
			driveTrain.arcadeDrive(speed, heading);
			
		}
		  public void pidWrite(double output) {
		        rotateToAngleRate = output;
		    }	
		
}
