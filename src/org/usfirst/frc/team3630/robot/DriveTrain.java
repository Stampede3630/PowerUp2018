package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.*;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.Faults;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
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
	
	double turnOutput;
	double posOutput;
	boolean errorGreatorThanFive = false;
	boolean init = true;
	boolean right = true;
	int myCurrentCase;		
	PIDController turnController;
	PIDController posController;
	double rotateToAngleRate;
	

	// target angle degrees for straight on should not be a constant !
	double targetAngleDegrees = 0f;
	double kTargetDistanceInches = 1000;

	private WPI_TalonSRX leftThree, rightSix, leftTwo, rightFive, leftOne, rightFour;

	
	DifferentialDrive driveTrain;

	// defining PIDSource
	EncoderPIDSource positionEncoderSource;

	/**
	 * leftThree , right six master motors and drive train constru
	 */
	public DriveTrain() {
		// why doing ahrs byte thing? // do we use update rate elswhere 
		ahrs = new AHRS(SPI.Port.kMXP,(byte) 200);
		ahrs.setPIDSourceType(PIDSourceType.kDisplacement);
		panel = new PowerDistributionPanel();
		_xBox = new XboxController(Consts.xBoxComPort);
		// srx definitions
		leftThree = new WPI_TalonSRX(Consts.leftThree);
		leftTwo = new WPI_TalonSRX(Consts.leftTwo);
		leftOne = new WPI_TalonSRX(Consts.leftOne);
		rightSix = new WPI_TalonSRX(Consts.rightSix);
		rightFive = new WPI_TalonSRX(Consts.rightFive);
		rightFour = new WPI_TalonSRX(Consts.rightFour);
		
		// mabey rename to leftThreeMaster? nice more specific name 
		configureTalon(leftThree);
		configureTalon(rightSix);
		configureTalon(leftTwo);
		configureTalon(rightFive);
		configureTalon(leftOne);
		configureTalon(rightFour);
		rightFive.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightSix.getDeviceID());
		leftTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftThree.getDeviceID());
		rightFour.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightSix.getDeviceID());
		leftOne.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftThree.getDeviceID());
		// why differ sensor phase diffrent would it be cosntant for both robots?
		leftThree.setSensorPhase(false);
		rightSix.setSensorPhase(true);

		


	
		driveTrain = new DifferentialDrive(leftThree, rightSix);
		driveTrain.setDeadband(0); // why set to zero and not at default ?.02
		turnController = new PIDController(Consts.kPRotAng, Consts.kIRotAng, Consts.kDRotAng, ahrs,new MyRotationPidOutput());

		// setting range and disable it
		turnController.setInputRange(-180.0f, 180.0f);
		ahrs.setPIDSourceType(edu.wpi.first.wpilibj.PIDSourceType.kDisplacement);
		turnController.setOutputRange(-.9, .9); // maybe should lower to .5 if to see if overcompensation
		turnController.setAbsoluteTolerance(Consts.ToleranceDegrees);
		turnController.setContinuous(true);
		turnController.disable();


		positionEncoderSource = new EncoderPIDSource(leftThree, rightSix);
		posController = new PIDController(Consts.kPPos, Consts.kIPos, Consts.kDPos,
		positionEncoderSource, new MyPosPidOutput());
		posController.setOutputRange(-.6, .6); //current testing
		posController.setAbsoluteTolerance(Consts.ToleranceDistance);
		posController.disable();
		

	}

	
	/**
	 *  set up for test init  */
	public void testInit() {
		// should delite unless someone can explain why we have it still
	/*	SmartDashboard.putNumber("Left Side Speed", 0);
		SmartDashboard.putNumber("Right Side Speed", 0);
		rightSix.setSelectedSensorPosition(0, 0, Consts.timeOutMs);*/
	}
	
	public void testPeriodic() {
		// should delite unless someone can explain why we have it still
	/*	leftThree.set(SmartDashboard.getNumber("Left Side Speed", 0));
		rightSix.set(SmartDashboard.getNumber("Right Side Speed", 0));
		// mising a few talons 
		rightFive.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightSix.getDeviceID());
		leftTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftThree.getDeviceID());
		// why comment theese out names 
		//rightThree.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightEncoder.getDeviceID());
		//leftThree.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftEncoder.getDeviceID());
		SmartDashboard.putNumber("Right Encoder Ticks", rightSix.getSelectedSensorPosition(0));*/
	}

	/**
	 * @return ahrs yaw value from -180 to 180 degrees 
	 */
	public double ahrsYaw() {
		double yaw = ahrs.getYaw();
		return yaw;
	}
	
	// add ahrs  congif method to see if calibating. It could be a good saftey checlk 
	public void teleopInit() {
		leftThree.configOpenloopRamp(0, Consts.timeOutMs);
		rightSix.configOpenloopRamp(0, Consts.timeOutMs);

	}
	public void teleopPeriodic() {
		double speed = (_xBox.getY(GenericHID.Hand.kLeft))*-1;
		double heading = (_xBox.getX(GenericHID.Hand.kRight));
		SmartDashboard.putNumber("heading acrcade drive", heading);
		driveTrain.arcadeDrive(speed, heading);
		getDiagnostics();
		// three are two missing bad? delted folowers set in constructor
		
	SmartDashboard.putNumber("Left three curent", leftThree.getOutputCurrent());
	SmartDashboard.putNumber("total voltage ", panel.getVoltage());
	SmartDashboard.putNumber("total current", panel.getTotalCurrent());
		SmartDashboard.putNumber("talon left two ", panel.getCurrent(1));
		
		// are we still getting curent issues 
		if(panel.getTotalCurrent()>300) {
			System.out.print("[WARNING] CURRENT DRAW IS AT ");
			System.out.print(panel.getTotalCurrent());
			System.out.print('\n');
		}

	}

	/**
	 * @param _talon
	 * set up  for tann initatioation
	 */
	private void configureTalon(TalonSRX _talon) {
		_talon.configNominalOutputForward(0, Consts.timeOutMs);
		_talon.configNominalOutputReverse(0, Consts.timeOutMs);
		_talon.configPeakOutputForward(1, Consts.timeOutMs);
		_talon.configPeakOutputReverse(-1, Consts.timeOutMs);
		_talon.configAllowableClosedloopError(0, 0, Consts.timeOutMs);
		_talon.config_kP(0, Consts.kPencoder, Consts.timeOutMs);
		_talon.config_kI(0, Consts.kIencoder, Consts.timeOutMs);
		_talon.config_kD(0, Consts.kDencoder, Consts.timeOutMs);
		_talon.configNeutralDeadband(0, Consts.timeOutMs); // Why do we have 0? 0.025 means a normal 2.5% deadband. might be worth looking at 
		_talon.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		_talon.setInverted(false);
//		_talon.configOpenloopRamp(.1, Consts.timeOutMs);  figure out wheere to sert 
		// where should we set ramp rate???
		///////////////////
	
		
	// Peak current and duration must be exceeded before corrent limit is activated.
	// When activated, current will be limited to continuous current.
    // Set peak current params to 0 if desired behavior is to immediately current-limit.
	//	_talon.enableCurrentLimit(true);
	//	_talon.configContinuousCurrentLimit(10,0); // Must be 5 amps or more
	//	_talon.configPeakCurrentLimit(10, 0); // 100 A
		//_talon.configPeakCurrentDuration(200,0); // 200 ms
		
	}

	/**
	 *  diganoaric method for taon srx debuging 
	 */
	public void getDiagnostics() {		
		SmartDashboard.putNumber("Left Current", leftThree.getOutputCurrent());
		SmartDashboard.putNumber("Right Current", rightSix.getOutputCurrent());
		
		SmartDashboard.putNumber("Front Right Position", getRotations(rightSix));
		SmartDashboard.putNumber("Front Right Velocity", getVelocity(rightSix));
		SmartDashboard.putNumber("Front Left Position", getRotations(leftThree));
		SmartDashboard.putNumber("Front Left Velocity", getVelocity(leftThree));
		SmartDashboard.putNumber("Left position in ticks", getTicks(leftThree));
		SmartDashboard.putNumber("Right position in ticks", getTicks(rightSix));
	
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
		
		fault=leftThree.getLastError();
		if(fault != ErrorCode.OK) System.out.println(fault);
//		if (leftEncoder.getOutputCurrent()>35) { 
//			System.out.print("[WARNING] Talon Current is at ");
//			System.out.print(leftEncoder.getOutputCurrent());
//			System.out.print('\n');
//		}
	}
	
	public void autoPeriodic() {
		driveTrain.arcadeDrive(posOutput, turnOutput);
	}
	
	/**
	 *  auto methods for each auto combentation 
	 */
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
		leftThree.configOpenloopRamp(.1, Consts.timeOutMs);
		rightSix.configOpenloopRamp(.1, Consts.timeOutMs);
		System.out.println("autoDriveFw was called");
		leftThree.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		rightSix.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		posController.setSetpoint(inches);
		posController.enable();
		init = false;
	}
	
	/**
	 * @param degree
	 * @return which motor to use for turning 
	 */
	public void autoTurnDegree(int degree) {
		leftThree.configOpenloopRamp(0, Consts.timeOutMs);
		rightSix.configOpenloopRamp(0, Consts.timeOutMs);
		if (degree<0) {
			right = true;
		}
		else {
			right = false;
		}
		posController.disable();
		turnDegree(degree);
		init = false;
	}

	// init method for navx calibaration setting
	
	public void turnDegree(double degrees) {
		targetAngleDegrees = degrees;
		turnController.setSetpoint(targetAngleDegrees);
	}


	public void autoDoNothing() {
		turnController.disable();
		posController.disable();
	}
	
	public void putData() {
		SmartDashboard.putNumber("correctionAngle", turnOutput);
	}

	public void stop() {
		driveTrain.arcadeDrive(0, 0);
	}


	/**
	 * init method for auto. reset sensor position. note setting ramp rate in method
	 */
	public void autoInit() {
		ahrs.reset();
		turnController.reset();
		posController.reset();
		leftThree.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		rightSix.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		// why setting ramp rate here? we aren't doing this for telop we should do this once not twice
		leftThree.configOpenloopRamp(.1, Consts.timeOutMs);
		rightSix.configOpenloopRamp(.1, Consts.timeOutMs);
		LiveWindow.disableAllTelemetry();
		myCurrentCase = 1;	

	}

	/**
	 * @param _talon
	 * @return actual rrotation of talon in a rotation of the wheel 
	 */
	public double getRotations(TalonSRX _talon) {
		double distance_ticks = _talon.getSelectedSensorPosition(0);
		double distance_rotations = distance_ticks / Consts.ticksPerRotation;
		return distance_rotations;
	}
	
	public double getTicks(TalonSRX _talon) {
		double distance_ticks = _talon.getSelectedSensorPosition(0);
		return distance_ticks;

	}

	/**
	 * @param _talon
	 * @return velocity in in/ second. from native taon units 
	 */
	public double getVelocity(TalonSRX _talon) {
		double velocity_milliseconds = (double) _talon.getSelectedSensorVelocity(0) / Consts.ticksPerRotation;
		double velocity_seconds = velocity_milliseconds * Consts.millisecondsPerSecond;
		return velocity_seconds;
	}

	

	/**
	 * method for gtting pos output for pid controllor 
	 *
	 */
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
