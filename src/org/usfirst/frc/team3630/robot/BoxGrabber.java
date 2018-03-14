package org.usfirst.frc.team3630.robot;

import edu.wpi.first.wpilibj.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;

public class BoxGrabber {


	// enum difftent state= state (F, forward) (R, piston reverse)
	// lift
	public enum State {
		LIFTUP,
		LIFTDOWN,
		
		SLIDEFORWARD,
		SLIDEBACK,
		
		CLAMPOPEN,
		CLAMPCLOSE,
		
		KICKFORWARD,
		KICKRETRACT,
		
		INTAKE,
		
		DROPBOX,
		
		STOP,
		
		SWITCHUPAUTOMATED,
		
		LOWSCALEUPAUTOMATED,
		
		LIFTUPAUTOMATED,
		LIFTDOWNAUTOMATED,
		
		SWITCHDOWNAUTOMATED, 
		
		MANUALCONTROL,
		STOPOVERRIDE
		//SLIDEFORWARD, CLAMPOPEN, KICKFORWARD, LIFTUP, LIFTDOWN, CLAMPCLOSE, SLIDEBACK, KICKRETRACT, STOP, INTAKE, DROPBOX, SWITCHDOWNAUTOMATED, SWITCHUPAUTOMATED, SCALEAUTOMATED, LIFTDOWNAUTOMATED, LIFTUPAUTOMATED, LOWSCALEUPAUTOMATED
		//liftup, liftdown, kickforward, kickretract never used
	}

	private XboxController _xBox;
	// private TalonSRX leftIntake, rightIntake;
	// name double solonoid
	DoubleSolenoid slide, clamp, kick, lift;
	Compressor mainC;
	// debug analog input
	boolean testOn;
	boolean isKickoutActivated;
	boolean liftUpSensorFlag,liftUpActivated;
	boolean liftDownSensorFlag,liftDownActivated; 
	boolean liftUpSwitchActivated, liftUpSwitchSensorFlag;
	boolean liftDownSwitchActivated,liftDownSwitchSensorFlag;
	boolean liftUpLowScaleSensorFlag, liftUpLowScaleActivated;
	boolean routineRunning;
	boolean atSwitch, atLowScale,atScale;
	AnalogInput pressureLevel;
	DigitalInput slideReversecheck;
	Timer liftTimer,slideTimer, kickTime;
	int kickoutState;
	double partysOverDown;

	AnalogInput scaleUpTrigger, atDownLevel;

	public BoxGrabber() {

		// peramtors for double soelnoid pcm, in chanel, out chanel
		// for detils on solondid asighning see output sheet i posted on slack
		slideTimer = new Timer();
		kickTime = new Timer();
		liftTimer = new Timer();
		
		slideReversecheck = new DigitalInput(3);
		
		
		isKickoutActivated= false;
		
		liftUpSensorFlag= false;
		liftUpActivated = false;
		liftDownSensorFlag=false;
		liftDownActivated = false;
		liftDownSwitchSensorFlag = false;
		liftDownSwitchActivated = false;
		
		liftUpSwitchSensorFlag = false;
		liftUpSwitchActivated = false;
		
		atSwitch = false;
		atScale = false;
		atLowScale = false;
		testOn = false;

		scaleUpTrigger = new AnalogInput(Consts.scaleUpAnalogPin);
		atDownLevel = new AnalogInput(Consts.downLevelAnalogPin);
		
		slide = new DoubleSolenoid(1, 2, 3);
		kick = new DoubleSolenoid(0, 0, 1);
		clamp = new DoubleSolenoid(0, 2, 3);
		lift = new DoubleSolenoid(1, 0, 1);
		
		mainC = new Compressor(0);
		pressureLevel = new AnalogInput(Consts.pressureLevelAnalogPin);
		pressureLevel.setOversampleBits(8);
		pressureLevel.setAverageBits(13);

		_xBox = new XboxController(Consts.xBoxComPort);
	}

	
// to do make button asighnments sane
	public State xBox() {
		if(_xBox.getPOV() != -1){
			return State.MANUALCONTROL;
		}
		else if (_xBox.getXButton()) {
			return State.LOWSCALEUPAUTOMATED;
		}
		else if (_xBox.getYButton()) {
			return State.LIFTUPAUTOMATED;	
		}
		else if (_xBox.getAButton()) {
			return State.LIFTDOWNAUTOMATED;	
		} 
		else if (_xBox.getBButton()) {
			return State.SWITCHUPAUTOMATED;
		}
		else if (_xBox.getBumper(GenericHID.Hand.kRight)) {
			return State.CLAMPCLOSE;
		}
		else if (_xBox.getBumper(GenericHID.Hand.kLeft)) {
			return State.CLAMPOPEN;
		}

		else if (_xBox.getBackButton()) {
			return State.STOPOVERRIDE;
		}
		else {
			return State.STOP;
		}  
	}

	public void manualControl() {
			routineRunning = false;
			double angle = _xBox.getPOV()*Math.PI/180;
			double liftControl = Math.cos(angle);
			double slideControl = Math.sin(angle);
			if(liftControl > 0) {
				armsUp();
			}
			else if (liftControl < 0) {
				armsDown();
			}
			else {
				armsStop();
			}
			
			
			if(slideControl < 0) {
				slideReverse();
			}
			else if(slideControl > 0) {
				slideForward();
				
			}
			else {
				slideOff();
			}
	}
	/*
	 * public void intake() { rightIntake.configNeutralDeadband(.1, 10);
	 * leftIntake.configNeutralDeadband(.1, 10);
	 * leftIntake.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower,
	 * rightIntake.getDeviceID());
	 * rightIntake.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput,
	 * _xBox.getTriggerAxis(GenericHID.Hand.kLeft));
	 * 
	 * }
	 */

	// each method for has a forward and reverse
	// sets a bollean to true in order to know it has ben activated

	
	
	// base class methods 
	public void kickForward() {
		kick.set(DoubleSolenoid.Value.kForward);
	}

	public void kickReverse() {
		kick.set(DoubleSolenoid.Value.kReverse);
	}

	public void armsUp() {
		lift.set(DoubleSolenoid.Value.kForward);
	}
	
	public void armsStop() {
		lift.set(DoubleSolenoid.Value.kOff);
	}

	public void armsDown() {
		lift.set(DoubleSolenoid.Value.kReverse);
	}

	public void clampOpen() {
		clamp.set(DoubleSolenoid.Value.kForward);
		}

	public void clampClose() {
		clamp.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void slideReverse() {
		slide.set(DoubleSolenoid.Value.kReverse);
	}

	public void slideOff() {
		slide.set(DoubleSolenoid.Value.kOff);
	}
	
	// stop method for safety
	public void stop() {
		clamp.set(DoubleSolenoid.Value.kOff);
		lift.set(DoubleSolenoid.Value.kOff);
		kick.set(DoubleSolenoid.Value.kOff);
	}

	public void slideForward() {
		slide.set(DoubleSolenoid.Value.kForward);
	}

	
	// psi method for pressure sensor need to calibrate normalized voltage during
	// testing
	public double compresorPSI() {
		double sensorV = pressureLevel.getVoltage();
		double psi = 250 * (sensorV / 5) - 25;
		return Math.round(psi);
	}

	public void lowPSIWarning() {
		if (compresorPSI() < 60.0) {
			System.out.println("WARNING robots dont have low pressure but yours does");
		}
	}

	
	public void kickoutInit(){
		kickTime.reset();
		kickoutState = 1;
		isKickoutActivated = true;
}
	
/*	public void  kickoutPeriodic(){
		if (isKickoutActivated){
			switch(kickoutState){
			case 1:
				System.out.println("case one");
				kickTime.start();
				kickoutState=2;
				break;
			case 2:
				clampOpen();
				System.out.println("case two");
				if (kickTime.hasPeriodPassed(.01)){
					kickoutState=3;
				}
				break;
			case 3:
				System.out.println("case three");
				kickForward();

				if (kickTime.hasPeriodPassed(.5)){
					kickoutState=4;
				}
				break; 
			case 4:
				kickReverse();
				System.out.println("case four");
				if (kickTime.hasPeriodPassed(.6)){
					kickoutState =5;
				}
				break; 
			case 5:
				//clampClose();
				System.out.println("case five ");

				if (kickTime.hasPeriodPassed(.004)){
					kickoutState = -1;
					isKickoutActivated = false;
					kickTime.stop();
					System.out.print("isKickoutactivated boolean in case five");
					System.out.print(isKickoutActivated);
				}
				break; 
			default:
				System.out.print("WARNING kickout method caught exception");
				isKickoutActivated = false;
				stop();
			}
	
			
}
	}*/

	public void liftUpInit () {
		liftTimer.reset();
		liftUpActivated = true;
		liftUpSensorFlag = false;
		liftTimer.start();
//		System.out.println("lift up init is being called");
		atScale = true;
		atLowScale = false;
		atSwitch = false;
	}
	
	public void liftUpPeriodic() {
		if (liftUpActivated) {
			if (liftTimer.get() > Consts.partysOverScaleUp) {
				stop();
				liftUpActivated = false;
				liftUpSensorFlag= false;
				System.out.println("Party's over");
			}
			else if (liftUpSensorFlag) {
				System.out.println("slide forward is called for lift up");
				slideForward();
			}
			else {
				System.out.println(" slide revse & arms up is called for lift up");
				slideReverse();
				armsUp();
				if (scaleUpTrigger.getVoltage() > 2 ) {
					liftUpSensorFlag= true;
					//						 System.out.println("liftUp sensor flag = ");
					//						 System.out.println(liftUpSensorFlag);
				}
			}
		}

	}
	public void liftDownInit () {
		
		
		liftDownSensorFlag = false;
		liftDownActivated = true;
		
		liftTimer.reset();
		liftTimer.start();

//		System.out.println("lift down init is being called");
		
		if (atSwitch) {
			partysOverDown = Consts.partysOverSwitchDown;
		}
		else if (atLowScale) {
			partysOverDown = Consts.partysOverLowScaleDown;
		}
		else {
			partysOverDown = Consts.partysOverScaleDown;
		}
//		System.out.println("Party's over value ");
//		System.out.print(partysOverDown);
		atSwitch = false;
		atScale = false;
		atLowScale = false;
		
	}
	
	public void liftDownPeriodic() {
		if (liftDownActivated) {
			if (liftTimer.get() > partysOverDown) {
				liftDownActivated = false;
				liftDownSensorFlag = false;
				System.out.println("Party's over");
				stop();
			}
			
			else if(liftDownSensorFlag) {
				System.out.println("slide forwad called for lift down");
				slideForward();
			}
			else if(liftTimer.hasPeriodPassed(.2)) {
				System.out.println("arms down called for lift down");
				armsDown();
				if (atDownLevel.getVoltage()>  2 ) {
					liftDownSensorFlag= true;
					 System.out.println("liftUp sensor flag = ");
					 System.out.println(liftUpSensorFlag);
				}
			}
			else {
				slideReverse();
			//	armsDown();
				System.out.println("slide reverse called for lift down");
				if (atDownLevel.getVoltage()>  2 ) {
					liftDownSensorFlag= true;
					 System.out.println("liftUp sensor flag = ");
					 System.out.println(liftUpSensorFlag);
				}
			}	
		}		
	}
	

	/**
	 * switch auto method need to test to fully develop
	 */
	public void switchAutoUpInit() {
		atSwitch = true;
		atScale = false;
		atLowScale = false;
		liftTimer.reset();
		liftUpSwitchActivated = true;
		liftTimer.start();
		liftDownSwitchSensorFlag = false;
	}
	
	public void switchAutoUpPeriodic() {
		if (liftUpSwitchActivated) {
			if (liftTimer.get() > Consts.partysOverSwitchUp) {
				System.out.println("Party's over");
				liftUpSwitchActivated = false;
				liftUpSwitchSensorFlag= false;
				stop();
			}
			else if (liftUpSwitchSensorFlag) {
				System.out.println("stop called for switch up");
				stop();
			}
			else {
				System.out.println("slide reverse and arms up called for switch up");
				slideReverse();
				armsUp();
				if (atDownLevel.getVoltage()>  2 ) {
						 liftUpSwitchSensorFlag= true;
						 System.out.println("liftUpSwitch sensor flag = ");
						 System.out.println(liftUpSwitchSensorFlag);

					}
				
			}
		}
		
	}
	/*public void switchAutoDownInit() {
		liftTimer.reset();
		liftDownSwitchActivated = true;
		liftTimer.start();
		liftUpSwitchSensorFlag = false;
	}
	
	public void switchAutoDownPeriodic() {
		if (liftDownSwitchActivated) {
			if (liftTimer.get() > Consts.partysOver) {
				liftDownSwitchActivated = false;
				liftDownSwitchSensorFlag= false;
			}
			else if (liftUpSwitchSensorFlag) {
				stop();
	
			}
			else  {
				slideForward();
				armsDown();
				if (atDownLevel.getVoltage()>  2 ) {
						 liftDownSwitchSensorFlag= true;

					}
				
			}
		}
		
	}*/
	public void lowScaleAutoUpInit() {
		atLowScale = true;
		atScale = false;
		atSwitch = false;
		liftTimer.reset();
		liftUpLowScaleActivated = true;
		liftTimer.start();
		liftUpLowScaleSensorFlag = false;
	}
	
	public void lowScaleAutoUpPeriodic() {
		SmartDashboard.putNumber("Party's Over", partysOverDown);
		if (liftUpLowScaleActivated) {
			if (liftTimer.get() > Consts.partysOverLowScale) {
				System.out.println("Party's over");
				liftUpLowScaleActivated = false;
				liftUpLowScaleSensorFlag= false;
			}
			else if (liftUpLowScaleSensorFlag) {
				System.out.println("stop called for low scale");
				stop();
	
			}
			else  {
				System.out.println("slide reverse and arms up called for low scale");
				slideReverse();
				armsUp();
				if (scaleUpTrigger.getVoltage()>  2 ) {
						 liftUpLowScaleSensorFlag= true;
						 System.out.println("liftUpLowScale sensor flag = ");
						 System.out.println(liftUpLowScaleSensorFlag);

					}
				
			}
		}
		
	}
	
	

	// manip diognostics output to smart doashboard for each pnumatic subsystem
	public void manipulatorDianostics() {
		testOn = true;
		compresorPSI();
		lowPSIWarning();
		SmartDashboard.putBoolean("slide back ", slideReversecheck.get());
	//	SmartDashboard.putBoolean("ARE ARMS DOWN", armsDownCheck.get());
		SmartDashboard.putNumber("Compresor PSI ", compresorPSI());
		// presure switch output
		SmartDashboard.putBoolean("testOn", testOn);

		SmartDashboard.putBoolean("isKickoutActivated", isKickoutActivated);
		SmartDashboard.putBoolean("liftDownActivated",liftDownActivated );
		SmartDashboard.putNumber( "test anlog soldoid sensor voltager", scaleUpTrigger.getVoltage());
		SmartDashboard.putNumber("atSwitchLevel", atDownLevel.getVoltage());
	
	}

	public void boxGrabberPeriodic () {
		routineRunning = liftUpActivated || liftDownActivated || isKickoutActivated || liftUpSwitchActivated || liftDownSwitchActivated || liftUpLowScaleActivated;
		// for testing
		// mainC.stop();

		
		manipulatorDianostics();
		liftDownPeriodic();
		liftUpPeriodic();
		
		//switchAutoDownPeriodic();
		switchAutoUpPeriodic();
		lowScaleAutoUpPeriodic();
		
		if(!routineRunning || _xBox.getPOV()!=-1 || _xBox.getBumper(GenericHID.Hand.kLeft)|| _xBox.getBumper(GenericHID.Hand.kRight)|| _xBox.getBackButton()|| _xBox.getStartButton()) {
			switch (xBox()) {
				case STOPOVERRIDE:
					stop();
					liftUpActivated = false;
					liftDownActivated = false;
					isKickoutActivated = false;
					liftUpSwitchActivated = false;
					liftDownSwitchActivated = false;
					liftUpLowScaleActivated = false;
				break;
				case MANUALCONTROL:
					manualControl();
					break;

			
				case LIFTUPAUTOMATED:
					if(!atSwitch) {
					liftUpInit();
					}
				break;
				
				case LOWSCALEUPAUTOMATED:
					lowScaleAutoUpInit();
				break;
				
				case SWITCHUPAUTOMATED:
					switchAutoUpInit();
				break;
				
				case LIFTDOWNAUTOMATED:
					liftDownInit();
				break;
				
				case CLAMPOPEN:
					clampOpen();
				break;
				
				case CLAMPCLOSE:
					clampClose();
				break;

				
				default:
					// default to stop for saftey reasons
					stop();	
				break;
			}
		}
	}
}
