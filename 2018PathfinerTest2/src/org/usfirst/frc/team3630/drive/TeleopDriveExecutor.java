package org.usfirst.frc.team3630.drive;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import org.usfirst.frc.team3630.robot.Robot;
import org.usfirst.frc.team3630.robot.RobotMap;

public class TeleopDriveExecutor 
{	
	public TeleopDriveExecutor()
	{
		
	}
	
	public void execute()
	{
/*		if(Robot.oi.getTrigger() < -0.5)
		{
			RobotMap.drive.arcadeDrive(Robot.oi.getLeftY(), Robot.oi.getRightX());
		}
		*/
			RobotMap.drive.arcadeDrive(0.7*Robot.oi.getLeftY(), 0.7*Robot.oi.getRightX());
	}
}
