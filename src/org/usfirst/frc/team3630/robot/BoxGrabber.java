package org.usfirst.frc.team3630.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class BoxGrabber {
	Timer manipTime;
	// enum difftent state= state (F, forward) (R, piston reverse)
	public enum State {
		SLIDEFORWARD, 
		CLAMPOPEN, 
		KICKFORWARD, 
		LIFTUP, 
		LIFTDOWN, 
		CLAMPCLOSE,
		SLIDEBACK, 
		KICKRETRACT,
		STOP,
		INTAKE 
		  
	}
	private XboxController _xBox;
	private TalonSRX leftIntake, rightIntake;
	// name double solonoid
	DoubleSolenoid slide,clamp,kick, lift;
	Compressor mainC;
	// debug analog input
	Boolean  liftUpEngaged, slideUpEngaged, slideOutEngaged, kickForwardEngaged, testOn, clampEnaged, kickReverseEngaged, liftDown, clampReverse;
	AnalogInput pressureLevel;
	
public BoxGrabber(){
	
	// peramtors for double soelnoid pcm, in chanel, out chanel
	// for detils on solondid asighning see output sheet i posted on slack 
	manipTime= new Timer ();
	slide = new DoubleSolenoid(1,2,3);
	kick	= new DoubleSolenoid(0,0, 1);
	clamp = new DoubleSolenoid(0,2,3);
	lift= new DoubleSolenoid(1,0, 1);
	mainC= new Compressor(0);
	pressureLevel= new AnalogInput(0);
	_xBox = new XboxController(Consts.xBoxComPort);
	leftIntake = new TalonSRX(7);

	rightIntake = new TalonSRX(8);
	leftIntake.setInverted(true);
	rightIntake.setInverted(true);
}


public State xBox () {
	// need to confirm buttons//  acyivates state for switch if button press is true 
	if (_xBox.getXButton()== true ) {
		return State.SLIDEFORWARD;
	}
	else if (_xBox.getAButton() == true) {
		return State.SLIDEBACK;
	}
else if (_xBox.getBButton()== true ) {
		return State.KICKFORWARD;
	}
else if (_xBox.getBumper(GenericHID.Hand.kRight)== true  ) {
		return State.KICKRETRACT;
	}

	else if (_xBox.getStartButton()== true ) {
		return State.LIFTUP;
	}
	else if (_xBox.getBackButton()== true ) {
		return State.CLAMPOPEN;
	}
	
	else if (_xBox.getBumper(GenericHID.Hand.kLeft)== true ) {
		return State.CLAMPCLOSE;
	}
	
	else if (_xBox.getYButton()== true ) {
	return 	State.LIFTDOWN;
	}
	
	
	else {
		return State.STOP;
	}
}



/*public void intake() {
	rightIntake.configNeutralDeadband(.1, 10);
	leftIntake.configNeutralDeadband(.1, 10);
	leftIntake.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, rightIntake.getDeviceID());
	rightIntake.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, _xBox.getTriggerAxis(GenericHID.Hand.kLeft));

}*/

// each method for has a forward and reverse 
// sets a bollean to true in order to know it has ben activated 

public void kickForward(){

	kick.set(DoubleSolenoid.Value.kForward);
	kickForwardEngaged= true;
	
}
public void kickReverse(){
	kick.set(DoubleSolenoid.Value.kReverse);
	kickReverseEngaged= true;
	
}
public void liftForward(){
	lift.set(DoubleSolenoid.Value.kForward);
	liftUpEngaged= true ;
	
}
public void liftDown(){
	lift.set(DoubleSolenoid.Value.kReverse);
	liftDown= true ;
	
	
}
public void clampOpen() {
	clamp.set(DoubleSolenoid.Value.kForward);
	clampEnaged= true;
}
public void clampClose()
 {
	clamp.set(DoubleSolenoid.Value.kReverse);
	clampReverse=true;
}
// stop method for saftey 
public void stop() {

	clamp.set(DoubleSolenoid.Value.kOff);
	lift.set(DoubleSolenoid.Value.kOff);
	kick.set(DoubleSolenoid.Value.kOff);
	
}

public void slideForward() {
	slideUpEngaged= true;
	slide.set(DoubleSolenoid.Value.kForward);
}
public void slideReverse() {
	slideOutEngaged= true;
	slide.set(DoubleSolenoid.Value.kReverse);
}

// psi method for presure sensor need to calibrate normaliesd voltage during testing 
public double   compresorPSI() {
	double sensorV= pressureLevel.getVoltage();
	double  psi = 250 * (sensorV/ 5) -25 ;
	return psi;
	// rerurn psi
	// decide low pxi level 
	// for details see adni mark spech sheet 
	
	
}

/**
 * method for geteing a box. Add timer delay curently for saftey 
 */
public void kickoutBox() {
	clampOpen(); 
	Timer.delay(.5);
	kickForward();
	Timer.delay(.5);
	kickReverse();
	
	
}

/**
 * scale Auto method for lift up and dump box 
 */
public void scaleAuto(){
	liftForward();
	Timer.delay(4);
	kickoutBox();
	Timer.delay(2.5);
	liftDown();
	
}

/**
 * swutch auto metod need to test to fully develop
 */
public void switchAuto() {
	
}

// manip diognostics output to smart doashboard for each pnumatic subsystem 
public void manipulatorDianostics() {
	testOn= true;
	compresorPSI();
	SmartDashboard.putNumber("Compresor PSI ",compresorPSI());
	// presure switch output 
	SmartDashboard.putBoolean("testOn", testOn);
	SmartDashboard.putBoolean("liftgoing up", liftUpEngaged);
	SmartDashboard.putBoolean(" Liftdown", liftDown);
	SmartDashboard.putBoolean("kick On", kickForwardEngaged);
	SmartDashboard.putBoolean("kick reversw", kickReverseEngaged);
	SmartDashboard.putBoolean("slideForwardEngaged", slideUpEngaged);
	SmartDashboard.putBoolean("slide reverse Engaged",slideOutEngaged );
	
	SmartDashboard.putBoolean("clamp forward Engaged",clampEnaged );
	SmartDashboard.putBoolean("clamp reverse Engaged",clampReverse );
	
	
	
	
}

public void boxGraberPeriodic() {
	// for testing 
	//mainC.stop();
	 liftUpEngaged=false;
	 slideUpEngaged=false;
	 slideOutEngaged=false;
	 kickForwardEngaged=false;
	 clampEnaged=false;
	 kickReverseEngaged=false;
	 liftDown=false;
	 clampReverse=false;
	manipulatorDianostics() ;
	//intake();
	   switch (xBox()) {
       case SLIDEFORWARD:
    	   slideForward() ;
           break;
               
       case SLIDEBACK:
    	   slideReverse()   ;
           break;
                    
       case KICKFORWARD:
    	   kickForward();
           break;
           
       case KICKRETRACT:
    	   kickReverse();
    	   break;
	   
       case LIFTUP:
		   liftForward();
		   break;
		   
	   case CLAMPCLOSE:
		   clampClose();
		   break;
	   
	   case CLAMPOPEN:
		   clampOpen();
		   break;
	  
	   case LIFTDOWN:
		   liftDown();
		   break;
	                                       
       default:
    	   // default to stop for saftey reasons 
    	   stop();
          
           break;
   }
	
}


}
