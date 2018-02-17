package org.usfirst.frc.team3630.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class boxGraber {
	public enum State {
		SLIDEF, 
		CLAMPF, 
		KICKF, 
		LIFTF, 
		LIFTR, 
		SLIDER, 
		KICKR
		  
	}
	private XboxController _xBox;
	DoubleSolenoid slide,clamp,kick, lift;
	Compressor mainC;
	Boolean  liftUpEngaged, slideUpEngaged, slideOutEngaged, kickForwardEngaged, testOn, clampEnaged, kickReverseEngaged, liftDown, clampReverse;
	AnalogInput pressureLevel;
	
public boxGraber(){
	
	// peramtors for double soelnoid pcm, in chanel, out chanel
	slide = new DoubleSolenoid(0,Consts.solonoidSliodeOpenChanal, Consts.solonoidSlideCloseChanal);
	clamp= new DoubleSolenoid(0,Consts.solonoidClampOpenChanal, Consts.solonoidClampCloseChanal);
	kick= new DoubleSolenoid(0,Consts.solonoidKickOpenChanal, Consts.solonoidKickCloseOpenChanal);
	lift= new DoubleSolenoid(0,Consts.solonoidLifterOpenChanal, Consts.solonoidLifterCloseChanal);
	mainC= new Compressor(0,1, 2);
	pressureLevel= new AnalogOutput(0);
	_xBox = new XboxController(Consts.xBoxComPort);
	
}


public State xBox () {
	if (_xBox.getAButton(GenericHID.Hand.kLeft.value)==  ) {
		return State.SLIDEF;
	}
	else if (_xBox.getAButton(GenericHID.Hand.kLeft.value)==) {
		return State.SLIDER;
	}
	else if (_xBox.getAButton(GenericHID.Hand.kLeft.value)==) {
		return State.KICKF;
	}
	else if (_xBox.getAButton(GenericHID.Hand.kLeft.value)==) {
		return State.KICKR;
	}
	else if (_xBox.getAButton(GenericHID.Hand.kLeft.value)==) {
		return State.LIFTF;
	}
	else if (_xBox.getAButton(GenericHID.Hand.kLeft.value)==) {
	return 	State.LIFTR;
	}
	else {
		
	}
	
}







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
	clampReverse=true
}
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
public double  compresorPSI() {
	double sensorV= pressureLevel.getVoltage();
	// psi = 250 (vout/ vn) -25 
	// rerurn psi
	
}
public void manipulatorDianostics() {
	testOn= true;
	// presure switch output 
	SmartDashboard.putBoolean("testOn", testOn);
	SmartDashboard.putBoolean("liftgoing up", liftUpEngaged);
	SmartDashboard.putBoolean(" Liftdown", liftDown);
	SmartDashboard.putBoolean("kick On", kickForwardEngaged);
	SmartDashboard.putBoolean("kick reversw", kickReverseEngaged);
	SmartDashboard.putBoolean("slideForwardEngaged", slideUpEngaged);
	SmartDashboard.putBoolean("slide reverse Engaged",slideOutEngaged );
	
	
	
}

public void boxGraberPeriodic() {
	xBox();
	   switch (State) {
       case :
           
           break;
               
       case :
           ;
           break;
                    
       case :
           ;
           break;
                   
       default:
          
           break;
   }
	
}


}
