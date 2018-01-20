package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    double angleRotate;
	private WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;
	private SpeedControllerGroup rightSpeedController, leftSpeedController;
	private DifferentialDrive driveTrain;
	
	
	public DriveTrain() {
		//calibrate navx !!!!!
		_xBox = new XboxController(Consts.xBoxComPort);
		frontLeft = new WPI_TalonSRX(Consts.frontLeftTalon);
		backLeft = new WPI_TalonSRX(Consts.backLeftTalon);
		frontRight = new WPI_TalonSRX(Consts.frontRightTalon);
		backRight = new WPI_TalonSRX(Consts.backRightTalon);
		leftSpeedController = new SpeedControllerGroup (frontLeft, new SpeedController[] {backLeft});
		rightSpeedController = new SpeedControllerGroup (frontRight, new SpeedController[] {backRight});
		driveTrain = new DifferentialDrive(leftSpeedController, rightSpeedController);
		ahrs = new AHRS(SPI.Port.kMXP);
		angleController = new PIDController(kP, kI, kD, kF, ahrs, angleRotate = angleController.get()  );
		angleController.setInputRange(-180, 180);
		angleController.setOutputRange(-1, 1);
		angleController.setAbsoluteTolerance(kToleranceDegrees);
		angleController.setContinuous(true);
		angleController.disable();
		// returns curent output of pid controllor 
	    /* Add the PID Controller to the Test-mode dashboard, allowing manual  */
        /* tuning of the Turn Controller's P, I and D coefficients.            */
        /* Typically, only the P value needs to be modified.                   */
        LiveWindow.addActuator("DriveSystem", "RotateController", angleController);    
		
	}
	
	
	// add auto drive straight peridodic 
	public void autoStraight() {
		// put navx heading 
		angleController.setSetpoint(kTargetAngleDegrees);
		
		SmartDashboard.putData("NavX heading", ahrs.getYaw());
		angleController.enable();
		driveTrain.arcadeDrive(.5, angleRotate);
	}


	
	
	
		public void driveTrainPeriodic() {
			double speed = _xBox.getY(GenericHID.Hand.kLeft);
			double heading = _xBox.getX(GenericHID.Hand.kRight);
			driveTrain.arcadeDrive(speed, heading);
			
		}
		  public void pidWrite(double output) {
			  angleRotate = output;
		    }	
		
}
