/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
//second commit test
package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.ExampleSubsystem;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;



import frc.robot.RobotMap;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
 
  //initializing subsystems
  public static ExampleSubsystem m_subsystem = new ExampleSubsystem();
  public static DriveTrain m_drivetrain = null;
  public static OI m_oi;

   //initializing the color sensors
   public static boolean isColorSensedBLue = false;
   private final I2C.Port i2cPort = I2C.Port.kOnboard;
   private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);
   private final ColorMatch m_colorMatch = new ColorMatch();
   private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
   private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
   private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
   private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113); 

   //initializing motors(temporarily?)
   TalonSRX leftFrontTalon = new TalonSRX(RobotMap.DRIVETRAIN_LEFT_FRONT_TALON);
   TalonSRX rightFrontTalon = new TalonSRX(RobotMap.DRIVETRAIN_RIGHT_FRONT_TALON);
   VictorSPX leftBackVictor = new VictorSPX(RobotMap.DRIVETRAIN_LEFT_BACK_VICTOR);
   VictorSPX rightBackVictor = new VictorSPX(RobotMap.DRIVETRAIN_RIGHT_BACK_VICTOR);
   DifferentialDrive differentialDrive = null;

   //color loop code
 
   static int colorChange = 0; // counts the amounts of times the color has been changed from the inital color
   static boolean isColorTheInitial = true; //used to determine if the color has changed or not
   static boolean controlBooleanCode = true;  
   static boolean hasInitialColorBeenSet = false;
   static String initialColor;


  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_drivetrain = new DriveTrain();
    m_oi = new OI();
    m_chooser.setDefaultOption("Default Auto", new ExampleCommand());
    // chooser.addOption("My Auto", new MyAutoCommand());
    SmartDashboard.putData("Auto mode", m_chooser);

    //adds the target colors to the Color Match
    m_colorMatch.addColorMatch(kBlueTarget);
    m_colorMatch.addColorMatch(kGreenTarget);
    m_colorMatch.addColorMatch(kRedTarget);
    m_colorMatch.addColorMatch(kYellowTarget);   
    leftBackVictor.set(ControlMode.Follower, leftFrontTalon.getDeviceID()); //setting leftBackVictor to follow leftFrontTalon
    rightBackVictor.set(ControlMode.Follower, rightFrontTalon.getDeviceID()); 
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
     /**p
     * The method GetColor() returns a normalized color value from the sensor and can be
     * useful if outputting the color to an RGB LED or similar. To
     * read the raw color, use GetRawColor().
     * 
     * The color sensor works best when within a few inches from an object in
     * well lit conditions (the built in LED is a big help here!). The farther
     * an object is the more light from the surroundings will bleed into the 
     * measurements and make it difficult to accurately determine its color.
     */
    Color detectedColor = m_colorSensor.getColor();

    /**
     * Run the color match algorithm on our detected color
     */
    String colorString = null;
   
    ColorMatchResult match = m_colorMatch.matchClosestColor(detectedColor);
    
    if (match.color == kBlueTarget) {
      System.out.println("The main loop is currently running");
      colorString = "Blue";
      //isColorSensedBLue = true;
       } else if (match.color == kRedTarget) {
      colorString = "Red";
      //isColorSensedBLue = false;
    } else if (match.color == kGreenTarget) {
      colorString = "Green";
      //isColorSensedBLue = false;
    } else if (match.color == kYellowTarget) {
      colorString = "Yellow";
      //isColorSensedBLue = false;
    } else {
      colorString = "Unknown";
      //isColorSensedBLue = false;
    } 

    if (Robot.hasInitialColorBeenSet != true){
      Robot.initialColor = colorString; //establish the initial color
      Robot.hasInitialColorBeenSet = true;
    }
   

    if(!colorString.equals(Robot.initialColor)){
        if(Robot.controlBooleanCode == true){
          Robot.controlBooleanCode = false;
          Robot.colorChange++;
          System.out.println("The color was changed, and the color has changed " + Robot.colorChange + " times from " + Robot.initialColor );
          
        }        
    } else{
      Robot.controlBooleanCode = true;
    }
    SmartDashboard.putString("Initial Color", Robot.initialColor);
    SmartDashboard.putNumber("Red", detectedColor.red);
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
    SmartDashboard.putNumber("Confidence", match.confidence);
    SmartDashboard.putString("Detected Color", colorString);
    SmartDashboard.putNumber("Color Changed From Initial", Robot.colorChange);
      
      
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   * You can use it to reset any subsystem information you want to clear when
   * the robot is disabled.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString code to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons
   * to the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_chooser.getSelected();

    /*
     * String autoSelected = SmartDashboard.getString("Auto Selector",
     * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
     * = new MyAutoCommand(); break; case "Default Auto": default:
     * autonomousCommand = new ExampleCommand(); break; }
     */

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
