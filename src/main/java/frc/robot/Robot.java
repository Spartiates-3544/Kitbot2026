// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  // Fields (not locals!) so teleopPeriodic can see them
  private final WPI_VictorSPX leftLead = new WPI_VictorSPX(1);
  private final WPI_VictorSPX leftFollow = new WPI_VictorSPX(3);
  private final WPI_VictorSPX rightLead = new WPI_VictorSPX(2);
  private final WPI_VictorSPX rightFollow = new WPI_VictorSPX(4);

  private final XboxController pad = new XboxController(0);

  private static final double DEADBAND = 0.08;
  private static final double MAX = 0.5;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  public Robot() {
    for (WPI_VictorSPX m : new WPI_VictorSPX[] {leftLead, leftFollow, rightLead, rightFollow}) {
      m.configFactoryDefault();
      m.setNeutralMode(NeutralMode.Brake);
    }
    
    // Followers copy their lead's output
    leftFollow.follow(leftLead);
    rightFollow.follow(rightLead);

    // Right side faces the other way, so invert it
    leftLead.setInverted(false);
    leftFollow.setInverted(InvertType.FollowMaster);
    rightLead.setInverted(true);
    rightFollow.setInverted(InvertType.FollowMaster);

  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {
    // Keep the watchdog fed and motors stopped in auto (for now)
    leftLead.set(ControlMode.PercentOutput, 0.0);
    rightLead.set(ControlMode.PercentOutput, 0.0);
  }

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    double fwd = -pad.getLeftY()*MAX; // stick Y is negative when pushed forward
    double turn = pad.getRawAxis(2)*MAX; // flip this sign if it turns the wrong way

    // 1. Deadband
    fwd = deadband(fwd, DEADBAND);
    turn = deadband(turn, DEADBAND);


    // 3. Mix
    double left = fwd + turn;
    double right = fwd - turn;

    leftLead.set(ControlMode.PercentOutput, left);
    rightLead.set(ControlMode.PercentOutput, right);
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}

  /** Zero inside the deadband */
  private static double deadband(double value, double db) {
    if (Math.abs(value) < db) {
      return 0.0;
    }
    return value;
  }
}