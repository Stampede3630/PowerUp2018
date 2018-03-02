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
		LIFTUP,LIFTDOWN,SLIDEFORWARD,SLIDEBACK,CLAMPOPEN,CLAMPCLOSE,KICKFORWARD,KICKRETRACT,INTAKE,DROPBOX,STOP,SWITCHUPAUTOMATED,LOWSCALEUPAUTOMATED,LIFTUPAUTOMATED,LIFTDOWNAUTOMATED,SWITCHDOWNAUTOMATED
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
	boolean liftUpEngaged, liftDownEngaged, slideUpEngaged, slideOutEngaged, clampEngaged, clampReverseEngaged, kickForwardEngaged,kickReverseEngaged;
	boolean isKickoutActivated;
	boolean liftUpSensorFlag,liftUpActivated;
	boolean liftDownSensorFlag,liftDownActivated; 
	boolean liftUpSwitchActivated, liftUpSwitchSensorFlag;
	boolean liftDownSwitchActivated,liftDownSwitchSensorFlag;
	boolean liftUpLowScaleSensorFlag, liftUpLowScaleActivated;
	boolean stopPneumatics;
	AnalogInput pressureLevel;
	DigitalInput slideReversecheck, armsDownCheck;
	Timer liftTimer,slideTimer, kickTime;
	int kickOutSwitchStates;

	AnalogInput scaleUpParimitor, atDownLevel;

	public BoxGrabber() {

		// peramtors for double soelnoid pcm, in chanel, out chanel
		// for detils on solondid asighning see output sheet i posted on slack
		slideTimer = new Timer();
		kickTime = new Timer();
		liftTimer = new Timer();
		isKickoutActivated= false;
		slideReversecheck = new DigitalInput(3);
		armsDownCheck= new DigitalInput(2);
		liftUpSensorFlag= false;
		liftUpActivated = false;
		liftDownSensorFlag=false;
		liftDownActivated = false;
		liftDownSwitchSensorFlag = false;
		liftDownSwitchActivated = false;
		liftUpSwitchSensorFlag = false;
		liftUpSwitchActivated = false;
		liftUpEngaged = false;
		liftDownEngaged = false;
		slideUpEngaged = false;
		slideOutEngaged = false;
		clampEngaged = false;
		clampReverseEngaged = false;
		kickForwardEngaged = false;
		kickReverseEngaged = false;
		
		testOn = false;
		 // need to make theese consts 
		slide = new DoubleSolenoid(1, 2, 3);
		kick = new DoubleSolenoid(0, 0, 1);
		clamp = new DoubleSolenoid(0, 2, 3);
		lift = new DoubleSolenoid(1, 0, 1);
		mainC = new Compressor(0);
		pressureLevel = new AnalogInput(0);
		scaleUpParimitor = new AnalogInput(1);
		atDownLevel = new AnalogInput(2);
		_xBox = new XboxController(Consts.xBoxComPort);
		// leftIntake = new TalonSRX(7);
		//
		// rightIntake = new TalonSRX(8);
		// leftIntake.setInverted(true);
		// rightIntake.setInverted(true);
	}

	
// to do make button asighnments sane
	public State xBox() {
		if (_xBox.getXButton()== true) {
			return State.LOWSCALEUPAUTOMATED;
		}
		else if (_xBox.getYButton() == true) {
			return State.LIFTUPAUTOMATED;	
		}
		else if (_xBox.getAButton() == true) {
			return State.LIFTDOWNAUTOMATED;	
		} 
		else if (_xBox.getBButton()== true) {
			return State.SWITCHUPAUTOMATED;
		}
		else if (_xBox.getBumper(GenericHID.Hand.kRight) == true) {
			return State.CLAMPCLOSE;
		}
		else if (_xBox.getBumper(GenericHID.Hand.kLeft) == true) {
			return State.CLAMPOPEN;
		}
		else if (_xBox.getStartButton()== true) {
			return State.DROPBOX;
		}
		else {
			return State.STOP;
		}
	}

	public void liftController() {
		if(_xBox.getPOV() != -1) {
			stopPneumatics = false;
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
			
			if(slideControl > 0) {
				slideForward();
			}
			else if(slideControl < 0) {
				slideReverse();
				
			
			}
			else {
				slideOff();
			}
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
	public void armsStop() {
		lift.set(DoubleSolenoid.Value.kOff);
		liftUpEngaged = true;

	}

	public void armsDown() {
		lift.set(DoubleSolenoid.Value.kReverse);
		liftDownEngaged = true;

	}

	public void clampOpen() {
		clamp.set(DoubleSolenoid.Value.kForward);
		clampEngaged = true;
	}

	public void clampClose() {
		clamp.set(DoubleSolenoid.Value.kReverse);
		clampReverseEngaged = true;
	}
	
	public void slideReverse() {
		slideOutEngaged = true;
		slide.set(DoubleSolenoid.Value.kReverse);
	}

	public void slideOff() {
		slideOutEngaged = false;
		slide.set(DoubleSolenoid.Value.kOff);
	}
	
	// stop method for safety
	public void stop() {

		clamp.set(DoubleSolenoid.Value.kOff);
		lift.set(DoubleSolenoid.Value.kOff);
		kick.set(DoubleSolenoid.Value.kOff);
	}

	public void slideForward() {
		slideUpEngaged = true;
		slide.set(DoubleSolenoid.Value.kForward);
	}

	
	// psi method for pressure sensor need to calibrate normalized voltage during
	// testing
	public double compresorPSI() {
		double sensorV = pressureLevel.getVoltage();
		double psi = 250 * (sensorV / 5) - 25;

		return psi;
	}

	public void lowPSIWarning() {
		if (compresorPSI() < 60.0) {
			System.out.println("WARNING robots dont have low presure but yours does");
			System.out.print('\n');
		}
	}

	
		
		
	
	public void kickOutInitialise(){
		kickTime.reset();
		kickOutSwitchStates = 1;
		isKickoutActivated = true;
}
	public void  kickoutPeriodic(){
		if (isKickoutActivated){
			switch(kickOutSwitchStates){
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
				System.out.println("case four");
				if (kickTime.hasPeriodPassed(.6)){
					kickOutSwitchStates =5;
				}
				break; 
			case 5:
				//clampClose();
				System.out.println("case five ");

				if (kickTime.hasPeriodPassed(.004)){
					kickOutSwitchStates = -1;
					isKickoutActivated = false;
					kickTime.stop();
					System.out.print("isKickoutactivated boolean in case five");
					System.out.print(isKickoutActivated);
				}
				break; 
			default:
				System.out.print("kickout box weird stoping solonoids");
				stop();
			}
	
			
}
	}

	public void liftUpInit () {
		liftTimer.reset();
		liftUpActivated = true;
		liftUpSensorFlag=false;
		liftTimer.start();
		System.out.println("lift up init is being called");
	}
	
	public void liftUpPeriodic() {
		if (liftUpActivated) {
			if (liftTimer.get() > Consts.partysOver) {
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
				if (scaleUpParimitor.getVoltage() > 2 ) {
						 liftUpSensorFlag= true;
						 System.out.println("liftUp sensor flag = ");
						 System.out.println(liftUpSensorFlag);
					}
			}
		}
		
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
				liftDownActivated = false;
				liftDownSensorFlag = false;
				System.out.println("Party's over");
			}
			
			else if(liftDownSensorFlag) {
				System.out.println("slide forwad called for lift down");
				slideForward();
			}
			else if(liftTimer.hasPeriodPassed(.5)) {
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
			}	
		}		
	}
	

	/**
	 * switch auto method need to test to fully develop
	 */
	public void switchAutoUpInit() {
		liftTimer.reset();
		liftUpSwitchActivated = true;
		liftTimer.start();
		liftDownSwitchSensorFlag = false;
	}
	
	public void switchAutoUpPeriodic() {
		if (liftUpSwitchActivated) {
			if (liftTimer.get() > Consts.partysOver) {
				System.out.println("Party's over");
				liftUpSwitchActivated = false;
				liftUpSwitchSensorFlag= false;
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
		liftTimer.reset();
		liftUpLowScaleActivated = true;
		liftTimer.start();
		liftUpLowScaleSensorFlag = false;
	}
	
	public void lowScaleAutoUpPeriodic() {
		if (liftUpLowScaleActivated) {
			if (liftTimer.get() > Consts.partysOver) {
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
				if (scaleUpParimitor.getVoltage()>  2 ) {
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
		SmartDashboard.putBoolean("liftgoing up", liftUpEngaged);
		SmartDashboard.putBoolean(" Liftdown", liftDownEngaged);
		SmartDashboard.putBoolean("kick On", kickForwardEngaged);
		SmartDashboard.putBoolean("kick reversw", kickReverseEngaged);
		SmartDashboard.putBoolean("slideForwardEngaged", slideUpEngaged);

		SmartDashboard.putBoolean("slide reverse Engaged", slideOutEngaged);
		SmartDashboard.putBoolean("clamp forward Engaged", clampEngaged);
		SmartDashboard.putBoolean("clamp reverse Engaged", clampReverseEngaged);
		SmartDashboard.putBoolean("isKickoutActivated", isKickoutActivated);
		SmartDashboard.putBoolean("liftDownActivated",liftDownActivated );
		SmartDashboard.putNumber( "test anlog soldoid sensor voltager", scaleUpParimitor.getVoltage());
		SmartDashboard.putNumber("atSwitchLevel", atDownLevel.getVoltage());
	
	}

	public void boxGrabberPeriodic () {
		stopPneumatics = !liftUpActivated && !liftDownActivated && !isKickoutActivated && !liftUpSwitchActivated && !liftDownSwitchActivated && !liftUpLowScaleActivated;
		// for testing
		// mainC.stop();
		liftUpEngaged = false;
		slideUpEngaged = false;
		slideOutEngaged = false;
		kickForwardEngaged = false;
		clampEngaged = false;
		kickReverseEngaged = false;
		liftDownEngaged = false;
		clampReverseEngaged = false;
		kickoutPeriodic();
		manipulatorDianostics();
		liftDownPeriodic();
		liftUpPeriodic();
		liftController();
		//switchAutoDownPeriodic();
		switchAutoUpPeriodic();
		lowScaleAutoUpPeriodic();
		
		
		switch (xBox()) {
			case LIFTUPAUTOMATED:
				liftUpInit();
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
				
			case DROPBOX:
				if(!isKickoutActivated) {
					kickOutInitialise();
				}
			break;
			
			default:
				// default to stop for saftey reasons
				if(stopPneumatics) {
					stop();	
			}
			

			break;
		}
	}

}
