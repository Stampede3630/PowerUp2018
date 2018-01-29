
package org.usfirst.frc.team3630.robot;

	import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class homeBrewArcadeDrive extends RobotDriveBasee {
	

	//  public static final double kDefaultQuickStopThreshold = 0.2;
	//  public static final double kDefaultQuickStopAlpha = 0.1;
	//  private static int instances = 0;

	// init as srx or wpilib talons? 
	  private TalonSRX _talonLeft;
	  private TalonSRX _talonRight;

	//  private double m_quickStopThreshold = kDefaultQuickStopThreshold;
	//  private double m_quickStopAlpha = kDefaultQuickStopAlpha;
	//  private double m_quickStopAccumulator = 0.0;
	//  private boolean m_reported = false;


	  public homeBrewArcadeDrive(  TalonSRX _talonLeft, TalonSRX _talonRight ) {
		
		  // eventualy put talon closed loop conctuct in here 
	   
	    
	    
	  }



	  /**
	   * Arcade drive method for differential drive platform.
	   *
	   * @param xSpeed        The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
	   * @param zRotation     The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
	   *                      positive.
	   * @param squaredInputs If set, decreases the input sensitivity at low speeds.
	   */
	  @SuppressWarnings("ParameterName")
	  public void homebrewarcadeDrivePeriodic(double xSpeed, double zRotation) {
	    if (!m_reported) {
	      HAL.report(tResourceType.kResourceType_RobotDrive, 2, tInstances.kRobotDrive_ArcadeStandard);
	      m_reported = true;
	    }

	   

	    
	    

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
	    double leftSetpoint =(leftMotorOutput )* 1023;
	    double rightSetpoint = (rightMotorOutput )*1023 ;
	    _talonLeft.set(ControlMode.Velocity,leftSetpoint  );
	    _talonRight.set(ControlMode.Velocity, rightSetpoint );

	
	  }

	
	

	 


	 
	

	

	
	

}
