package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.kauailabs.navx.frc.AHRS;

public class DriveTrain  {
	
	
	private XboxController _xBox;
	AHRS ahrs;
	public double correctionAngle = 0;
	
	// add coment about from what perspective of robot 
	// need to test
	 PIDController turnController;
	 double rotateToAngleRate;
	// pid conts need to test     
    static final double kP = 0.1 ;
    static final double kI = 0.00;
    static final double kD = 0.00;
    static final double kF = 1;
    //MyPidOutput PIDOut;

    static final double kToleranceDegrees = .5f;    
    // target anfle degrees for straight on
     double kTargetAngleDegrees = 0f;
    
	private  WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;
	private SpeedControllerGroup  leftSpeedController,rightSpeedController;
	// PIDSource pidSource ;
	 DifferentialDrive driveTrain ;

	public DriveTrain()  {
		//calibrate navx !!!!!
	    
		 ahrs = new AHRS(SPI.Port.kMXP); 
		 ahrs.setPIDSourceType(PIDSourceType.kDisplacement);
		_xBox = new XboxController(Consts.xBoxComPort);
		// srx defin
		frontLeft = new WPI_TalonSRX(Consts.frontLeftTalon);
		backLeft = new WPI_TalonSRX(Consts.backLeftTalon);
		frontRight = new WPI_TalonSRX(Consts.frontRightTalon);
		backRight = new WPI_TalonSRX(Consts.backRightTalon);
		//////////////////////////
	
	leftSpeedController = new SpeedControllerGroup (frontLeft, new SpeedController[] {backLeft});
	rightSpeedController = new SpeedControllerGroup (frontRight, new SpeedController[] {backRight});
////////////
		driveTrain = new DifferentialDrive(leftSpeedController, rightSpeedController);
// init pid controlor
		
		   turnController = new PIDController(kP, kI, kD,  ahrs, new MyPidOutput());
	       // setting range and disable it 
		   turnController.setInputRange(-180.0f,  180.0f);
	        turnController.setOutputRange(-1.0, 1.0);
	        turnController.setAbsoluteTolerance(kToleranceDegrees);
	        turnController.setContinuous(true);
	        turnController.disable();
	        
	      
	}
	
	public void autoInit() {
		ahrs.reset();
	}
	// init method for navx calibaration setting 
	
	
	   /* This function is invoked periodically by the PID Controller, */

	public void driveStraight() {
		
		
		turnController.enable();
		turnController.setSetpoint(kTargetAngleDegrees);
		
	
	//	driveTrain.arcadeDrive(.5, angle );
		
	}
	public void turnDegree(double degrees) {
		 kTargetAngleDegrees= degrees ;
		 turnController.setSetpoint( kTargetAngleDegrees);
	
		
		
	}
	
	
	
	


	public void driveTrainPeriodic() {
		double speed = _xBox.getY(GenericHID.Hand.kLeft);
		double heading = _xBox.getX(GenericHID.Hand.kRight);
			driveTrain.arcadeDrive(speed, heading);
			
		}
	public void testDriveTrainPeriodic() {
	
	
		driveTrain.arcadeDrive(.6, correctionAngle);
	}
	
	public void putData() {
		SmartDashboard.putNumber("corectionAnnge", correctionAngle);
		SmartDashboard.putNumber("ahrs headng", ahrs.getAngle());

	}
	public void stop(){
		driveTrain.arcadeDrive(0,0);
	}
	
	
public  class MyPidOutput implements PIDOutput {
		
	
		public void pidWrite(double output) {
			correctionAngle=output;
			
			
		}
		
	}
}
