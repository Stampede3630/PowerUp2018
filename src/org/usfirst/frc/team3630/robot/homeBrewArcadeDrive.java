
package org.usfirst.frc.team3630.robot;

	import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class homeBrewArcadeDrive extends RobotDriveBasee {
	/*----------------------------------------------------------------------------*/
	/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
	/* Open Source Software - may be modified and shared by FRC teams. The code   */
	/* must be accompanied by the FIRST BSD license file in the root directory of */
	/* the project.                                                               */
	/*----------------------------------------------------------------------------*/


	  public static final double kDefaultQuickStopThreshold = 0.2;
	  public static final double kDefaultQuickStopAlpha = 0.1;

	  private static int instances = 0;

	  private TalonSRX _talonLeft;
	  private TalonSRX _talonRight;

	  private double m_quickStopThreshold = kDefaultQuickStopThreshold;
	  private double m_quickStopAlpha = kDefaultQuickStopAlpha;
	  private double m_quickStopAccumulator = 0.0;
	  private boolean m_reported = false;


	  public homeBrewArcadeDrive(  TalonSRX _talonLeft, TalonSRX _talonRight ) {
		  _talonLeft = leftMotor;
		  _talonRight = rightMotor;
		  // eventualy put talon closed loop conctuct in here 
	   
	    
	    
	  }

	  /**
	   * Arcade drive method for differential drive platform.
	   * The calculated values will be squared to decrease sensitivity at low speeds.
	   *
	   * @param xSpeed    The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
	   * @param zRotation The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
	   *                  positive.
	   */
	
	

	  /**
	   * Arcade drive method for differential drive platform.
	   *
	   * @param xSpeed        The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
	   * @param zRotation     The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
	   *                      positive.
	   * @param squaredInputs If set, decreases the input sensitivity at low speeds.
	   */
	  @SuppressWarnings("ParameterName")
	  public void homebrewarcadeDrive(double xSpeed, double zRotation) {
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
	    double leftSetpoint =(leftMotorOutput )*( 500.0 * 4096 / 600);
	    double rightSetpoint = (rightMotorOutput )*( 500.0 * 4096 / 600) ;
	    _talonLeft.set(ControlMode.Velocity,leftSetpoint  );
	    _talonRight.set(ControlMode.Velocity, rightSetpoint );

	
	  }

	
	

	 


	 
	

	

	
	

}
