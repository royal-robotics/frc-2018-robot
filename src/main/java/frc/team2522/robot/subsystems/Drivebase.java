package frc.team2522.robot.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/*
    Drivebase owns all sensors and motors that are part of the sub assembly.

    During Teleop `fmsUpdate` is called. Joystick values should be read at this
    time and motors/modes should be updated.

    DriveController is an important subclass that's responsible for autonomously
    controlling the drive motors. Some of this functionality will be exposed publicly
    so that it can be set by the AutonomousController. It can also be used in various
    teleop 'modes'
 */
public class Drivebase {
    VictorSP leftDrive1 = new VictorSP(0);
    VictorSP leftDrive2 = new VictorSP(1);
    VictorSP rightDrive1 = new VictorSP(2);
    VictorSP rightDrive2 = new VictorSP(3);

    SpeedControllerGroup leftMotors = new SpeedControllerGroup(leftDrive1, leftDrive2);
    SpeedControllerGroup rightMotors = new SpeedControllerGroup(rightDrive1, rightDrive2);
    DifferentialDrive differentialDrive = new DifferentialDrive(leftMotors, rightMotors);

    DriveData driveDataLeft = new DriveData(2, 3, true);
    DriveData driveDataRight = new DriveData(4,5, false);

    DriveController driveController = new DriveController(differentialDrive, driveDataLeft, driveDataRight);

    Joystick driver;

    public Drivebase(Joystick driver) {
        this.driver = driver;
        reset();
    }

    public void fmsUpdate() {
        differentialDrive.tankDrive(driver.getRawAxis(1), driver.getRawAxis(5), true);
    }

    public void reset() {
        driveDataLeft.reset();
        driveDataRight.reset();
    }
}
