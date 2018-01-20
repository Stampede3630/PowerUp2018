package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;



import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


import edu.wpi.first.wpilibj.SpeedControllerGroup;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.PIDController;

public class DriveTrain  implements PIDOutput{
	private XboxController _xBox;
	AHRS ahrs;
	
	// add coment about from what perspective of robot 
	// need to test
	 PIDController turnController;
	 double rotateToAngleRate;
	    
    static final double kP = 0.03;
    static final double kI = 0.00;
    static final double kD = 0.00;
    static final double kF = 1;
    
    static final double kToleranceDegrees = 2.0f;    
    
    static final double kTargetAngleDegrees = 0f;
    
	private  WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;
	private SpeedControllerGroup  leftSpeedController,rightSpeedController;
	 PIDSource pidSource ;
	 DifferentialDrive driveTrain ;
	public DriveTrain()  {
		//calibrate navx !!!!!
		 ahrs = new AHRS(SPI.Port.kMXP); 
		 // pidSource = ahrs.setPIDSourceType(PIDSourceType.kDisplacement);
		_xBox = new XboxController(Consts.xBoxComPort);
		
		frontLeft = new WPI_TalonSRX(Consts.frontLeftTalon);
		backLeft = new WPI_TalonSRX(Consts.backLeftTalon);
		frontRight = new WPI_TalonSRX(Consts.frontRightTalon);
		backRight = new WPI_TalonSRX(Consts.backRightTalon);
		
		leftSpeedController = new SpeedControllerGroup (frontLeft, new SpeedController[] {backLeft});
	rightSpeedController = new SpeedControllerGroup (frontRight, new SpeedController[] {backRight});

		driveTrain = new DifferentialDrive(leftSpeedController, rightSpeedController);
		
		   turnController = new PIDController(kP, kI, kD, kF, pidSource, this);
	        turnController.setInputRange(-180.0f,  180.0f);
	        turnController.setOutputRange(-1.0, 1.0);
	        turnController.setAbsoluteTolerance(kToleranceDegrees);
	        turnController.setContinuous(true);
	        turnController.disable();

	}
	
	public void driveStraight() {
		rotateToAngleRate= 0;
		turnController.enable();
		turnController.setSetpoint(kTargetAngleDegrees);

		
	}
	
	
	

	public void driveTrainPeriodic() {
		double speed = _xBox.getY(GenericHID.Hand.kLeft);
		double heading = _xBox.getX(GenericHID.Hand.kRight);
			driveTrain.arcadeDrive(speed, heading);
			
		}
		

    /* This function is invoked periodically by the PID Controller, */
    /* based upon navX MXP yaw angle input and PID Coefficients.    */
    public void pidWrite(double output) {
        rotateToAngleRate = output;
    }
}
