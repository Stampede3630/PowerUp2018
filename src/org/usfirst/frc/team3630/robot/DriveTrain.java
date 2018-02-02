package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.kauailabs.navx.frc.AHRS;

public class DriveTrain  {
	
	
	private XboxController _xBox;
	AHRS ahrs;
	// corection angle for PID Source 
	public double correctionAngle = 0;
	TankDrivePath path;
	// add coment about from what perspective of robot 
	// need to test
	 PIDController turnController;
	 
		TankDrivePath path;
	 double rotateToAngleRate;
	    static final double kP = 0.1 ;
    static final double kI = 0.00;
    static final double kD = 0.00;
    static final double kF = 1;

    // target angle degrees for straight on should not be a constant !
     double kTargetAngleDegrees = 0f;
    
	private  WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;
	private SpeedControllerGroup  leftSpeedController,rightSpeedController;
	// PIDSource pidSource ;
	 DifferentialDrive driveTrain ;

	public DriveTrain()  {
		//calibrate navx !!!!!
		path = new TankDrivePath();
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

		
		   turnController = new PIDController(Consts.kPA, Consts.kIA, Consts.kID,  ahrs, new MyPidOutput());
	       // setting range and disable it 
		   turnController.setInputRange(-180.0f,  180.0f);
	        turnController.setOutputRange(-1.0, 1.0);
	        turnController.setAbsoluteTolerance(Consts.kToleranceDegrees);
	        turnController.setContinuous(true);
	        turnController.disable();
	        
	      
	     
	        
	    	configureTalon(frontLeft);
			configureTalon(frontRight);
			configureTalon(backLeft);
			configureTalon(backRight);
			frontRight.setInverted(true);
			backRight.setInverted(true);
			   path = new TankDrivePath(frontLeft,frontRight);
			
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
	
	
	
	
public double ahrsYaw() {
	double yaw = ahrs.getYaw();
	return yaw;
}

	public void driveTrainPeriodic() {
		double speed = _xBox.getY(GenericHID.Hand.kLeft);
		double heading = _xBox.getX(GenericHID.Hand.kRight);

	
		
	
		SmartDashboard.putNumber("Setpoint", 1000);
		
	}
	private void configureTalon(TalonSRX _talon) {
	
			_talon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0,10);
		_talon.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, 0);
		_talon.configNominalOutputForward(0, Consts.timeOutMs);
		_talon.configNominalOutputReverse(0, Consts.timeOutMs);
		_talon.configPeakOutputForward(1, Consts.timeOutMs);
		_talon.configPeakOutputReverse(-1, Consts.timeOutMs);
		_talon.setSensorPhase(true);
		_talon.configAllowableClosedloopError(0, 0, Consts.timeOutMs);
		_talon.config_kP(0, Consts.kPencoder, Consts.timeOutMs);
		_talon.config_kI(0, Consts.kIencoder, Consts.timeOutMs);
		_talon.config_kD(0, Consts.kDencoder, Consts.timeOutMs);
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
		public void testPeriodic() {
			SmartDashboard.putNumber("Front Right Position", getRotations(frontRight));
			SmartDashboard.putNumber("Front Right Velocity", getVelocity(frontRight));
			SmartDashboard.putNumber("Front Left Position", getRotations(frontLeft));
			SmartDashboard.putNumber("Front Left Velocity", getVelocity(frontLeft));
			SmartDashboard.putNumber("Back Right Position", getRotations(backRight));
			SmartDashboard.putNumber("Back Right Velocity", getVelocity(backRight));
			SmartDashboard.putNumber("Back Left Position", getRotations(backLeft));
			SmartDashboard.putNumber("Back Left Velocity", getVelocity(backLeft));
			//SmartDashboard.putNumber("Target", frontLeft.getClosedLoopTarget(0));
			//SmartDashboard.putString("control mode",frontLeft.getControlMode() );
			//frontLeft.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
			//frontRight.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
			//backLeft.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
			//backRight.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
			//SmartDashboard.putNumber("Front Left Error", frontLeft.getClosedLoopError(0));
			path.pathFeedback()
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

		
		public  class MyPidOutput implements PIDOutput {
			
			// implements pid output
					

				
					public void pidWrite(double output) {
						correctionAngle=output;
						
						
					}
					
				}

}
