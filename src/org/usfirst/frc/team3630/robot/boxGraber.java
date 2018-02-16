package org.usfirst.frc.team3630.robot;
import edu.wpi.first.wpilibj.Compressor
import edu.wpi.first.wpilibj.Solenoid;
public class boxGraber {
	DoubleSolenoid slide,clamp,kick, lift;
	Compressor mainC;
	
public boxGraber{
	slide = new DoubleSolenoid(1, 2);
	clamp= new DoubleSolenoid(1, 2);
	kick= new DoubleSolenoid(1, 2);
	lift= new DoubleSolenoid(1, 2);
	mainC= new Compressor(0);
}
// add diagnostis method 



public void kick(){
	
}
public void liftUp(){
	
}
public void liftdown(){
	
}
public void clamp() {
	
}

public void stop() {
	
}


}
