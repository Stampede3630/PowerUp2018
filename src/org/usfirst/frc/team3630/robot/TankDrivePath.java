package org.usfirst.frc.team3630.robot;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;
import com.ctre.phoenix.*;
import edu.wpi.first.wpilibj.smartdashboard.*;;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;


public class TankDrivePath {
//	  Trajectory right,left;\
	TankModifier modifier;
	private  TalonSRX lTalon;
	private  TalonSRX rTalon;
	EncoderFollower left, right;
	public TankDrivePath ( TalonSRX leftSRXSide,TalonSRX rightSRXSide) {
		 
		// Create the Trajectory Configuration
		//
		// Arguments:
		// Fit Method:          HERMITE_CUBIC or HERMITE_QUINTIC
		// Sample Count:        SAMPLES_HIGH (100 000)
//		              SAMPLES_LOW  (10 000)
//		                      SAMPLES_FAST (1 000)
		// Time Step:           0.05 Seconds
		// Max Velocity:        1.7 m/s
		// Max Acceleration:    2.0 m/s/s
		// Max Jerk:            60.0 m/s/s/s
		
		
		// comsts ok???
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.05, 135, 500, 500);
	
		 Waypoint[] points = new Waypoint[] {
	            //    new Waypoint(-4, -1, Pathfinder.d2r(-45)),
	        		   new Waypoint(0, 0, 0),   // Waypoint @ x=-4, y=-1, exit angle=-45 degrees
	              // connts under setpoint acoring to math. why dose pathfinder stop??
	        	
				 new Waypoint(168, 0, 0)
			     
			
	           };

            Trajectory trajectory = Pathfinder.generate(points, config);

            // Wheelbase Width = in acurate ?
             modifier = new TankModifier(trajectory).modify(14);

            // Do something with the new Trajectories...
            Trajectory    leftT = modifier.getLeftTrajectory();
            Trajectory     rightT = modifier.getRightTrajectory();
            
            
        
           
            lTalon =  leftSRXSide;
            rTalon = rightSRXSide;
         
            rTalon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0,10);
            lTalon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0,10);
          
            
            left = new EncoderFollower(modifier.getLeftTrajectory());
        	right = new EncoderFollower(modifier.getRightTrajectory());
        	 // set encoder infomartion 
        	 	left.configureEncoder(0, Consts.ticksPerRotation, 6);
        	 	right.configureEncoder(0, Consts.ticksPerRotation, 6);
        	    // outputs data from path put to csv 
             // lets produce trajectory graphs. to check conttants are correct ? 
        	 	// pos and velocity per time 
        	 	//acceloration per time 
        	 	// any other graphs?
             /*   for (int i = 0; i<left.length(); i++){
                	
                	 System.out.print(left.get(i).acceleration);
                	 System.out.print(",");
                	 System.out.print(left.get(i).dt);
                	 System.out.print(",");
                   	 System.out.print(left.get(i).heading);
                	 System.out.print(",");
                 	 System.out.print(left.get(i).jerk);
                	 System.out.print(",");
                	 System.out.print(left.get(i).position);
                	 System.out.print(",");
                	 System.out.print(left.get(i).velocity);
                	 System.out.print(",");
                	 System.out.print(left.get(i).x);
                	 System.out.print(",");
                	 System.out.print(left.get(i).y);
                 
                	 System.out.print("\n");


                }
                */

        		// set encoders 
        		
        		// configure pidva 
        		// The first argument is the proportional gain. Usually this will be quite high
        		// The second argument is the integral gain. This is unused for motion profiling
        		// The third argument is the derivative gain. Tweak this if you are unhappy with the tracking of the trajectory
        		// The fourth argument is the velocity ratio. This is 1 over the maximum velocity you provided in the 
//        		 trajectory configuration (it translates m/s to a -1 to 1 scale that your motors can read)
        		// The fifth argument is your acceleration gain. Tweak this if you want to get to a higher or lower speed quicker

        		// is kf term correct at 1/135? i don't know??
        	 	
        		
        		left.configurePIDVA(50, 0.0, 0.0, 1/135 , 0);
        		right.configurePIDVA(50, 0.0, 0.0, ( 1/135) , 0);
	}
	
	public int  getDistance(TalonSRX _talon) {
		// gets ticks ?? 
		int  distance_ticks = _talon.getSelectedSensorPosition(0);
		
		return distance_ticks;
	}
	public double reConvert(double  output) {
	
		double rads = output * Consts.rotConversion;
		double ticks = rads *1000;
		double ms = (ticks /1000)*100;
		return ms ;
	}
	public void pathFeedback(){
		
	
	
	
	
	
	// get desired output  . calucate coverts ticks to inches based on input 
	
		
	// output should be between -1 and 1 correct? should check output and print it out 
	 double outputLeft = left.calculate( getDistance(lTalon)  );
	
	//System.out.println(getDistance(lTalon)) ;
	 
	 
	 double outputRight = right.calculate( getDistance(rTalon));
	 
	 
	 SmartDashboard.putNumber("pathtLeft ", outputLeft);
	 SmartDashboard.putNumber("PathRight ", outputRight);
	// SmartDashboard.putString("talonmode", value)
	 
	 
	
	SmartDashboard.putNumber("left encoder  ", getDistance(lTalon));
	SmartDashboard.putNumber("Right encoder ", getDistance(rTalon ));
	
	 
	 lTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput,  outputLeft) ;
	 rTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, outputRight);

	
	
	
	}
	        }

  

    
  

