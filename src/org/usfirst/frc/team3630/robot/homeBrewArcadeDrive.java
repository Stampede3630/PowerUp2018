
package org.usfirst.frc.team3630.robot;

	import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*
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

	/**
	 * A class for driving differential drive/skid-steer drive platforms such as the Kit of Parts drive
	 * base, "tank drive", or West Coast Drive.
	 *
	 * <p>These drive bases typically have drop-center / skid-steer with two or more wheels per side
	 * (e.g., 6WD or 8WD). This class takes a SpeedController per side. For four and
	 * six motor drivetrains, construct and pass in {@link edu.wpi.first.wpilibj.SpeedControllerGroup}
	 * instances as follows.
	 *
	 * <p>Four motor drivetrain:
	 * <pre><code>
	 * public class Robot {
	 *   Spark m_frontLeft = new Spark(1);
	 *   Spark m_rearLeft = new Spark(2);
	 *   SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_rearLeft);
	 *
	 *   Spark m_frontRight = new Spark(3);
	 *   Spark m_rearRight = new Spark(4);
	 *   SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_rearRight);
	 *
	 *   DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);
	 * }
	 * </code></pre>
	 *
	 * <p>Six motor drivetrain:
	 * <pre><code>
	 * public class Robot {
	 *   Spark m_frontLeft = new Spark(1);
	 *   Spark m_midLeft = new Spark(2);
	 *   Spark m_rearLeft = new Spark(3);
	 *   SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_midLeft, m_rearLeft);
	 *
	 *   Spark m_frontRight = new Spark(4);
	 *   Spark m_midRight = new Spark(5);
	 *   Spark m_rearRight = new Spark(6);
	 *   SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_midRight, m_rearRight);
	 *
	 *   DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);
	 * }
	 * </code></pre>
	 *
	 * <p>A differential drive robot has left and right wheels separated by an arbitrary width.
	 *
	 * <p>Drive base diagram:
	 * <pre>
	 * |_______|
	 * | |   | |
	 *   |   |
	 * |_|___|_|
	 * |       |
	 * </pre>
	 *
	 * <p>Each drive() function provides different inverse kinematic relations for a differential drive
	 * robot. Motor outputs for the right side are negated, so motor direction inversion by the user is
	 * usually unnecessary.
	 *
	 * <p>This library uses the NED axes convention (North-East-Down as external reference in the world
	 * frame): http://www.nuclearprojects.com/ins/images/axis_big.png.
	 *
	 * <p>The positive X axis points ahead, the positive Y axis points right, and the positive Z axis
	 * points down. Rotations follow the right-hand rule, so clockwise rotation around the Z axis is
	 * positive.
	 *
	 * <p>Inputs smaller then {@value edu.wpi.first.wpilibj.drive.RobotDriveBase#kDefaultDeadband} will
	 * be set to 0, and larger values will be scaled so that the full range is still used. This
	 * deadband value can be changed with {@link #setDeadband}.
	 *
	 * <p>RobotDrive porting guide:
	 * <br>{@link #tankDrive(double, double)} is equivalent to
	 * {@link edu.wpi.first.wpilibj.RobotDrive#tankDrive(double, double)} if a deadband of 0 is used.
	 * <br>{@link #arcadeDrive(double, double)} is equivalent to
	 * {@link edu.wpi.first.wpilibj.RobotDrive#arcadeDrive(double, double)} if a deadband of 0 is used
	 * and the the rotation input is inverted eg arcadeDrive(y, -rotation)
	 * <br>{@link #curvatureDrive(double, double, boolean)} is similar in concept to
	 * {@link edu.wpi.first.wpilibj.RobotDrive#drive(double, double)} with the addition of a quick turn
	 * mode. However, it is not designed to give exactly the same response.
	 */

	  public static final double kDefaultQuickStopThreshold = 0.2;
	  public static final double kDefaultQuickStopAlpha = 0.1;

	  private static int instances = 0;

	  private TalonSRX _talonLeft;
	  private TalonSRX _talonRight;

	  private double m_quickStopThreshold = kDefaultQuickStopThreshold;
	  private double m_quickStopAlpha = kDefaultQuickStopAlpha;
	  private double m_quickStopAccumulator = 0.0;
	  private boolean m_reported = false;

	  /**
	   * Construct a DifferentialDrive.
	   *
	   * <p>To pass multiple motors per side, use a {@link SpeedControllerGroup}. If a motor needs to be
	   * inverted, do so before passing it in.
	   */
	  public homeBrewArcadeDrive(  TalonSRX _talonLeft, TalonSRX _talonRight ) {
		  _talonLeft = leftMotor;
		  _talonRight = rightMotor;
	    addChild(m_leftMotor);
	    addChild(m_rightMotor);
	    instances++;
	    setName("DifferentialDrive", instances);
	  }

	  /**
	   * Arcade drive method for differential drive platform.
	   * The calculated values will be squared to decrease sensitivity at low speeds.
	   *
	   * @param xSpeed    The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
	   * @param zRotation The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
	   *                  positive.
	   */
	  @SuppressWarnings("ParameterName")
	  public void arcadeDrive(double xSpeed, double zRotation) {
	    arcadeDrive(xSpeed, zRotation, true);
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
	  public void arcadeDrive(double xSpeed, double zRotation, boolean squaredInputs) {
	    if (!m_reported) {
	      HAL.report(tResourceType.kResourceType_RobotDrive, 2, tInstances.kRobotDrive_ArcadeStandard);
	      m_reported = true;
	    }

	    xSpeed = limit(xSpeed);
	    xSpeed = applyDeadband(xSpeed, m_deadband);

	    zRotation = limit(zRotation);
	    zRotation = applyDeadband(zRotation, m_deadband);

	    // Square the inputs (while preserving the sign) to increase fine control
	    // while permitting full power.
	    if (squaredInputs) {
	      xSpeed = Math.copySign(xSpeed * xSpeed, xSpeed);
	      zRotation = Math.copySign(zRotation * zRotation, zRotation);
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

	  
	    _talonLeft.set(ControlMode.Velocity, (limit(leftMotorOutput) * m_maxOutput)*( 500.0 * 4096 / 600) );
	    _talonRight.set(ControlMode.Velocity, (limit(leftMotorOutput) * m_maxOutput)*( 500.0 * 4096 / 600) );

	    m_safetyHelper.feed();
	  }

	
	

	  /**
	   * Sets the QuickStop speed threshold in curvature drive.
	   *
	   * <p>QuickStop compensates for the robot's moment of inertia when stopping after a QuickTurn.
	   *
	   * <p>While QuickTurn is enabled, the QuickStop accumulator takes on the rotation rate value
	   * outputted by the low-pass filter when the robot's speed along the X axis is below the
	   * threshold. When QuickTurn is disabled, the accumulator's value is applied against the computed
	   * angular power request to slow the robot's rotation.
	   *
	   * @param threshold X speed below which quick stop accumulator will receive rotation rate values
	   *                  [0..1.0].
	   */
	  public void setQuickStopThreshold(double threshold) {
	    m_quickStopThreshold = threshold;
	  }

	  /**
	   * Sets the low-pass filter gain for QuickStop in curvature drive.
	   *
	   * <p>The low-pass filter filters incoming rotation rate commands to smooth out high frequency
	   * changes.
	   *
	   * @param alpha Low-pass filter gain [0.0..2.0]. Smaller values result in slower output changes.
	   *              Values between 1.0 and 2.0 result in output oscillation. Values below 0.0 and
	   *              above 2.0 are unstable.
	   */
	  public void setQuickStopAlpha(double alpha) {
	    m_quickStopAlpha = alpha;
	  }

	  @Override
	  public void stopMotor() {
	    m_leftMotor.stopMotor();
	    m_rightMotor.stopMotor();
	    m_safetyHelper.feed();
	  }

	  @Override
	  public String getDescription() {
	    return "DifferentialDrive";
	  }

	  @Override
	  public void initSendable(SendableBuilder builder) {
	    builder.setSmartDashboardType("DifferentialDrive");
	    builder.addDoubleProperty("Left Motor Speed", m_leftMotor::get, m_leftMotor::set);
	    builder.addDoubleProperty(
	        "Right Motor Speed",
	        () -> -m_rightMotor.get(),
	        x -> m_rightMotor.set(-x));
	  }
	

}
