/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team3630.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.GenericHID.Hand;

//import org.usfirst.frc.team3630.robot.commands.MecanumDrive_Command;
//import org.usfirst.frc.team3630.robot.commands.SafeDrive_Command;
//import org.usfirst.frc.team3630.robot.commands.Climber_Command;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	
	XboxController controller = new XboxController(0);
	
	final static int aButton = 1;
	final static int bButton = 2;
	final static int xButton = 3;
	final static int yButton = 4;
	final static int leftBumper = 5;
	final static int rightBumper = 6;
	final static int backButton = 7;
	final static int startButton = 8;
	final static int lStickButton = 9;
	final static int rStickButton = 10;
	final static int lStickXAxis = 0;
	final static int lStickYAxis = 1;
	final static int lTriggerAxis = 2;
	final static int rTriggerAxis = 3;
	final static int rStickXAxis = 4;
	final static int rStickYAxis = 5;
	final static double deadzone = 0.1;
	
	public final JoystickButton buttonA = new JoystickButton(controller, aButton);
	public final JoystickButton buttonB = new JoystickButton(controller, bButton);
	public final JoystickButton buttonX = new JoystickButton(controller, xButton);
	public final JoystickButton buttonY = new JoystickButton(controller, yButton);
	public final JoystickButton bumperL = new JoystickButton(controller, leftBumper);
	public final JoystickButton bumperR = new JoystickButton(controller, rightBumper);
	public final JoystickButton backB = new JoystickButton(controller, backButton);	
	public final JoystickButton startB = new JoystickButton(controller, startButton);
	public final JoystickButton leftStickB = new JoystickButton(controller, lStickButton);
	public final JoystickButton rightStickB = new JoystickButton(controller, rStickButton);
	
	public OI()
	{
		
	}
	
	public double deadzone(double input)
	{
		if(Math.abs(input)>deadzone)
		{
			return input;
		}
		else
		{
			return 0;
		}
	}
	
	public double getLeftY()
	{
		return -deadzone(controller.getY(Hand.kLeft));
	}
	
	public double getLeftX()
	{
		return deadzone(controller.getX(Hand.kLeft));
	}
	
	public double getRightY()
	{
		return deadzone(controller.getY(Hand.kRight));
	}
	
	public double getRightX()
	{
		return deadzone(controller.getX(Hand.kRight));
	}
	
	public double getTrigger()
	{
		if(controller.getTriggerAxis(Hand.kRight)>0)
		{
			return deadzone(controller.getTriggerAxis(Hand.kRight));
		}
		else if(controller.getTriggerAxis(Hand.kLeft)>0)
		{
			return -deadzone(controller.getTriggerAxis(Hand.kLeft));
		}
		else
		{
			return 0.0;
		}
		//left trigger has negative value, right trigger has positive value	
	}

	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a
	//// joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.
	// Joystick stick = new Joystick(port);
	// Button button = new JoystickButton(stick, buttonNumber);

	// There are a few additional built in buttons you can use. Additionally,
	// by subclassing Button you can create custom triggers and bind those to
	// commands the same as any other Button.

	//// TRIGGERING COMMANDS WITH BUTTONS
	// Once you have a button, it's trivial to bind it to a button in one of
	// three ways:

	// Start the command when the button is pressed and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenPressed(new ExampleCommand());

	// Run the command while the button is being held down and interrupt it once
	// the button is released.
	// button.whileHeld(new ExampleCommand());

	// Start the command when the button is released and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenReleased(new ExampleCommand());
}
