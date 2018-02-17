package org.usfirst.frc.team3630.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class BoxGrabber {
	// enum difftent state= state (F, forward) (R, piston reverse)
	public enum State {
		SLIDEF, 
		CLAMPF, 
		KICKF, 
		LIFTF, 
		LIFTR, 
		CLAMPR,
		SLIDER, 
		KICKR,
		STOP
		  
	}
	private XboxController _xBox;
	// name double solonoid
	DoubleSolenoid slide,clamp,kick, lift;
	Compressor mainC;
	// debug analog input
	Boolean  liftUpEngaged, slideUpEngaged, slideOutEngaged, kickForwardEngaged, testOn, clampEnaged, kickReverseEngaged, liftDown, clampReverse;
	AnalogInput pressureLevel;
	
public BoxGrabber(){
	
	// peramtors for double soelnoid pcm, in chanel, out chanel
	// for detils on solondid asighning see output sheet i posted on slack 
	slide = new DoubleSolenoid(Consts.pcmBChanal,Consts.solonoidSliodeOpenChanal, Consts.solonoidSlideCloseChanal);
	clamp= new DoubleSolenoid(Consts.pcmAChanal,Consts.solonoidClampOpenChanal, Consts.solonoidClampCloseChanal);
	kick= new DoubleSolenoid(Consts.pcmBChanal,Consts.solonoidKickOpenChanal, Consts.solonoidKickCloseChanal);
	lift= new DoubleSolenoid(Consts.pcmAChanal,Consts.solonoidLifterOpenChanal, Consts.solonoidLifterCloseChanal);
	mainC= new Compressor(0);
	pressureLevel= new AnalogInput(0);
	_xBox = new XboxController(Consts.xBoxComPort);
	
}


public State xBox () {
	// need to confirm buttons//  acyivates state for switch if button press is true 
	if (_xBox.getXButton()== true ) {
		return State.SLIDEF;
	}
	else if (_xBox.getAButton() == true) {
		return State.SLIDER;
	}
	else if (_xBox.getBButton()== true ) {
		return State.KICKF;
	}
	else if (_xBox.getYButton()== true ) {
		return State.KICKR;
	}
	else if (_xBox.getStartButton()== true ) {
		return State.LIFTF;
	}
	
/*	else if (_xBox()== true) {
	return 	State.LIFTR;
	}
	*/

	else {
		return State.STOP;
	}
}





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
public void liftReverse(){
	lift.set(DoubleSolenoid.Value.kReverse);
	liftDown= true ;
	
	
}
public void clampForward() {
	clamp.set(DoubleSolenoid.Value.kForward);
	clampEnaged= true;
}
public void clampReverse() {
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
public void  compresorPSI() {
	
	double sensorV= pressureLevel.getVoltage();
	// psi = 250 (vout/ vn) -25 
	// rerurn psi
	// decide low pxi level 
	// for details see adni mark spech sheet 
	
	
}


// manip diognostics output to smart doashboard for each pnumatic subsystem 
public void manipulatorDianostics() {
	testOn= true;
	compresorPSI();
	// presure switch output 
	SmartDashboard.putBoolean("testOn", testOn);
	SmartDashboard.putBoolean("liftgoing up", liftUpEngaged);
	SmartDashboard.putBoolean(" Liftdown", liftDown);
	SmartDashboard.putBoolean("kick On", kickForwardEngaged);
	SmartDashboard.putBoolean("kick reversw", kickReverseEngaged);
	SmartDashboard.putBoolean("slideForwardEngaged", slideUpEngaged);
	SmartDashboard.putBoolean("slide reverse Engaged",slideOutEngaged );
	
	SmartDashboard.putBoolean("clamp forward Engaged",clampEnaged );
	SmartDashboard.putBoolean("clamp forward Engaged",clampReverse );
	
	
	
	
}

public void boxGraberPeriodic() {
	// for testing 
	 liftUpEngaged=false;
	 slideUpEngaged=false;
	 slideOutEngaged=false;
	 kickForwardEngaged=false;
	 clampEnaged=false;
	 kickReverseEngaged=false;
	 liftDown=false;
	 clampReverse=false;
	manipulatorDianostics() ;
	   switch (xBox()) {
       case SLIDEF:
    	   	slideForward() ;
           break;
               
       case SLIDER:
    	   slideReverse()   ;
           break;
                    
       case KICKF:
    	   kickForward();
           break;
           
    	   case KICKR:
    		   kickReverse();
	   break;
	   case LIFTF:
		   liftForward();
		   break;
	   case LIFTR:
		   liftReverse();
		   break;
	                                       
       default:
    	   // default to stop for saftey reasons 
    	   stop();
          
           break;
   }
	
}


}
