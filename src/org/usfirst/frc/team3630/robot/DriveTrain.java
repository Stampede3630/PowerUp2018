package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.*;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.Faults;

import edu.wpi.first.wpilibj.PowerDistributionPanel;;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.livewindow.*;
import edu.wpi.first.wpilibj.*;


import com.kauailabs.navx.frc.AHRS;

public class DriveTrain {

	private XboxController _xBox;
	PowerDistributionPanel panel;
	AHRS ahrs;
	ErrorCode sticky;
	ErrorCode fault;
	// add coment about from what perspective of robot
	// need to test
	double turnOutput;
	double posOutput;
	boolean init = true;
	boolean right = true;
	int myCurrentCase;		
	PIDController turnController;
	PIDController posController;
	double rotateToAngleRate;
	

	// target angle degrees for straight on should not be a constant !
	double kTargetAngleDegrees = 0f;
	double kTargetDistanceInches = 1000;

	private WPI_TalonSRX leftEncoder, rightEncoder, leftTwo, rightTwo, leftThree, rightThree;
	DifferentialDrive driveTrain;

	// defining PIDSource
	EncoderPIDSource positionEncoderSource;

	public DriveTrain() {
		// calibrate navx !!!!!
		ahrs = new AHRS(SPI.Port.kMXP);
		ahrs.setPIDSourceType(PIDSourceType.kDisplacement);
		panel = new PowerDistributionPanel();
		_xBox = new XboxController(Consts.xBoxComPort);
		// srx defin
		leftEncoder = new WPI_TalonSRX(Consts.leftEncoder);
		leftTwo = new WPI_TalonSRX(Consts.leftTwo);
		leftThree = new WPI_TalonSRX(Consts.leftThree);
		rightEncoder = new WPI_TalonSRX(Consts.rightEncoder);
		rightTwo = new WPI_TalonSRX(Consts.rightTwo);
		rightThree = new WPI_TalonSRX(Consts.rightThree);
		
		//////////////////////////
		configureTalon(leftEncoder);
		configureTalon(rightEncoder);
		configureTalon(leftTwo);
		configureTalon(rightTwo);
		configureTalon(leftThree);
		configureTalon(rightThree);
		rightTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightEncoder.getDeviceID());
		leftTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftEncoder.getDeviceID());
		rightThree.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightEncoder.getDeviceID());
		leftThree.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftEncoder.getDeviceID());
		leftEncoder.setSensorPhase(false);
		
		//SmartDashboard.putNumber("Setpoint", 1000);
		//SmartDashboard.putNumber("pos Setpoint", 24);
		//SmartDashboard.putNumber("posController kP", 0.07);
		
	/*	leftEncoder.configContinuousCurrentLimit(30, Consts.timeOutMs); // Must be 5 amps or more
		leftEncoder.configPeakCurrentLimit(0, Consts.timeOutMs); // 100 A
		leftEncoder.enableCurrentLimit(true);
		leftEncoder.configClosedloopRamp(2, Consts.timeOutMs);
		rightEncoder.configContinuousCurrentLimit(30, Consts.timeOutMs); // Must be 5 amps or more
		rightEncoder.configPeakCurrentLimit(0, Consts.timeOutMs); // 100 A
		rightEncoder.enableCurrentLimit(true);
		rightEncoder.configClosedloopRamp(2, Consts.timeOutMs);

*/
		

	
		driveTrain = new DifferentialDrive(leftEncoder, rightEncoder);
		driveTrain.setDeadband(0);
		
		turnController = new PIDController(Consts.kPRotAng, Consts.kIRotAng, Consts.kDRotAng, ahrs,new MyRotationPidOutput());
		// setting range and disable it
		turnController.setInputRange(-180.0f, 180.0f);
		turnController.setOutputRange(-.75, .75);
		turnController.setAbsoluteTolerance(Consts.ToleranceDegrees);
		turnController.setContinuous(true);
		turnController.disable();

		positionEncoderSource = new EncoderPIDSource(leftEncoder, rightEncoder);
		posController = new PIDController(Consts.kPPos, Consts.kIPos, Consts.kDPos,
				positionEncoderSource, new MyPosPidOutput());
		posController.setOutputRange(-1, 1);
		posController.setAbsoluteTolerance(Consts.ToleranceDistance);
		posController.disable();
		

	}
	
	public void testInit() {
		SmartDashboard.putNumber("Left Side Speed", 0);
		SmartDashboard.putNumber("Right Side Speed", 0);
	}
	
	public void testPeriodic() {
		leftEncoder.set(SmartDashboard.getNumber("Left Side Speed", 0));
		rightEncoder.set(SmartDashboard.getNumber("Right Side Speed", 0));

		rightTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightEncoder.getDeviceID());
		leftTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftEncoder.getDeviceID());
		//rightThree.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightEncoder.getDeviceID());
		//leftThree.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftEncoder.getDeviceID());
	}
	// init method for navx calibaration setting


	public void turnDegree(double degrees) {
		kTargetAngleDegrees = degrees;
		turnController.setSetpoint(kTargetAngleDegrees);
	

	}

	public double ahrsYaw() {
		double yaw = ahrs.getYaw();
		return yaw;
	}

	public void teleopPeriodic() {
		double speed = (_xBox.getY(GenericHID.Hand.kLeft))*-1;
		double heading = _xBox.getX(GenericHID.Hand.kRight);
		SmartDashboard.putNumber("heading acrcade drive", heading);
		driveTrain.arcadeDrive(speed, heading);
		getDiagnostics();
		
		rightTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightEncoder.getDeviceID());
		leftTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftEncoder.getDeviceID());
	//	rightThree.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightEncoder.getDeviceID());
		//leftThree.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftEncoder.getDeviceID());
		SmartDashboard.putNumber("Left encoder curent", leftEncoder.getOutputCurrent());
		SmartDashboard.putNumber("Left encoder volt", leftEncoder.getMotorOutputVoltage());
	//	SmartDashboard.putNumber("Left three curent", leftThree.getOutputCurrent());
		SmartDashboard.putNumber("total voltage ", panel.getVoltage());
		
		SmartDashboard.putNumber("total current", panel.getTotalCurrent());
		SmartDashboard.putNumber("talon left two ", panel.getCurrent(1));
		/*rightTwo.enableCurrentLimit(true);
		rightTwo.configContinuousCurrentLimit(30, Consts.timeOutMs); // Must be 5 amps or more
		rightTwo.configPeakCurrentLimit(0, Consts.timeOutMs); // 100 A
		rightTwo.enableCurrentLimit(true);
		rightTwo.configContinuousCurrentLimit(30, Consts.timeOutMs); // Must be 5 amps or more
		rightTwo.configPeakCurrentLimit(0, Consts.timeOutMs); // 100 A
		rightThree .enableCurrentLimit(true);
		rightThree.configContinuousCurrentLimit(30, Consts.timeOutMs); // Must be 5 amps or more
		rightThree.configPeakCurrentLimit(0, Consts.timeOutMs); // 100 A
		leftTwo.enableCurrentLimit(true);
		leftTwo.configContinuousCurrentLimit(30, Consts.timeOutMs); // Must be 5 amps or more
		leftTwo.configPeakCurrentLimit(0, Consts.timeOutMs); // 100 A
		leftThree.enableCurrentLimit(true);
		leftThree.configContinuousCurrentLimit(30, Consts.timeOutMs); // Must be 5 amps or more
		leftThree.configPeakCurrentLimit(0, Consts.timeOutMs); // 100 A
		*/
		
		if(panel.getTotalCurrent()>300) {
			System.out.print("[WARNING] CURRENT DRAW IS AT ");
			System.out.print(panel.getTotalCurrent());
			System.out.print('\n');
		}
		
	}

	private void configureTalon(TalonSRX _talon) {
		_talon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 10);
		_talon.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, 0);
		_talon.configNominalOutputForward(0, Consts.timeOutMs);
		_talon.configNominalOutputReverse(0, Consts.timeOutMs);
		_talon.configPeakOutputForward(1, Consts.timeOutMs);
		_talon.configPeakOutputReverse(-1, Consts.timeOutMs);
		_talon.configAllowableClosedloopError(0, 0, Consts.timeOutMs);
		_talon.config_kP(0, Consts.kPencoder, Consts.timeOutMs);
		_talon.config_kI(0, Consts.kIencoder, Consts.timeOutMs);
		_talon.config_kD(0, Consts.kDencoder, Consts.timeOutMs);
		_talon.configNeutralDeadband(0, Consts.timeOutMs); // Why do we have 0? 0.025 means a normal 2.5% deadband.
		_talon.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Coast);
		_talon.setSensorPhase(true);
		_talon.setInverted(false);
	//	_talon.configOpenloopRamp(.1, Consts.timeOutMs);
		
	// Peak current and duration must be exceeded before corrent limit is activated.
	// When activated, current will be limited to continuous current.
    // Set peak current params to 0 if desired behavior is to immediately current-limit.
	//	_talon.enableCurrentLimit(true);
	//	_talon.configContinuousCurrentLimit(10,0); // Must be 5 amps or more
	//	_talon.configPeakCurrentLimit(10, 0); // 100 A
		//_talon.configPeakCurrentDuration(200,0); // 200 ms
		
	}

	public void getDiagnostics() {
		//SmartDashboard.putBoolean("TwoInverted?",leftTwo.getInverted());
	//	SmartDashboard.putBoolean("ThreeInverted?",leftThree.getInverted());
		
		SmartDashboard.putNumber("Left Current", leftEncoder.getOutputCurrent());
		SmartDashboard.putNumber("Right Current", rightEncoder.getOutputCurrent());
		
		SmartDashboard.putNumber("Front Right Position", getRotations(rightEncoder));
		SmartDashboard.putNumber("Front Right Velocity", getVelocity(rightEncoder));
		SmartDashboard.putNumber("Front Left Position", getRotations(leftEncoder));
		SmartDashboard.putNumber("Front Left Velocity", getVelocity(leftEncoder));
		SmartDashboard.putNumber("Left position in ticks", getTicks(leftEncoder));
		SmartDashboard.putNumber("Right position in ticks", getTicks(rightEncoder));
		
		// SmartDashboard.putNumber("Target", frontLeft.getClosedLoopTarget(0));
		// SmartDashboard.putString("control mode",frontLeft.getControlMode() );
//		frontLeft.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
//		frontRight.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
//		backLeft.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
//		backRight.set(com.ctre.phoenix.motorcontrol.ControlMode.Position, SmartDashboard.getNumber("Setpoint", 1000));
		SmartDashboard.putNumber("Front Left Error", leftEncoder.getClosedLoopError(0));
		//SmartDashboard.putString("Drive Mode", frontLeft.getControlMode().toString());
	     
		
		SmartDashboard.putNumber("ahrs headng", ahrs.getAngle());
		SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		SmartDashboard.putNumber("Position Setpoint", posController.getSetpoint());
		SmartDashboard.putNumber("Position Error", posController.getError());
		//SmartDashboard.putString("Drive Mode", frontLeft.getControlMode().toString());
		SmartDashboard.putNumber("Stage", myCurrentCase);
		SmartDashboard.putNumber("turn controller error", turnController.getError());
		//posController.setP(SmartDashboard.getNumber("posController kP", 0.07));
		SmartDashboard.putBoolean("Is right true?", right);
		SmartDashboard.putBoolean("PosControl ON", 	posController.isEnabled());
		SmartDashboard.putBoolean("TurnControl On", turnController.isEnabled());
		SmartDashboard.putBoolean("Is init true?", init);
		SmartDashboard.putNumber("posController input", posOutput);
		
		fault=leftEncoder.getLastError();
		if(fault != ErrorCode.OK) System.out.println(fault);
		if (leftEncoder.getOutputCurrent()>35) { 
			System.out.print("[WARNING] Talon Current is at ");
			System.out.print(leftEncoder.getOutputCurrent());
			System.out.print('\n');
		}
		}
	
	
	public void autoPeriodic() {
		driveTrain.arcadeDrive(posOutput, turnOutput);
	}
	
	public void leftSwitchLeft() {
		if (myCurrentCase == 1) {
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA + Consts.autoB);
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     	myCurrentCase = 2;
		     	init = true;
		    }
		} //SAMV added this
		if (myCurrentCase == 2) {
			if(init) {
				autoTurnDegree(90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		}
		if (myCurrentCase == 3) {
			if(init) {
				autoDriveFw(Consts.autoE);
				
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
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
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA + Consts.autoB);
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError) {
		     	myCurrentCase = 2;
		     	init = true;
		    } 			
		}
		if (myCurrentCase == 2) {
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())<3) {
				myCurrentCase = 3;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		}
		if (myCurrentCase == 3) {
			if(init) {
				autoDriveFw(Consts.autoE);
			}
			if(Math.abs(posController.getError()) < Consts.autoTurnError) {
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
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA + Consts.autoB + Consts.autoC);
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     	myCurrentCase = 2;
		     	init = true;
		    }		
		}
		if (myCurrentCase == 2) {
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
			}
		if (myCurrentCase == 3) {
			if(init) {
				autoDriveFw(Consts.autoF);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
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
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA + Consts.autoB + Consts.autoC);
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError) {
		     	myCurrentCase = 2;
		     	init = true;
		    }  			
		}
		if (myCurrentCase == 2) {
			if(init) {
				autoTurnDegree(90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		}
		if (myCurrentCase == 3) {
			if(init) {
				autoDriveFw(Consts.autoD);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 4;
	     		init = true;
			}
		}
		if(myCurrentCase == 4) {
			turnController.disable();
			posController.disable();
		}
	}
		
	public void leftSwitchRight() {
		if (myCurrentCase  == 1) {
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 2;
				init = true;
			}
		}
		if (myCurrentCase == 2) {
			if(init) {
				autoTurnDegree(90);
			}
			if(Math.abs(turnController.getError())<Consts.autoTurnError) {
				myCurrentCase = 3;
				init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		}
		if (myCurrentCase == 3) {
			if(init) {
				autoDriveFw(Consts.autoG);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 4;
				init = true;
			}
		}
		if (myCurrentCase == 4) {
			if(init) {
				autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 5;
				init = true;
			}
		}
		if(myCurrentCase == 5) {
			if(init) {
				autoDriveFw(Consts.autoD);
			}
			if(Math.abs(posController.getError())< Consts.autoPosError) {
				myCurrentCase = 6;
				init = true;
			}
		}
		if(myCurrentCase == 6) {
			turnController.disable();
			posController.disable();
		}
	}

	public void rightSwitchLeft() {
		if (myCurrentCase  == 1) {
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 2;
				init = true;
			}
		}
		if (myCurrentCase == 2) {
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
				init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		}
		if (myCurrentCase == 3) {
			if(init) {
				autoDriveFw(Consts.autoG);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 4;
				init = true;
			}
		}
		if (myCurrentCase == 4) {
			if(init) {
				autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 5;
				init = true;
			}
		}
		if(myCurrentCase == 5) {
			if(init) {
				autoDriveFw(Consts.autoD);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 6;
				init = true;
			}
		}
		if(myCurrentCase == 6) {
			turnController.disable();
			posController.disable();
		}
	}
		
	public void leftScaleRight() {
		if (myCurrentCase  == 1) {
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA * 2 + Consts.autoB);
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     	myCurrentCase = 2;
		     	init = true;
		    }
		}
		if (myCurrentCase == 2) {
			if(init) {
				autoTurnDegree(90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
			}
		if (myCurrentCase  == 3) {
			if(init) {
				autoDriveFw(Consts.autoH);
			}
		     if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     		myCurrentCase = 4;
		     		init = true;
		     	}
		}
		if (myCurrentCase == 4) {
			if(init) {
				 autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 5;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
			}
		if (myCurrentCase  == 5) {
			if(init) {
				autoDriveFw(Consts.autoC + Consts.autoA);
			}
		     if(Math.abs(posController.getError()) < Consts.autoPosError) {
		     		myCurrentCase = 6;
		     		init = true;
		     	}
		}
		if (myCurrentCase == 6) {
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 7;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
			}
		if (myCurrentCase  == 7) {
			if(init) {
				autoDriveFw(Consts.autoF);
			}
		     if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     		myCurrentCase = 8;
		     		init = true;
		     	}
		}
		if(myCurrentCase == 8) {
			turnController.disable();
			posController.disable();
		}
	}
	
	public void rightScaleLeft() {
		if (myCurrentCase  == 1) {
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA * 2 + Consts.autoB);
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     	myCurrentCase = 2;
		     	init = true;
		     }
		}
		if (myCurrentCase == 2) {
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
			}
		if (myCurrentCase  == 3) {
			if(init) {
				autoDriveFw(Consts.autoH);
			}
		     if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     	myCurrentCase = 4;
		     	init = true;
		     }
		}
		if (myCurrentCase == 4) {
			if(init) {
				autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 5;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		}
		if (myCurrentCase  == 5) {
			if(init) {
				autoDriveFw(Consts.autoC - Consts.autoA);
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError) {
		     	myCurrentCase = 6;
		     	init = true;
		    }
		}
		if (myCurrentCase == 6) {
			if(init) {
				autoTurnDegree(90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 7;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
			}
		if (myCurrentCase  == 7) {
			if(init) {
				autoDriveFw(Consts.autoF);
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError) {
		     	myCurrentCase = 8;
		     	init = true;
		    }
		}
		if(myCurrentCase == 8) {
			turnController.disable();
			posController.disable();
		}
	}
	public void middleSwitchLeft() {
		if (myCurrentCase == 1) {
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 2;
				init = true;
				}			
		}
		if(myCurrentCase == 2) {
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
				init = true;
			}
		}
		if (myCurrentCase == 3) {
			if(init) {
				autoDriveFw(Consts.autoI);
			}
		
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 4;
				init = true;
			}
		}
		if(myCurrentCase == 4) {
			if(init) {
				autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 5;
				init = true;
			}
		}
		if (myCurrentCase == 5) {
			if(init) {
				autoDriveFw(Consts.autoD);
			}
		
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 6;
				init = true;
			}
		}	
		if(myCurrentCase == 6) {
			turnController.disable();
			posController.disable();
		}
	}
	
	public void middleSwitchRight() {
		if(myCurrentCase == 1) {
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA + Consts.autoD);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 2;
				init = true;
			}
		}
		if (myCurrentCase == 2) {
			turnController.disable();
			posController.disable();
		}
	}

	public void middleScaleLeft() {
		if(myCurrentCase == 1) {
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 2;
				init = true;
			}
		}
		if(myCurrentCase == 2) {
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
		}
		if (myCurrentCase == 3) {
			if(init) {
				autoDriveFw(Consts.autoG);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 4;
				init = true;
			}
		}
		if(myCurrentCase == 4) {
			if(init) {
				autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())<3) {
				myCurrentCase = 5;
	     		init = true;
			}
		}
		if(myCurrentCase == 5) {
			if(init) {
				autoDriveFw(Consts.autoB + Consts.autoC);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 6;
				init = true;
			}
		}
		if(myCurrentCase == 6) {
			if(init) {
				autoTurnDegree(90);
			}
			if(Math.abs(turnController.getError())<3) {
				myCurrentCase = 7;
	     		init = true;
			}
		}
		if(myCurrentCase == 7) {
			if(init) {
				autoDriveFw(Consts.autoF);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 8;
				init = true;
			}
		}
		if(myCurrentCase == 8) {
			turnController.disable();
			posController.disable();
		}
	}
	
	public void middleScaleRight() {
		if(myCurrentCase == 1) {
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 2;
				init = true;
			}
		}
		if(myCurrentCase == 2) {
			if(init) {
				autoTurnDegree(90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
		}
		if (myCurrentCase == 3) {
			if(init) {
				autoDriveFw(Consts.autoG - Consts.autoI);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 4;
				init = true;
			}
		}
		if(myCurrentCase == 4) {
			if(init) {
				autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 5;
	     		init = true;
			}
		}
		if(myCurrentCase == 5) {
			if(init) {
				autoDriveFw(Consts.autoB + Consts.autoC);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 6;
				init = true;
			}
		}
		if(myCurrentCase == 6) {
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 7;
	     		init = true;
			}
		}
		if(myCurrentCase == 7) {
			if(init) {
				autoDriveFw(Consts.autoF);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 8;
				init = true;
			}
		}
		if(myCurrentCase == 8) {
			turnController.disable();
			posController.disable();
		}
	}

	
	public void autoDriveFw(double inches) {
		System.out.println("autoDriveFw was called");
		leftEncoder.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		rightEncoder.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		posController.setSetpoint(inches);
		posController.enable();
		init = false;
	}
	
	public void autoTurnDegree(int degree) {
		leftEncoder.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		rightEncoder.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		if (degree>0) {
			right = false;
		}
		else {
			right = true;
		}
		posController.disable();
		turnDegree(degree);
		init = false;
	}
	
	public void autoDoNothing() {
		turnController.disable();
		posController.disable();
	}
	
	public void putData() {
		SmartDashboard.putNumber("correctionAngle", turnOutput);
	}

	public void autoInit() {
		ahrs.reset();
		turnController.reset();
		posController.reset();
		leftEncoder.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		rightEncoder.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		leftTwo.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		rightTwo.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
	//	leftThree.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		//rightThree.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		LiveWindow.disableAllTelemetry();
		myCurrentCase = 1;	
	}

	public double getRotations(TalonSRX _talon) {
		double distance_ticks = _talon.getSelectedSensorPosition(0);
		double distance_rotations = distance_ticks / Consts.ticksPerRotation;
		return distance_rotations;
	}
	
	public double getTicks(TalonSRX _talon) {
		double distance_ticks = _talon.getSelectedSensorPosition(0);
		return distance_ticks;
	}

	public double getVelocity(TalonSRX _talon) {
		double velocity_milliseconds = (double) _talon.getSelectedSensorVelocity(0) / Consts.ticksPerRotation;
		double velocity_seconds = velocity_milliseconds * Consts.millisecondsPerSecond;
		return velocity_seconds;
	}

	
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
			double fRGetSelected = _frontRight.getSelectedSensorPosition(0);
			double positionInches;
			if(right) {
				positionInches = fRGetSelected * (double) (2 * Math.PI * Consts.wheelRadiusInch) / (double) Consts.ticksPerRotation ;
				SmartDashboard.putString("Right", "Right calling");
			}
			else {
				positionInches = fLGetSelected * (double) (2 * Math.PI * Consts.wheelRadiusInch) / (double) Consts.ticksPerRotation ;
				SmartDashboard.putString("Left", "Left calling");
			}
			return positionInches;
		}

		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}

		public void setPIDSourceType(PIDSourceType pidSource) {
			
		}
	}
}
