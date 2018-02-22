package org.usfirst.frc.team3630.robot;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj.SPI;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * @author deltaA380
 * method for warnings for driver during robot competin
 *
 */
public class driverStationWarnings {

private AHRS aHrs;
private double pitchAngleDegree;
private double rollAngleDegree;
static final double xPitchTollerance = 5.0;
static final double yPitchTollerance = 5.0;
PowerDistributionPanel panel;


public driverStationWarnings (){
	aHrs =  new AHRS(SPI.Port.kMXP);
	panel = new PowerDistributionPanel(0);
}

public void tipOverX(){
	pitchAngleDegree = aHrs.getPitch();
	SmartDashboard.putNumber("AHRS ROLL IN x DIRECTION", pitchAngleDegree);
	if(pitchAngleDegree > xPitchTollerance) {
		System.out.print("WARNING ROBOTS DONT TIP ");
		System.out.print(pitchAngleDegree);
		System.out.print('\n');
		
	}
}

public void pdpTotalCurnet() {
	if(panel.getTotalCurrent()>300) {
		System.out.print("[WARNING] CURRENT DRAW IS AT ");
		System.out.print(panel.getTotalCurrent());
		System.out.print('\n');
	}
}
public void tipOverY(){
	rollAngleDegree = aHrs.getRoll();
	SmartDashboard.putNumber("AHRS ROLL IN Y DIRECTION", rollAngleDegree);
	if(rollAngleDegree >yPitchTollerance) {
		System.out.print("WARNING ROBOTS DONT TIP  ");
		System.out.print(pitchAngleDegree);
		System.out.print('\n');
		
		
	}
	
	
}
}
