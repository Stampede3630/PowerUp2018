package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.kauailabs.navx.frc.AHRS;

public class DriveTrain extends PIDSourceType {
	private XboxController _xBox;
	AHRS ahrs;
	
	// add coment about from what perspective of robot 
	// need to test
	double  kP = 0.03;
     double kI = 0.00;
  double kD = 0.00;
   double kF = 0.00;
	// need to test later 
     double kToleranceDegrees = 3f;
 double kTargetAngleDegrees = 0f;
    PIDController  angleController;
   double angleRotate = 0f ;
	private WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;
	private SpeedControllerGroup rightSpeedController, leftSpeedController;
	DifferentialDrive driveTrain;

	public DriveTrain() extend PIdSourceType {
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
		 
		 ahrs.setPIDSourceType(PIDSourceType ahrs.getYaw());
	
		angleController = new PIDController(kP, kI, kD, kF, ahrs, this);
		angleController.setInputRange(-180f, 180f);
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
	
	  public void pidWrite(double output) {
		  angleRotate = output;
	    }	
	
	  
	public void autoStraight() {
		// put navx heading 
		angleController.setSetpoint(kTargetAngleDegrees);
		double   currentRotationRate = angleRotate;
		SmartDashboard.putData("NavX heading", ahrs.getYaw());
		angleController.enable();
		driveTrain.arcadeDrive(.5,currentRotationRate );
	}


	
	
	
		public void driveTrainPeriodic() {
			double speed = _xBox.getY(GenericHID.Hand.kLeft);
			double heading = _xBox.getX(GenericHID.Hand.kRight);
			driveTrain.arcadeDrive(speed, heading);
			
		}
		
}
