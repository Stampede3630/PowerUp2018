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
		SLIDEFORWARD, CLAMPOPEN, KICKFORWARD, LIFTUP, LIFTDOWN, CLAMPCLOSE, SLIDEBACK, KICKRETRACT, STOP, INTAKE, DROPBOX, SWITCHAUTOMATED, SCALEAUTOMATED, LIFTDOWNAUTOMATED, LIFTUPAUTOMATED

	}

	private XboxController _xBox;
	// private TalonSRX leftIntake, rightIntake;
	// name double solonoid
	DoubleSolenoid slide, clamp, kick, lift;
	Compressor mainC;
	// debug analog input
	boolean liftUpEngaged, slideUpEngaged, slideOutEngaged, kickForwardEngaged, testOn, clampEnaged, 
	kickReverseEngaged,liftDown, clampReverse,  slideFullyReversed, clampOpen, liftUpActivated, liftDownActivated;
	AnalogInput pressureLevel;
	DigitalInput slideReversecheck, armsDownCheck;
	Timer liftTimer ,slideTimer, kickTime;
	int kickOutSwitchStates;
	boolean isKickoutActivated;
	boolean liftUpSensorFlag, 	liftDownSensorFlag;
	AnalogInput scaleUpParimitor, atDownLevel;

	public BoxGrabber() {

		// peramtors for double soelnoid pcm, in chanel, out chanel
		// for detils on solondid asighning see output sheet i posted on slack
		slideTimer = new Timer();
		kickTime = new Timer();
		isKickoutActivated= false;
		liftUpSensorFlag= false;
		liftTimer = new Timer();
		liftUpActivated = false;
		slideReversecheck = new DigitalInput(3);
		 armsDownCheck= new DigitalInput(2);
		 liftUpSensorFlag = false;
		 liftDownSensorFlag=false;
		 // need to make theese consts 
		slide = new DoubleSolenoid(1, 2, 3);
		kick = new DoubleSolenoid(0, 0, 1);
		clamp = new DoubleSolenoid(0, 2, 3);
		lift = new DoubleSolenoid(1, 0, 1);
		mainC = new Compressor(0);
		pressureLevel = new AnalogInput(0);
		_xBox = new XboxController(Consts.xBoxComPort);
		scaleUpParimitor = new AnalogInput(1);
		atDownLevel = new AnalogInput(2);
		// leftIntake = new TalonSRX(7);
		//
		// rightIntake = new TalonSRX(8);
		// leftIntake.setInverted(true);
		// rightIntake.setInverted(true);
	}

	
// to do make button asighnments sane
	public State xBox() {
	
		if (_xBox.getAButton() == true) {
			return State.LIFTDOWNAUTOMATED;
			
		} else if (_xBox.getXButton() == true) {
			return State.LIFTUPAUTOMATED;
			
		}

		else if (_xBox.getStartButton()== true) {
			return State.DROPBOX;
		}

		else if (_xBox.getBumper(GenericHID.Hand.kRight) == true) {
			return State.CLAMPCLOSE;
		}
		else if (_xBox.getBumper(GenericHID.Hand.kLeft) == true) {
			return State.CLAMPOPEN;
		}
		else if (_xBox.getYButton()== true) {
			return State.SLIDEFORWARD;
		}
		else if (_xBox.getBButton()== true) {
			return State.SLIDEBACK;
		}
		else {
			return State.STOP;
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
		kickForwardEngaged = true;

	}

	public void kickReverse() {
		kick.set(DoubleSolenoid.Value.kReverse);
		kickReverseEngaged = true;

	}

	public void armsUp() {
		lift.set(DoubleSolenoid.Value.kForward);
		liftUpEngaged = true;

	}

	public void armsDown() {
		lift.set(DoubleSolenoid.Value.kReverse);
		liftDown = true;

	}

	public void clampOpen() {
		clamp.set(DoubleSolenoid.Value.kForward);
		clampEnaged = true;
	}

	public void clampClose() {
		clamp.set(DoubleSolenoid.Value.kReverse);
		clampReverse = true;
		System.out.println("ClampClose called");
	}

	// stop method for saftey
	public void stop() {

		clamp.set(DoubleSolenoid.Value.kOff);
		lift.set(DoubleSolenoid.Value.kOff);
		kick.set(DoubleSolenoid.Value.kOff);

	}

	public void slideForward() {
		slideUpEngaged = true;
		slide.set(DoubleSolenoid.Value.kForward);
	}

	public void slideReverse() {
		slideOutEngaged = true;
		slide.set(DoubleSolenoid.Value.kReverse);
	}

	// psi method for presure sensor need to calibrate normaliesd voltage during
	// testing
	public double compresorPSI() {
		double sensorV = pressureLevel.getVoltage();
		double psi = 250 * (sensorV / 5) - 25;

		return psi;

	}

	public void lowPSIWarning() {
		if (compresorPSI() < 60.0) {
			System.out.println("WARNING robots dont have low presure but yourse dose");
			System.out.print('\n');
		}
	}

	
		
		
	
	public void kickOutInitilaise(){
		kickTime.reset();
		kickOutSwitchStates =1;
		isKickoutActivated =true;
}
	public void  kickoutPeriodic(){
		
	if (isKickoutActivated){
		switch(kickOutSwitchStates ){
			case 1:
				System.out.println("case one");
				kickTime.start();
				kickOutSwitchStates=2;
				break;
			case 2:
				clampOpen();
				System.out.println("case two");
					if (kickTime.hasPeriodPassed(.01)){
						
						
						kickOutSwitchStates=3;
					}
					break;
			case 3:
				System.out.println("case three");
				kickForward();
				
				if (kickTime.hasPeriodPassed(.5)){
					
					
					kickOutSwitchStates=4;
				}
				break; 
			case 4:
				kickReverse();
				System.out.println("case four ");
				if (kickTime.hasPeriodPassed(.6)){
					kickOutSwitchStates =5;
				
			}
				break; 
			case 5:
				clampClose();
				System.out.println("case five ");
	
				if (kickTime.hasPeriodPassed(.004)){
					kickOutSwitchStates =-1;
					isKickoutActivated = false;
					kickTime.stop();
					System.out.print("isKickoutactivated boolean in case five");
					System.out.print(isKickoutActivated);
			}
				break; 
			default:
				System.out.print("kickout =box weird stoping solonoids");
				stop();
			}
	
			
}
	}


	
	/**
	 * lift up for scale method to drop box for scale will go to full hight
	 */
	/*public void competitionLiftUpScale() {
		
		if (slideReversecheck.get()) {
			slideReverse();

		} else {
			armsUp();
			Timer.delay(4);
			slideForward();
		}

	}
	*/
	public void liftUpInit () {
		liftTimer.reset();
		liftUpActivated = true;
		liftUpSensorFlag=false;
		liftTimer.start();
		System.out.println("lift up init is being called");
		
	}
	public void liftDownInit () {
		liftTimer.reset();
		liftDownActivated = true;
		liftTimer.start();
		liftDownSensorFlag = false;
		System.out.println("lift down init is being called");
	}
	
	public void liftDownPeriodic() {
		if (liftDownActivated) {
			if (liftTimer.get() > Consts.partysOverDown) {
				System.out.println("set is down activated to false");
				liftDownActivated = false;
				liftDownSensorFlag = false;
			}
			
			else if(liftDownSensorFlag) {
				System.out.println("slide forwad called  in lift down periodic ");
				slideForward();
			}
		
			else  {
				slideReverse();
				armsDown();

				System.out.println("arms called  in lift down periodic ");
					if (atDownLevel.getVoltage()>  2 ) {
						liftDownSensorFlag= true;
						 System.out.println("liftUp sensor flag = ");
						 System.out.println(liftUpSensorFlag);
					}
						}
		}
		
		
	}
	
	public void liftUpPeriodic() {
		if (liftUpActivated) {
			if (liftTimer.get() > Consts.partysOver) {
				liftUpActivated = false;
				liftUpSensorFlag= false;
				System.out.println(liftUpActivated);
			}
			else if (liftUpSensorFlag) {
				System.out.println(" slide forward lit up is being called");
				slideForward();
			}
			else  {
				System.out.println(" slide revse arms up is called for  lit up ");
				slideReverse();
				armsUp();
				if (scaleUpParimitor.getVoltage()>  2 ) {
						 liftUpSensorFlag= true;
						 System.out.println("liftUp sensor flag = ");
						 System.out.println(liftUpSensorFlag);
					}
				
		
		}
		}
		
	}
	/**
	 * saftey method for lift down. ensure robot can't be in forward state when the
	 * arms go down will eventualy become driver lift down button
	 */
/*	public void liftDownRobotCompetion() {
 * 
 * 
		
			armsDown();

	Timer.delay(2.0);
			slideForward();


	}

	public void liftUPRobotCompetion() {
		if (slideReversecheck.get()) {
			slideReverse();
			slideTimer.reset();
		}*/
//
//		else {
//			armsUp();
////			if(slideTimer.get() > 4.0) {
////				slideForward();
////			}

//		}
//
//	}
	

	/**
	 * swutch auto metod need to test to fully develop
	 */
	public void switchAuto() {

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
		SmartDashboard.putBoolean("liftgoing up", liftUpEngaged);
		SmartDashboard.putBoolean(" Liftdown", liftDown);
		SmartDashboard.putBoolean("kick On", kickForwardEngaged);
		SmartDashboard.putBoolean("kick reversw", kickReverseEngaged);
		SmartDashboard.putBoolean("slideForwardEngaged", slideUpEngaged);
		SmartDashboard.putBoolean("slide reverse Engaged", slideOutEngaged);
		SmartDashboard.putBoolean("clamp forward Engaged", clampEnaged);
		SmartDashboard.putBoolean("clamp reverse Engaged", clampReverse);
		SmartDashboard.putBoolean("isKickoutActivated", isKickoutActivated);
		SmartDashboard.putBoolean("liftDownActivated",liftDownActivated );
		SmartDashboard.putNumber( "test anlog soldoid sensor voltager", scaleUpParimitor.getVoltage());
	SmartDashboard.putNumber("atSwitchLevel", atDownLevel.getVoltage());
	
	}

	public void boxGrabberPeriodic () {
		// for testing
		// mainC.stop();
		liftUpEngaged = false;
		slideUpEngaged = false;
		slideOutEngaged = false;
		kickForwardEngaged = false;
		clampEnaged = false;
		kickReverseEngaged = false;
		liftDown = false;
		clampReverse = false;
		kickoutPeriodic();
		manipulatorDianostics();
		liftDownPeriodic();
		liftUpPeriodic();
		switch (xBox()) {
		case SCALEAUTOMATED:
			liftUpInit();

			break;

		case DROPBOX:
			if(!isKickoutActivated) {
				kickOutInitilaise();
				kickoutPeriodic();
			}
			break;

		case LIFTDOWNAUTOMATED:
			liftDownInit ();
			break;

		case LIFTUPAUTOMATED:
			liftUpInit();
			break;

		case CLAMPCLOSE:
			clampClose();
			break;

		case CLAMPOPEN:
			clampOpen();
			break;
		case SLIDEFORWARD:
			slideForward();
			break;
		case SLIDEBACK:
			slideReverse();
			break;
		default:
			// default to stop for saftey reasons
			if(!liftUpActivated && !liftDownActivated && !isKickoutActivated) {
				stop();	
			}
			

			break;
		}
	}

}
