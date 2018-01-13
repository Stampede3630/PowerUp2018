package org.usfirst.frc.team3630.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	XboxController xBox;
	Talon leftTalon, rightTalon;
	DifferentialDrive diffDrive;
	@Override
	public void robotInit() {
		xBox = new XboxController(0);
		leftTalon = new Talon(1);
		rightTalon = new Talon(0);
		diffDrive = new DifferentialDrive(leftTalon, rightTalon);
	}
	public void teleopPeriodic() {
		double rotate=xBox.getX(GenericHID.Hand.kRight);
		double speed=xBox.getY(GenericHID.Hand.kLeft);
		diffDrive.arcadeDrive(speed, rotate);
	}
	
}

