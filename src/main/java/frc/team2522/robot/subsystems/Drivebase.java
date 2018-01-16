package frc.team2522.robot.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drivebase {
    //TODO: Make a constants file that maps roborio ports to subsystem names to avoid conflicts
    VictorSP leftDrive1 = new VictorSP(0);
    VictorSP leftDrive2 = new VictorSP(1);
    VictorSP rightDrive1 = new VictorSP(2);
    VictorSP rightDrive2 = new VictorSP(3);

    SpeedControllerGroup leftMotors = new SpeedControllerGroup(leftDrive1, leftDrive2);
    SpeedControllerGroup rightMotors = new SpeedControllerGroup(rightDrive1, rightDrive2);
    DifferentialDrive driveControl = new DifferentialDrive(leftMotors, rightMotors);

    public Drivebase() { }

    public void setPower(double leftPower, double rightPower) {
        driveControl.tankDrive(leftPower, rightPower, true);
    }
}
