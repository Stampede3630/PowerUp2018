package org.usfirst.frc.team3630.robot;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
public class TalonTester extends WPI_TalonSRX {
	public TalonTester (int address) {
		super (address);
	}
/*	public ErrorCode setSelectedSensorPosition(int address, int secondInput, int timeout) {
		System.out.println ("Encoder reset call");
		return this.setSelectedSensorPosition(address, secondInput, timeout);
	}
	*/
}