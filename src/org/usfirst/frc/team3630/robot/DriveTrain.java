package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.*;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.Faults;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.livewindow.*;
import edu.wpi.first.wpilibj.*;
import com.kauailabs.navx.frc.AHRS;

public class DriveTrain {

	private XboxController _xBox;
	
	PowerDistributionPanel panel;
	private AHRS ahrs;
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
	Timer backwardsTimer;
	
	
	// target angle degrees for straight on should not be a constant !
	double targetAngleDegrees = 0f;
	double kTargetDistanceInches = 1000;

	private WPI_TalonSRX leftThreeEncoder, rightSixEncoder, leftTwo, rightFive;

	
	DifferentialDrive driveTrain;

	// defining PIDSource
	EncoderPIDSource positionEncoderSource;

	/**
	 * leftThree , right six master motors and drive train constru
	 */
	private BoxGrabber driveBox;
	/**
	 * constructer for auto where objects in drie train are constructed
	 * @param _boxGrabber for auto programing 
	 * 
	 */
	public DriveTrain(BoxGrabber _boxGrabber) {
		
		driveBox = _boxGrabber;
		
		ahrs = new AHRS(SPI.Port.kMXP);
		ahrs.setPIDSourceType(PIDSourceType.kDisplacement);
		
		panel = new PowerDistributionPanel(0);
		
		_xBox = new XboxController(Consts.xBoxComPort);
		// srx definitions
		leftThreeEncoder = new WPI_TalonSRX(Consts.leftThree);
		leftTwo = new WPI_TalonSRX(Consts.leftTwo);

		rightSixEncoder = new WPI_TalonSRX(Consts.rightSix);
		rightFive = new WPI_TalonSRX(Consts.rightFive);

		backwardsTimer = new Timer();
		
		configureTalon(leftThreeEncoder);
		configureTalon(rightSixEncoder);
		configureTalon(leftTwo);
		configureTalon(rightFive);

		rightFive.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightSixEncoder.getDeviceID());
		leftTwo.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, leftThreeEncoder.getDeviceID());
		// why differ sensor phase diffrent would it be cosntant for both robots?
		
	
		leftThreeEncoder.setSensorPhase(false);
		rightSixEncoder.setSensorPhase(true);

	
		driveTrain = new DifferentialDrive(leftThreeEncoder, rightSixEncoder);
		driveTrain.setDeadband(0); // why set to zero and not at default ?.02
		turnController = new PIDController(Consts.kPRotAng, Consts.kIRotAng, Consts.kDRotAng, ahrs,new MyRotationPidOutput());

		// setting range and disable it
		turnController.setInputRange(-180.0f, 180.0f);
		ahrs.setPIDSourceType(edu.wpi.first.wpilibj.PIDSourceType.kDisplacement);
		turnController.setOutputRange(-.9, .9); // maybe should lower to .5 if to see if overcompensation
		turnController.setAbsoluteTolerance(Consts.ToleranceDegrees);
		turnController.setContinuous(true);
		turnController.disable();


		positionEncoderSource = new EncoderPIDSource(leftThreeEncoder, rightSixEncoder);
		posController = new PIDController(Consts.kPPos, Consts.kIPos, Consts.kDPos,
		positionEncoderSource, new MyPosPidOutput());
		posController.setOutputRange(-.6, .6); //current testing

		posController.setAbsoluteTolerance(Consts.ToleranceDistance);
		posController.disable();
		

	}


	/**
	 *  set up for test init  */
	public void testInit() {

	}
	
	public void testPeriodic() {

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
		leftThreeEncoder.configOpenloopRamp(.2, Consts.timeOutMs);
		rightSixEncoder.configOpenloopRamp(.2, Consts.timeOutMs);

	}
	public void teleopPeriodic() {
		double speed = (_xBox.getY(GenericHID.Hand.kLeft))*-.6;
		double heading = (_xBox.getX(GenericHID.Hand.kRight))*.6;
		if(_xBox.getTriggerAxis(GenericHID.Hand.kLeft) > .85) {
			speed = (_xBox.getY(GenericHID.Hand.kLeft))*-1;
			heading =  (_xBox.getX(GenericHID.Hand.kRight));
		}
		SmartDashboard.putNumber("heading acrcade drive", heading);
		driveTrain.arcadeDrive(speed, heading);
		getDiagnostics();
		// three are two missing bad? delted folowers set in constructor
		
	SmartDashboard.putNumber("Left three curent", leftThreeEncoder.getOutputCurrent());
	SmartDashboard.putNumber("total voltage ", panel.getVoltage());
	SmartDashboard.putNumber("total current", panel.getTotalCurrent());
	//	SmartDashboard.putNumber("talon left two ", panel.getCurrent(1));
		// moved over to driverStaton warnings 
		// are we still getting curent issues 
//		if(panel.getTotalCurrent()>300) {
//			System.out.print("[WARNING] CURRENT DRAW IS AT ");
//			System.out.print(panel.getTotalCurrent());
//			System.out.print('\n');
//		}

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
//		_talon.config_kP(0, Consts.kPencoder, Consts.timeOutMs);
//		_talon.config_kI(0, Consts.kIencoder, Consts.timeOutMs);
//		_talon.config_kD(0, Consts.kDencoder, Consts.timeOutMs);
		_talon.configNeutralDeadband(0.05, Consts.timeOutMs); // Why do we have 0? 0.025 means a normal 2.5% deadband. might be worth looking at 
		_talon.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		_talon.setInverted(false);
//		_talon.configOpenloopRamp(.1, Consts.timeOutMs);  figure out wheere to sert 
		// where should we set ramp rate???
		///////////////////
	
		
		// Peak current and duration must be exceeded before corrent limit is activated.
		// When activated, current will be limited to continuous current.
	    // Set peak current params to 0 if desired behavior is to immediately current-limit.
		_talon.enableCurrentLimit(true);
		_talon.configContinuousCurrentLimit(30,Consts.timeOutMs); // Must be 5 amps or more
		_talon.configPeakCurrentLimit(30, Consts.timeOutMs); // 100 A
		_talon.configPeakCurrentDuration(200,Consts.timeOutMs); // 200 ms
		
	}

	/**
	 *  diganoaric method for taon srx debuging 
	 */
	public void getDiagnostics() {		
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
		SmartDashboard.putData("PDP", panel);
		
		SmartDashboard.putNumber("turnController kP", turnController.getP());
		
		if(panel.getTotalCurrent()>300) {
			System.out.print("[WARNING] CURRENT DRAW IS AT ");
			System.out.print(panel.getTotalCurrent());
			System.out.print('\n');
		}
		
		fault=leftThreeEncoder.getLastError();
		if(fault != ErrorCode.OK) System.out.println(fault);
//		if (leftEncoder.getOutputCurrent()>35) { 
//			System.out.print("[WARNING] Talon Current is at ");
//			System.out.print(leftEncoder.getOutputCurrent());
//			System.out.print('\n');
//		}
	}
	
	public void getWheelsAndCompass() {
		SmartDashboard.putNumber("Left Current", leftThreeEncoder.getOutputCurrent());
		SmartDashboard.putNumber("Right Current", rightSixEncoder.getOutputCurrent());
		SmartDashboard.putNumber("total Current", panel.getTotalCurrent());
		SmartDashboard.putNumber("Right Wheel Position", getRotations(rightSixEncoder));
		SmartDashboard.putNumber("Right Velocity", getVelocity(rightSixEncoder));
		SmartDashboard.putNumber("Left Wheel Position", getRotations(leftThreeEncoder));
		SmartDashboard.putNumber("Left Velocity", getVelocity(leftThreeEncoder));
		SmartDashboard.putNumber("Left position in ticks", getTicks(leftThreeEncoder));
		SmartDashboard.putNumber("Right position in ticks", getTicks(rightSixEncoder));
		SmartDashboard.putData("ahrs headng", ahrs);
	}
	
	public void autoPeriodic() {
		posOutput = posController.get();
		driveTrain.arcadeDrive(posOutput, turnOutput);
	}
	
	/**
	 *  auto methods for each auto combintation 
	 */
	public void leftSwitchLeft() {
		if (myCurrentCase == 1) {
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				turnController.setPID(Consts.kPDrAngle, Consts.kIDrAngle, Consts.kDDrAngle);
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
	public void leftSwitchLeftFF() {
		if (myCurrentCase == 1) {
			//ENTER CONDITION
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				turnController.setPID(Consts.kPDrAngle, Consts.kIDrAngle, Consts.kDDrAngle);
				autoDriveFw(Consts.firstDistanceInSwitchFFMethod);
				driveBox.switchAutoUpInit();
			}
			//
		    if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     	myCurrentCase = 2;
		     	init = true;
		    }
		} 
		if (myCurrentCase == 2) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(35);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		}
		if (myCurrentCase == 3) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.secondDistanceInSwitchFFMethod);
				
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 4;
	     		init = true;
	     		driveBox.liftUpSwitchActivated = false;
			}
		}
		if(myCurrentCase == 4) {
			//ENTER CONDITION
			if(init) {
				init = false;
				driveBox.kickoutInit();
				System.out.println("Case four called");
			}
				turnController.disable();
				posController.disable();
		}
	}



	public void rightSwitchRight() {
		if (myCurrentCase  == 1) {
			//ENTER CONDITION
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
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn"
					+ " Target", posController.onTarget());
		}
		if (myCurrentCase == 3) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoE);
			}
			if(Math.abs(posController.getError()) < Consts.autoTurnError) {
				myCurrentCase = 4;
	     		init = true;
			}
		}
		if(myCurrentCase == 4) {
			//ENTER CONDITION
			if(init) {
				turnController.disable();
				posController.disable();
				//driveBox.switchAuto();
				init = false;
			}
		}
	}
	public void rightSwitchRightFF() {
		if (myCurrentCase  == 1) {
			//ENTER CONDITION
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.firstDistanceInSwitchFFMethod);
				driveBox.switchAutoUpInit();
				
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError) {
		      	myCurrentCase = 2;
			     init = true;
		    } 			
		}
		if (myCurrentCase == 2) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(-45);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		}
		if (myCurrentCase == 3) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.secondDistanceInSwitchFFMethod);
						
			}
			if(Math.abs(posController.getError()) < Consts.autoTurnError) {
				myCurrentCase = 4;
	     		init = true;
			}
		}
			if(myCurrentCase == 4) {
				//ENTER CONDITION
				if(init) {
					init = false;
					driveBox.kickoutInit();
				}
					turnController.disable();
					posController.disable();
			}
		}
	
	
	public void rightScaleRight() {
		if (myCurrentCase  == 1) {
			//ENTER CONDITION
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.firstDistanceInScaleFFMethod);
				driveBox.liftUpInit();
			
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     	myCurrentCase = 2;
		     	init = true;
		    }		
		}
		if (myCurrentCase == 2) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(-45);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
			}
		if (myCurrentCase == 3) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.secondDistanceInScaleFFMethod);				
				
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 4;
	     		init = true;
			}
		}
			
		
		if(myCurrentCase == 4) {
			//ENTER CONDITION
			if(init){
				init =false;
				driveBox.kickoutInit();
			}
			turnController.disable();
			posController.disable();
		}	
			
		}	
		
	public void leftScaleLeft() {
		if (myCurrentCase  == 1) {
			//ENTER CONDITION
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.firstDistanceInScaleFFMethod);
				driveBox.liftUpInit();
				
			}
			
		    if(Math.abs(posController.getError()) < Consts.autoPosError) {
		     	myCurrentCase = 2;
		     	init = true;
		    }  			
		}
		if (myCurrentCase == 2) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(35);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 4;
	     		init = true;
			}
			SmartDashboard.putBoolean("Hit Turn Target", posController.onTarget());
		}
		if (myCurrentCase == 3) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.secondDistanceInScaleFFMethod);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 4;
	     		init = true;
			}
		}
		if(myCurrentCase == 4) {
			//ENTER CONDITION
			if(init){
				init = false;
				driveBox.kickoutInit();
		}
			turnController.disable();
			posController.disable();
	}
	}
		
	/**
	 * dose this method work seems to have comented out unfinshed pices of code? just want to double chack if something needs to be finished? or is missing
	 */
	public void leftSwitchRight() {
		if (myCurrentCase  == 1) {
			//ENTER CONDITION
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA);
				driveBox.switchAutoUpInit();
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 2;
				init = true;
			}
		}
		if (myCurrentCase == 2) {
			//ENTER CONDITION
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
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoG);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 4;
				init = true;
			}
		}
		if (myCurrentCase == 4) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 5;
				init = true;
			}
		}
		if(myCurrentCase == 5) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoD);
			}
			if(Math.abs(posController.getError())< Consts.autoPosError) {
				myCurrentCase = 6;
				init = true;
			}
		}
		if(myCurrentCase == 6) {
			if(init) {
				init = false;
				driveBox.kickoutInit();
			}
			turnController.disable();
			posController.disable();
		}
	}

	public void rightSwitchLeft() {
		if (myCurrentCase  == 1) {
			//ENTER CONDITION
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA);
				driveBox.switchAutoUpInit();
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 2;
				init = true;
			}
		}
		if (myCurrentCase == 2) {
			//ENTER CONDITION
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
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoG);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 4;
				init = true;
			}
		}
		if (myCurrentCase == 4) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 5;
				init = true;
			}
		}
		if(myCurrentCase == 5) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoD);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 6;
				init = true;
			}
		}
		if(myCurrentCase == 6) {
			//ENTER CONDITION
			if(init) {
				init = false;
				driveBox.kickoutInit();
			}
			turnController.disable();
			posController.disable();
		}
	}
		
	public void leftScaleRight() {
		//ENTER CONDITION
		if (myCurrentCase  == 1) {
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA * 2 + Consts.autoB);
				driveBox.liftUpInit();
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     	myCurrentCase = 2;
		     	init = true;
		    }
		}
		if (myCurrentCase == 2) {
			//ENTER CONDITION
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
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoH);
			}
		     if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     		myCurrentCase = 4;
		     		init = true;
		     	}
		}
		if (myCurrentCase == 4) {
			//ENTER CONDITION
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
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoF);
			}
		     if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     		myCurrentCase = 8; /// MADE NINE
		     		init = true;
		     	}
		}
		if(myCurrentCase == 8) {
			if(init) {
				init = true;
				driveBox.kickoutInit();
			}
			turnController.disable();
			posController.disable();
	
		}
		
		/*if(myCurrentCase == 9) {   //NO IDEA WHAT THIS IS DOING
			//ENTER CONDITION
			if(init){
				turnController.disable();
				posController.disable();
				driveBox.liftUpInit();
				init =false;
				
			}
			else if(driveBox.liftUpActivated) {
				driveBox.liftUpPeriodic();
				
			}
			else {
				myCurrentCase = 10;
				init = true;
			}
		
			
		}
		///////////FIX FIX FIX FIX
		if (myCurrentCase == 10) {
			//ENTER CONDITION
/*		if (init) {
			driveBox.kickoutInit();
			if(driveBox.isKickoutActivated) {
				driveBox.kickoutPeriodic();
				init = false;
			}
			else if(driveBox.isKickoutActivated) {
				driveBox.kickoutPeriodic();
				
			}
			
			else {
				init = false;
			}
		}*/
	}
	public void rightScaleLeft() {
		if (myCurrentCase  == 1) {
			if(init) {
				//ENTER CONDITION
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA * 2 + Consts.autoB);
				driveBox.liftUpInit();
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     	myCurrentCase = 2;
		     	init = true;
		     }
		}
		if (myCurrentCase == 2) {
			//ENTER CONDITION
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
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoH);
			}
		     if(Math.abs(posController.getError()) < Consts.autoPosError ) {
		     	myCurrentCase = 4;
		     	init = true;
		     }
		}
		if (myCurrentCase == 4) {
			//ENTER CONDITION
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
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoC - Consts.autoA);
			}
		    if(Math.abs(posController.getError()) < Consts.autoPosError) {
		     	myCurrentCase = 6;
		     	init = true;
		    }
		}
		if (myCurrentCase == 6) {
			//ENTER CONDITION
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
			//ENTER CONDITION
			if(init) {
				init = false;
				driveBox.kickoutInit();
			}
			turnController.disable();
			posController.disable();
		}
	}

	public void middleSwitchLeft() {
		if (myCurrentCase == 1) {
			//ENTER CONDITION
			if(init) {
				turnController.enable();
				turnController.setSetpoint(0);
				autoDriveFw(Consts.autoA);
				driveBox.switchAutoUpInit();
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 2;
				init = true;
				}			
		}
		
		if (myCurrentCase == 2) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(12);
			}
		
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 3;
				init = true;
			}
		}
		
		if(myCurrentCase == 3) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 4;
				init = true;
			}
		}
		if (myCurrentCase == 4) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoI);
			}
		
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 5;
				init = true;
			}
		}
		if(myCurrentCase == 5) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 6;
				init = true;
			}
		}
		if (myCurrentCase == 6) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoD-12);
			}
		
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 7;
				init = true;
			}
		}	
		if(myCurrentCase == 7) {
			if(init) {
				init = false;
				driveBox.kickoutInit();
			}
			turnController.disable();
			posController.disable();
		}
	}
	public void middleSwitchLeftFF() { //started. No right angles. Need a lot of testing
		
		if(myCurrentCase == 1 || myCurrentCase == 2) {
//
	//		if(init) {
				System.out.println("Case 1/2 callled");
				autoDriveFw(36);
	//	}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
		}
		if(myCurrentCase == 3) {
			if(init) {
				turnController.setPID(.03, Consts.kIDrAngle, Consts.kDDrAngle);
			driveBox.switchAutoUpInit();
				turnController.enable();
				autoTurnDegree(-60);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 4;
	     		init = true;
			}
		}
		if(myCurrentCase == 4) {
			if(init){
				turnController.setPID(.02, Consts.kIRotAng, Consts.kDRotAng);
				autoDriveFw(110);//162.3);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 5;
				init = true;
			}
		}
		if(myCurrentCase == 5) {
			if(init) {
				autoTurnDegree(10);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 6;
	     		init = true;
			}
		}
		if(myCurrentCase == 6) {
			if(init) {
				autoDriveFw(30);//63.6);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 7;
				init = true;
			}
		}
		if(myCurrentCase == 7) {
			//ENTER CONDITION
			if(init){
			init = false;
				driveBox.kickoutInit();	
		}
			turnController.disable();
			posController.disable();
		}
		
	}
	
	public void middleSwitchRight() {
		if(myCurrentCase == 1) {
			//ENTER CONDITION
			if(init) {
				driveBox.switchAutoUpInit();
				driveBox.switchAutoUpPeriodic();
				Timer.delay(1);
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
			//ENTER CONDITION
			if(init) {
				if (!driveBox.liftUpSwitchActivated) {
					driveBox.kickoutInit();
					init = false;	
				}	
			}
			turnController.disable();
			posController.disable();
		}
	}

	public void middleScaleLeft() { //left unchanged. Probably woun't be using
		if(myCurrentCase == 1) {
			//ENTER CONDITION
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
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
		}
		if (myCurrentCase == 3) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoG);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 4;
				init = true;
			}
		}
		if(myCurrentCase == 4) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())<3) {
				myCurrentCase = 5;
	     		init = true;
			}
		}
		if(myCurrentCase == 5) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoB + Consts.autoC);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 6;
				init = true;
			}
		}
		if(myCurrentCase == 6) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(90);
			}
			if(Math.abs(turnController.getError())<3) {
				myCurrentCase = 7;
	     		init = true;
			}
		}
		if(myCurrentCase == 7) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoF);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError) {
				myCurrentCase = 8;
				init = true;
			}
		}
		if(myCurrentCase == 8) {
			//ENTER CONDITION
			turnController.disable();
			posController.disable();
			
			init = false;
		}
	}
	
	public void middleScaleRight() { ////left unchanged. Probably woun't be using
		if(myCurrentCase == 1) {
			//ENTER CONDITION
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
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 3;
	     		init = true;
			}
		}
		if (myCurrentCase == 3) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoG - Consts.autoI);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 4;
				init = true;
			}
		}
		if(myCurrentCase == 4) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(0);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 5;
	     		init = true;
			}
		}
		if(myCurrentCase == 5) {
			//ENTER CONDITION
			if(init) {
				autoDriveFw(Consts.autoB + Consts.autoC);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 6;
				init = true;
			}
		}
		if(myCurrentCase == 6) {
			//ENTER CONDITION
			if(init) {
				autoTurnDegree(-90);
			}
			if(Math.abs(turnController.getError())< Consts.autoTurnError) {
				myCurrentCase = 7;
	     		init = true;
			}
		}
		if(myCurrentCase == 7) {
			//ENTER CONDITION
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
			
			init = false;
		}
	}
	
	
	public void autoDriveFw(double inches) {
		turnController.setPID(Consts.kPDrAngle, Consts.kIDrAngle, Consts.kDDrAngle);
		leftThreeEncoder.configOpenloopRamp(.1, Consts.timeOutMs);
		rightSixEncoder.configOpenloopRamp(.1, Consts.timeOutMs);
		System.out.println("autoDriveFw was called");
		leftThreeEncoder.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		rightSixEncoder.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		posController.setSetpoint(inches);
		posController.enable();
		init = false;
	}
	
	/**
	 * @param degree
	 * @return which motor to use for turning 
	 */
	public void autoTurnDegree(int degree) {
		turnController.setPID(Consts.kPRotAng, Consts.kIRotAng, Consts.kDRotAng);
		leftThreeEncoder.configOpenloopRamp(0.1, Consts.timeOutMs);
		rightSixEncoder.configOpenloopRamp(0.1, Consts.timeOutMs);
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
	
	public void driveAutoLine() {
		if(myCurrentCase == 1) {
			//ENTER CONDITION
			if(init) {
				turnController.enable();
				//turnController.setSetpoint(0);
				autoDriveFw(Consts.autoLine);
			}
			if(Math.abs(posController.getError()) < Consts.autoPosError ) {
				myCurrentCase = 2;
				init = true;
			}
		}
		if (myCurrentCase == 2) {
			//ENTER CONDITION
			turnController.disable();
			//posController.disable();
		}
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
		leftThreeEncoder.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		rightSixEncoder.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		// why setting ramp rate here? we aren't doing this for telop we should do this once not twice
		leftThreeEncoder.configOpenloopRamp(1, Consts.timeOutMs);
		rightSixEncoder.configOpenloopRamp(1, Consts.timeOutMs);
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
		double velocity_seconds = velocity_milliseconds *10* 6*Math.PI*.0254; 
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
				//(2 * Math.PI * Consts.wheelRadiusInch) make this a contant !!
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
