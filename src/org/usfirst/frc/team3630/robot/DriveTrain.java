package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.*;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.livewindow.*;

import com.kauailabs.navx.frc.AHRS;

public class DriveTrain {

	private XboxController _xBox;
	AHRS ahrs;

	// add coment about from what perspective of robot
	// need to test
	double turnOutput;
	double posOutput;
	boolean init = true;
	int myCurrentCase;		
	PIDController turnController;
	PIDController posController;
	double rotateToAngleRate;
	

	// target angle degrees for straight on should not be a constant !
	double kTargetAngleDegrees = 0f;
	double kTargetDistanceInches = 1000;

	private WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;
	private SpeedControllerGroup leftSpeedController, rightSpeedController;
	// PIDSource pidSource ;
	DifferentialDrive driveTrain;

	// defining PIDSource
	EncoderPIDSource positionEncoderSource;

	public DriveTrain() {
		// calibrate navx !!!!!
		ahrs = new AHRS(SPI.Port.kMXP);
		ahrs.setPIDSourceType(PIDSourceType.kDisplacement);
		_xBox = new XboxController(Consts.xBoxComPort);
		// srx defin
		frontLeft = new WPI_TalonSRX(Consts.frontLeftTalon);
		backLeft = new WPI_TalonSRX(Consts.backLeftTalon);
		frontRight = new WPI_TalonSRX(Consts.frontRightTalon);
		backRight = new WPI_TalonSRX(Consts.backRightTalon);
		//////////////////////////
		configureTalon(frontLeft);
		configureTalon(frontRight);
		configureTalon(backLeft);
		configureTalon(backRight);
		frontRight.setInverted(false);
		backRight.setInverted(false); 
		

		//SmartDashboard.putNumber("Setpoint", 1000);
		//SmartDashboard.putNumber("pos Setpoint", 24);
		//SmartDashboard.putNumber("posController kP", 0.07);

		leftSpeedController = new SpeedControllerGroup(frontLeft, new SpeedController[] { backLeft });
		rightSpeedController = new SpeedControllerGroup(frontRight, new SpeedController[] { backRight });
		////////////
		driveTrain = new DifferentialDrive(leftSpeedController, rightSpeedController);
		driveTrain.setDeadband(0);
		turnController = new PIDController(Consts.kPRotAng, Consts.kIRotAng, Consts.kDRotAng, ahrs,new MyRotationPidOutput());

		// setting range and disable it
		turnController.setInputRange(-180.0f, 180.0f);
		turnController.setOutputRange(-.75, .75);
		turnController.setAbsoluteTolerance(Consts.kToleranceDegrees);
		turnController.setContinuous(true);
		turnController.disable();

		positionEncoderSource = new EncoderPIDSource(frontLeft, frontRight);
		posController = new PIDController(Consts.kPPos, Consts.kIPos, Consts.kDPos,
				positionEncoderSource, new MyPosPidOutput());
		posController.setOutputRange(-1, 1);
		posController.setAbsoluteTolerance(Consts.kToleranceDistance);
		posController.disable();
		

	}
	// init method for navx calibaration setting

	/* This function is invoked periodically by the PID Controller, */

	public void driveStraight() {

		turnController.enable();
		turnController.setSetpoint(kTargetAngleDegrees);

		posController.enable();
		posController.setSetpoint(kTargetDistanceInches);

		// driveTrain.arcadeDrive(.5, angle );

	}

	public void turnDegree(double degrees) {
		kTargetAngleDegrees = degrees;
		turnController.setSetpoint(kTargetAngleDegrees);
	

	}

	public double ahrsYaw() {
		double yaw = ahrs.getYaw();
		return yaw;
	}

	public void driveTrainPeriodic() {
		double speed = (_xBox.getY(GenericHID.Hand.kLeft))*-1;
		double heading = _xBox.getX(GenericHID.Hand.kRight);
		driveTrain.arcadeDrive(speed, heading);

	}

	private void configureTalon(TalonSRX _talon) {
		_talon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 10);

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
		//SmartDashboard.putString("Drive Mode", frontLeft.getControlMode().toString());
	     
		driveTrain.arcadeDrive(posOutput, turnOutput);
		SmartDashboard.putNumber("ahrs headng", ahrs.getAngle());
		SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		SmartDashboard.putNumber("Position Setpoint", posController.getSetpoint());
		SmartDashboard.putNumber("Position Error", posController.getError());
		//SmartDashboard.putString("Drive Mode", frontLeft.getControlMode().toString());
		SmartDashboard.putNumber("Front Left Position", getRotations(frontLeft));
		SmartDashboard.putNumber("Front Left Velocity", getVelocity(frontLeft));
		SmartDashboard.putNumber("Stage", myCurrentCase);
		SmartDashboard.putNumber("turn controller error", turnController.getError());
		//posController.setP(SmartDashboard.getNumber("posController kP", 0.07));
		//leftSwitchLeft();
		rightSwitchRight();
		//rightScaleRight();
		//leftScaleLeft();
		
	}
	
	public void leftSwitchLeft() {
		if (myCurrentCase  == 1) {
			if(init) {
				posController.setSetpoint(Consts.midOfSwitch);
				posController.enable();
				turnController.enable();
				turnController.setSetpoint(0);
				init = false;
			}
		     if(Math.abs(posController.getError()) < 1) {
		     		myCurrentCase = 2;
		     		init = true;
		     	}
		     			
		}
		//posController.setSetpoint(SmartDashboard.getNumber("pos Setpoint", 48))
		
		if (myCurrentCase == 2) {
			if(init) {
				posController.disable();
				turnController.enable();
				turnDegree(90f);
				init = false;
			}
			if(Math.abs(turnController.getError())<4) {
				myCurrentCase = 3;
	     		init = true;
			}
		    
			
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
			}
		if (myCurrentCase == 3) {
			if(init) {

				posController.setSetpoint(Consts.afterTurnToSwitch + positionEncoderSource.pidGet());
				posController.enable();
				init = false;
			}
			if(Math.abs(posController.getError()) < 1) {
				myCurrentCase = 4;
	     		init = true;
			}
		}
		if(myCurrentCase == 4) {
			turnController.disable();
			posController.disable();
		}
	}

	public void rightSwitchRight() {
		if (myCurrentCase  == 1) {
			if(init) {
				posController.setSetpoint(Consts.midOfSwitch);
				posController.enable();
				turnController.enable();
				turnController.setSetpoint(0);
				init = false;
			}
		     if(Math.abs(posController.getError()) < 1) {
		     		myCurrentCase = 2;
		     		init = true;
		     	}
		     			
		}
		//posController.setSetpoint(SmartDashboard.getNumber("pos Setpoint", 48))
		
		if (myCurrentCase == 2) {
			if(init) {
				posController.disable();
				turnController.enable();
				turnDegree(-90f);
				init = false;
			}
			if(Math.abs(turnController.getError())<4) {
				myCurrentCase = 3;
	     		init = true;
			}
		    
			
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
			}
		if (myCurrentCase == 3) {
			if(init) {

				posController.setSetpoint(Consts.afterTurnToSwitch + positionEncoderSource.pidGet());
				posController.enable();
				init = false;
			}
			if(Math.abs(posController.getError()) < 1) {
				myCurrentCase = 4;
	     		init = true;
			}
		}
		if(myCurrentCase == 4) {
			turnController.disable();
			posController.disable();
		}
	}
	
	public void rightScaleRight() {
		if (myCurrentCase  == 1) {
			if(init) {
				posController.setSetpoint(Consts.midOfScale);
				posController.enable();
				turnController.enable();
				turnController.setSetpoint(0);
				init = false;
			}
		     if(Math.abs(posController.getError()) < 1) {
		     		myCurrentCase = 2;
		     		init = true;
		     	}
		     			
		}
		//posController.setSetpoint(SmartDashboard.getNumber("pos Setpoint", 48))
		
		if (myCurrentCase == 2) {
			if(init) {
				posController.disable();
				turnController.enable();
				turnDegree(-90f);
				init = false;
			}
			if(Math.abs(turnController.getError())<4) {
				myCurrentCase = 3;
	     		init = true;
			}
		    
			
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
			}
		if (myCurrentCase == 3) {
			if(init) {

				posController.setSetpoint(Consts.afterTurnToSwitch + positionEncoderSource.pidGet());
				posController.enable();
				init = false;
			}
			if(Math.abs(posController.getError()) < 1) {
				myCurrentCase = 4;
	     		init = true;
			}
		}
		if(myCurrentCase == 4) {
			turnController.disable();
			posController.disable();
		}
		
	}
	
	public void leftScaleLeft() {
		if (myCurrentCase  == 1) {
			if(init) {
				posController.setSetpoint(Consts.midOfScale);
				posController.enable();
				turnController.enable();
				turnController.setSetpoint(0);
				init = false;
			}
		     if(Math.abs(posController.getError()) < 1) {
		     		myCurrentCase = 2;
		     		init = true;
		     	}
		     			
		}
		//posController.setSetpoint(SmartDashboard.getNumber("pos Setpoint", 48))
		
		if (myCurrentCase == 2) {
			if(init) {
				posController.disable();
				turnController.enable();
				turnDegree(90f);
				init = false;
			}
			if(Math.abs(turnController.getError())<4) {
				myCurrentCase = 3;
	     		init = true;
			}
		    
			
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
			}
		if (myCurrentCase == 3) {
			if(init) {

				posController.setSetpoint(Consts.afterTurnToSwitch + positionEncoderSource.pidGet());
				posController.enable();
				init = false;
			}
			if(Math.abs(posController.getError()) < 1) {
				myCurrentCase = 4;
	     		init = true;
			}
		}
		if(myCurrentCase == 4) {
			turnController.disable();
			posController.disable();
		}
	}
	
	
/*public void leftSwitchRight() {
	if (myCurrentCase  == 1) {
		if(init) {
			posController.setSetpoint(Consts.midOfScale);//Need to change distance
			posController.enable();
			turnController.enable();
			turnController.setSetpoint(0);
			init = false;
		}
	     if(Math.abs(posController.getError()) < 1) {
	     		myCurrentCase = 2;
	     		init = true;
	     	}
	     			
	}
	//posController.setSetpoint(SmartDashboard.getNumber("pos Setpoint", 48))
	
	if (myCurrentCase == 2) {
		if(init) {
			posController.disable();
			turnController.enable();
			turnDegree(90f);
			init = false;
		}
		if(Math.abs(turnController.getError())<4) {
			myCurrentCase = 3;
     		init = true;
		}
	    
		
		SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		}
	if (myCurrentCase == 3) {
		if(init) {

			posController.setSetpoint(Consts.afterTurnToSwitch + positionEncoderSource.pidGet());//Change distance
			posController.enable();
			init = false;
		}
		if(Math.abs(posController.getError()) < 1) {
			myCurrentCase = 4;
     		init = true;
		}
	}
	if (myCurrentCase == 4) {
		if(init)
			posController.disable();
			turnController.enable();
			turnDegree(90f)
		if(Math.abs(posController.get))
	}
	if(myCurrentCase == 4) {
		turnController.disable();
		posController.disable();
	}
*/
	
		
	public void putData() {
		SmartDashboard.putNumber("correctionAngle", turnOutput);
		

	}

	public void stop() {
		driveTrain.arcadeDrive(0, 0);
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
		// SmartDashboard.putNumber("Target", frontLeft.getClosedLoopTarget(0));
		// SmartDashboard.putString("control mode",frontLeft.getControlMode() );
//		frontLeft.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
//		frontRight.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
//		backLeft.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
//		backRight.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
		SmartDashboard.putNumber("Front Left Error", frontLeft.getClosedLoopError(0));
	}

	public void testInit() {
		ahrs.reset();
		frontLeft.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		frontRight.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		LiveWindow.disableAllTelemetry();
		myCurrentCase = 1;	
		
		
		
	}

	public double getRotations(TalonSRX _talon) {
		double distance_ticks = _talon.getSelectedSensorPosition(0);
		double distance_rotations = distance_ticks / Consts.ticksPerRotation;
		return distance_rotations;

	}

	public double getVelocity(TalonSRX _talon) {
		double velocity_milliseconds = (double) _talon.getSelectedSensorVelocity(0) / Consts.ticksPerRotation;
		//System.out.println(velocity_milliseconds);
		double velocity_seconds = velocity_milliseconds * Consts.millisecondsPerSecond;
		return velocity_seconds;
	}

	/*public class MyPidOutput implements PIDOutput {
		public double correctionAngle=0;
		// implements pid output
	
		public void pidWrite(double output) {
			correctionAngle = output;
		}

	}*/
	
	public  class MyPosPidOutput implements PIDOutput {
		// implements pid output
				public void pidWrite(double output) {
					posOutput=output;
				}
		}
	public  class MyRotationPidOutput implements PIDOutput {
		// implements pid output
				public void pidWrite(double output) {
					turnOutput=output;
				}
		}


	private class EncoderPIDSource implements PIDSource {
		private TalonSRX _frontLeft, _frontRight;

		public EncoderPIDSource(TalonSRX talon1,TalonSRX talon2) {
			_frontLeft = talon1;
			_frontRight = talon2;

		}

		public double pidGet() {
			double fLGetSelected = _frontLeft.getSelectedSensorPosition(0);
			double fRGetSelected = _frontRight.getSelectedSensorPosition(0)*-1;
			double position_raw = (fLGetSelected + fRGetSelected)/2;
			double position_inches = position_raw * (double) (2 * Math.PI * Consts.wheelRadius) / (double) Consts.ticksPerRotation ;
			SmartDashboard.putNumber("Front Left Talon Position", fRGetSelected);
			SmartDashboard.putNumber("Front Right Talon Position", fRGetSelected);
			return position_inches;
		}

		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}

		public void setPIDSourceType(PIDSourceType pidSource) {
			
		}
	}
}
