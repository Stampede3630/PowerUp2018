package org.usfirst.frc.team3630.robot;

import edu.wpi.first.wpilibj.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * manipulator class for box grabber. Based on pnumatics 
 * 
 *
 */
public class BoxGrabber {



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
		
	
	}
	private WPI_TalonSRX leftMasterIntakeTalon, rightSlaveIntakeTalon;

	
	
	
	private XboxController _xBox;

	DoubleSolenoid slide, clamp, kick, lift;
	Compressor mainC;
	
	boolean testOn;
	boolean isKickoutActivated;
	boolean liftUpSensorFlag,liftUpActivated;
	boolean liftDownSensorFlag,liftDownActivated; 
	boolean liftUpSwitchActivated, liftUpSwitchSensorFlag;
	boolean liftDownSwitchActivated;
	boolean liftUpLowScaleSensorFlag, liftUpLowScaleActivated;
	boolean routineRunning;
	boolean atSwitch, atLowScale,atScale;
	AnalogInput pressureLevel;
	DigitalInput slideReversecheck;
	Timer liftTimer,slideTimer, kickTime;
	int kickoutBoxState;
	double partysOverDown;

	AnalogInput scaleUpTrigger, atDownLevel;

	public BoxGrabber() {

		
		
		slideTimer = new Timer();
		kickTime = new Timer();
		liftTimer = new Timer();
		
		slideReversecheck = new DigitalInput(3);
		
		
		isKickoutActivated= false;
		
		liftUpSensorFlag= false;
		liftUpActivated = false;
		liftDownSensorFlag=false;
		liftDownActivated = false;
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
		
		leftMasterIntakeTalon = new WPI_TalonSRX(7); 
		rightSlaveIntakeTalon = new WPI_TalonSRX(8);
		configureTalon(leftMasterIntakeTalon);
		configureTalon(rightSlaveIntakeTalon);
		rightSlaveIntakeTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, 7);
		leftMasterIntakeTalon.setInverted(false);
		rightSlaveIntakeTalon.setInverted(true);
		
		
	}

	

	public State xBox() {
		if(_xBox.getPOV() != -1){
			return State.MANUALCONTROL;
		}
		else if (_xBox.getXButton()) {
			return State.LOWSCALEUPAUTOMATED;
		}
		else if (_xBox.getStartButton()) {
			return State.KICKFORWARD;
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

	/**
	 * this is for full on manual control with the x-box d pad
	 * based on the unit circul math
	 */
	public void manualControl() {
			routineRunning = false;
			liftUpActivated = false;
			liftDownActivated = false;
			isKickoutActivated = false;
			liftUpSwitchActivated = false;
			liftDownSwitchActivated = false;
			liftUpLowScaleActivated = false;
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
	

	
	
	

	

	private void configureTalon(TalonSRX _talon) {
		_talon.configNominalOutputForward(0, Consts.timeOutMs);
		_talon.configNominalOutputReverse(0, Consts.timeOutMs);
		_talon.configPeakOutputForward(1, Consts.timeOutMs);
		_talon.configPeakOutputReverse(-1, Consts.timeOutMs);
		_talon.configNeutralDeadband(0.05, Consts.timeOutMs);
		_talon.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
	}
	
	

	public void boxIntakePeriodic() {
		double speed = (_xBox.getTriggerAxis(GenericHID.Hand.kRight))*-1;
		leftMasterIntakeTalon.set(speed);
	
		
	}
	
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
	
	
	
	/**
	 * stop compand for saftey
	 */
	public void stop() {
		clamp.set(DoubleSolenoid.Value.kOff);
		lift.set(DoubleSolenoid.Value.kOff);
		kick.set(DoubleSolenoid.Value.kOff);
	}

	/**
	 * move manipulator forwords
	 */
	public void slideForward() {
		slide.set(DoubleSolenoid.Value.kForward);
	}

	
	
	/**
	 * @return rouned compreser psi 
	 */
	public double compresorPSI() {
		double sensorV = pressureLevel.getVoltage();
		double psi = 250 * (sensorV / 5) - 25;
		return Math.round(psi);
	}

	/**
	 * low psi warning when sensor registers bellow 60 psi
	 */
	public void lowPSIWarning() {
		if (compresorPSI() < 60.0) {
			System.out.println("WARNING robots dont have low pressure but yours does");
		}
	}

	
	public void kickoutInit(){
		if (!isKickoutActivated){
			kickTime.reset();
			kickoutBoxState = 1;
			isKickoutActivated = true;
			kickTime.start();
			leftMasterIntakeTalon.set(1);
	
		}
}
	

	public void  kickoutPeriodic(){
		if (isKickoutActivated){
			switch(kickoutBoxState){
			case 1:
			
				leftMasterIntakeTalon.set(1);
				if (kickTime.hasPeriodPassed(.05)){
					kickoutBoxState=2;
				}
				break; 
			case 2:
				clampOpen();
				leftMasterIntakeTalon.set(1);
			
				if (kickTime.hasPeriodPassed(1)){
					kickoutBoxState =3;
				}
				break; 
			case 3:
				
				leftMasterIntakeTalon.set(0);
			

				if (kickTime.hasPeriodPassed(2)){
					kickoutBoxState = -1;
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
				break;
			}
		}
			
}
	
	

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
	
				}
			}
		}

	}
	public void liftDownInit () {
		
		
		liftDownSensorFlag = false;
		liftDownActivated = true;
		
		liftTimer.reset();
		liftTimer.start();


		
		if (atSwitch) {
			partysOverDown = Consts.partysOverSwitchDown;
		}
		else if (atLowScale) {
			partysOverDown = Consts.partysOverLowScaleDown;
		}
		else {
			partysOverDown = Consts.partysOverScaleDown;
		}

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
		liftUpSwitchSensorFlag = false;
		System.out.println("Switch Auto init was called");
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
				liftUpSwitchActivated = false;
				
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

	public void lowScaleAutoUpInit() {
		atLowScale = true;
		atScale = false;
		atSwitch = false;
		liftTimer.reset();
		liftUpLowScaleActivated = true;
		liftTimer.start();
		liftUpLowScaleSensorFlag = false;
	}
	
	// manip diognostics output to smart doashboard for each pnumatic subsystem
	public void manipulatorDianostics() {
		testOn = true;
		compresorPSI();
		lowPSIWarning();
		SmartDashboard.putBoolean("slide back ", slideReversecheck.get());
	
		SmartDashboard.putNumber("Compresor PSI ", compresorPSI());
	
		SmartDashboard.putBoolean("testOn", testOn);

		SmartDashboard.putBoolean("isKickoutActivated", isKickoutActivated);
		SmartDashboard.putBoolean("liftDownActivated",liftDownActivated );
		SmartDashboard.putNumber( "test anlog soldoid sensor voltager", scaleUpTrigger.getVoltage());
		SmartDashboard.putNumber("atSwitchLevel", atDownLevel.getVoltage());
	
	}

	public void boxGrabberPeriodic () {
		routineRunning = liftUpActivated || liftDownActivated || isKickoutActivated || liftUpSwitchActivated || liftDownSwitchActivated || liftUpLowScaleActivated;
		
		boxIntakePeriodic();
		kickoutPeriodic();
		liftDownPeriodic();
		liftUpPeriodic();
		
	
		switchAutoUpPeriodic();
		
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
					if(!atSwitch && !atLowScale) {
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
				case KICKFORWARD:
					 kickoutInit();
				break;
				
				case CLAMPCLOSE:
					clampClose();
				break;

				
				default:
					// default to stop for safety reasons
					stop();	
				break;
			}
		}
	}
}
