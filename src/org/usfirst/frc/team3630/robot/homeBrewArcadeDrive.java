
package org.usfirst.frc.team3630.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class homeBrewArcadeDrive {

	//NOTE (SAM): These should be able to be TalonSRXs
	  private TalonSRX _talonLeft;
	  private TalonSRX _talonRight;




	  public homeBrewArcadeDrive(  TalonSRX talonLeft, TalonSRX talonRight ) {
		
		  // eventualy put talon closed loop conctuct in here 
		 //MADE CHANGES HERE:
	   _talonLeft = talonLeft;
	   _talonRight = talonRight;
	  }


	  /**
	   * Arcade drive method for differential drive platform.
	   *
	   * @param xSpeed        The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
	   * @param zRotation     The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
	   *                      positive.
	   */
	  public void homebrewarcadeDrivePeriodic(double xSpeed, double zRotation) {

	    double leftMotorOutput;
	    double rightMotorOutput;

	    double maxInput = Math.copySign(Math.max(Math.abs(xSpeed), Math.abs(zRotation)), xSpeed);

	    if (xSpeed >= 0.0) {
	      // First quadrant, else second quadrant
	      if (zRotation >= 0.0) {
	        leftMotorOutput = maxInput;
	        rightMotorOutput = xSpeed - zRotation;
	      } else {
	        leftMotorOutput = xSpeed + zRotation;
	        rightMotorOutput = maxInput;
	      }
	      
	    } else {
	      // Third quadrant, else fourth quadrant
	      if (zRotation >= 0.0) {
	        leftMotorOutput = xSpeed + zRotation;
	        rightMotorOutput = maxInput;
	      } else {
	        leftMotorOutput = maxInput;
	        rightMotorOutput = xSpeed - zRotation;
	      }
	    }
	    // need to double check math 
	    double leftSetpoint =(leftMotorOutput )* Consts.powertoSRXConversion;
	    double rightSetpoint = (rightMotorOutput )*Consts.powertoSRXConversion;
	    _talonLeft.set(com.ctre.phoenix.motorcontrol.ControlMode.Velocity,leftSetpoint);
	    System.out.println(rightSetpoint);
	    System.out.println(leftSetpoint);
	
	    _talonRight.set(com.ctre.phoenix.motorcontrol.ControlMode.Velocity,rightSetpoint);
	  //  SmartDashboard.putNumber("rightSetpoint", _talonRight.);
	 //   SmartDashboard.putNumber("LeftSetpoint", leftSetpoint);
	    
	  }

	
	

	 


	 
	

	

	
	

}
