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

    Encoder leftDriveEncoder = new Encoder(new DigitalInput(2), new DigitalInput(3));
    Encoder rightDriveEncoder = new Encoder(new DigitalInput(4), new DigitalInput(5));

    DriveData driveDataLeft = new DriveData(leftDriveEncoder);
    DriveData driveDataRight = new DriveData(rightDriveEncoder);

    public Drivebase() {
        final double driveDistancePerPulse = (3.50 * 3.1415) / (360.00);

        leftDriveEncoder.setReverseDirection(true);
        leftDriveEncoder.setDistancePerPulse(driveDistancePerPulse);
        rightDriveEncoder.setDistancePerPulse(driveDistancePerPulse);

        reset();
    }

    public void setPower(double leftPower, double rightPower) {
        System.out.printf("Left: P=%f, V=%f, A=%f\n", driveDataLeft.getPosition(), driveDataLeft.getVelocity(), driveDataLeft.getAcceleration());
        driveControl.tankDrive(leftPower, rightPower, true);
    }

    public void reset() {
        driveDataLeft.reset();
        driveDataRight.reset();
    }
}
