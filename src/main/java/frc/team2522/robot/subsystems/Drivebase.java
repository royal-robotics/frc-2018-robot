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

//    DriveData driveDataLeft = new DriveData(2, 3, true);
//    DriveData driveDataRight = new DriveData(4,5, false);

    public Drivebase() {
        reset();
    }

    public void setPower(double leftPower, double rightPower) {

        //System.out.printf("Left: P=%f, V=%f, A=%f\n", driveDataLeft.getPosition(), driveDataLeft.getVelocity(), driveDataLeft.getAcceleration());
        driveControl.tankDrive(leftPower, rightPower, true);
    }

    public void reset() {
//        driveDataLeft.reset();
//        driveDataRight.reset();
    }
}
