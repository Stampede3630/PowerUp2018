package org.usfirst.frc.team3630.robot;

import edu.wpi.first.wpilibj.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;

public class BoxGrabber {
	Timer kickTime;
	boolean clampOpen;
	// enum difftent state= state (F, forward) (R, piston reverse)
	// lift
	public enum State {
		SLIDEFORWARD, CLAMPOPEN, KICKFORWARD, LIFTUP, LIFTDOWN, CLAMPCLOSE, SLIDEBACK, KICKRETRACT, STOP, INTAKE, DROPBOX, SWITCHAUTOMATED, SCALEAUTOMATED, LIFTDOWNAUTOMATED, LIFTUPAUTOMATED

	}

	private XboxController _xBox;
	// private TalonSRX leftIntake, rightIntake;
	// name double solonoid
	DoubleSolenoid slide, clamp, kick, lift;
	Timer slideTimer;
	Boolean slideFullyReversed;
	Compressor mainC;
	// debug analog input
	Boolean liftUpEngaged, slideUpEngaged, slideOutEngaged, kickForwardEngaged, testOn, clampEnaged, kickReverseEngaged,
			liftDown, clampReverse;
	AnalogInput pressureLevel;
	DigitalInput slideReversecheck, armsDownCheck;
	boolean liftUpActivated;
	Timer liftTimer;
	int kickOut;
	boolean isKickoutActivated;

	public BoxGrabber() {

		// peramtors for double soelnoid pcm, in chanel, out chanel
		// for detils on solondid asighning see output sheet i posted on slack
		slideTimer = new Timer();
		kickTime = new Timer();
		isKickoutActivated= false;
		liftTimer = new Timer();
		liftUpActivated = false;
		slideReversecheck = new DigitalInput(3);
		 armsDownCheck= new DigitalInput(2);
		slide = new DoubleSolenoid(1, 2, 3);
		kick = new DoubleSolenoid(0, 0, 1);
		clamp = new DoubleSolenoid(0, 2, 3);
		lift = new DoubleSolenoid(1, 0, 1);
		mainC = new Compressor(0);
		pressureLevel = new AnalogInput(0);
		_xBox = new XboxController(Consts.xBoxComPort);
		// leftIntake = new TalonSRX(7);
		//
		// rightIntake = new TalonSRX(8);
		// leftIntake.setInverted(true);
		// rightIntake.setInverted(true);
	}

	
	// goal to test automated buttons to see as reibale and then test replace buton
	// function going to list them out tonight
	// plan intake togle
	// box out button
	// switch buton lift up with tip back
	// scale buton
	// general lift up button with ti back
	// clamp on box button
	public State xBox() {
		// need to confirm buttons// acyivates state for switch if button press is true
		if (_xBox.getXButton() == true) {
			return State.SCALEAUTOMATED;
		} else if (_xBox.getAButton() == true) {
			return State.LIFTDOWNAUTOMATED;
		} else if (_xBox.getBButton() == true) {
			return State.LIFTUPAUTOMATED;
		}
		else if (_xBox.getBumper(GenericHID.Hand.kRight)== true ) {
		return State.SLIDEFORWARD;
		 }

		else if (_xBox.getStartButton() == true) {
			return State.DROPBOX;
		} else if (_xBox.getBackButton() == true) {
			return State.CLAMPOPEN;
		}

		else if (_xBox.getBumper(GenericHID.Hand.kLeft) == true) {
			return State.CLAMPCLOSE;
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
		
		kickOut =1;
		isKickoutActivated =true;
}
	public void  kickoutPeriodic(){
		
	if (isKickoutActivated){
		switch(kickOut ){
			case 1:
				System.out.print("case one kick out was called");
				kickTime.start();
				kickOut=2;
				break;
			case 2:
				clampOpen();
				System.out.print("case two kick out was called");
					if (kickTime.hasPeriodPassed(.001)){
						
						
						kickOut=3;
					}
					break;
			case 3:
				System.out.print("case three kick out was called");
				kickForward();
				
				if (kickTime.hasPeriodPassed(.002)){
					
					
					kickOut=4;
				}
				break; 
			case 4:
				kickReverse();
				System.out.print("case four kick out was called");
				if (kickTime.hasPeriodPassed(.003)){
					kickOut =5;
				
			}
				break; 
			case 5:
				clampClose();
				System.out.print("case five was called");
	
				if (kickTime.hasPeriodPassed(.004)){
					kickOut =-1;
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
	 * scale Auto method for lift up and dump box
	 */
	public void scaleAuto() {
		liftUpInit();
		Timer.delay(4);

		Timer.delay(Consts.timeDelay);
		armsDown();
		

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
		liftTimer.start();
		
	}
	
	public void liftUpPeriodic() {
		if (liftUpActivated == true) {
			if (liftTimer.get() > Consts.partysOver) {
				liftUpActivated = false;
			}
			else if(liftTimer.get() > Consts.stillStanding) {
				slideForward();
			}
			else if(liftTimer.get() > 0) {
				slideReverse();
				armsUp();
			}
		}
		
		
	}
	/**
	 * saftey method for lift down. ensure robot can't be in forward state when the
	 * arms go down will eventualy become driver lift down button
	 */
	public void liftDownRobotCompetion() {
		
			armsDown();

	Timer.delay(2.0);
			slideForward();


	}

	public void liftUPRobotCompetion() {
		if (slideReversecheck.get()) {
			slideReverse();
			slideTimer.reset();
		}

		else {
			armsUp();
//			if(slideTimer.get() > 4.0) {
//				slideForward();
//			}

		}

	}

	public void boxIntakeClamp() {

	}

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
		// intake();
		liftUpPeriodic();
		switch (xBox()) {
		case SCALEAUTOMATED:
			liftUpInit();

			break;

		case DROPBOX:
			kickOutInitilaise();
			  kickoutPeriodic();
			break;

		case LIFTDOWNAUTOMATED:
			liftDownRobotCompetion();
			break;

		case LIFTUPAUTOMATED:
			liftUPRobotCompetion();
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

		default:
			// default to stop for saftey reasons
			if(!liftUpActivated) {
				stop();	
			}
			

			break;
		}
	}

}
