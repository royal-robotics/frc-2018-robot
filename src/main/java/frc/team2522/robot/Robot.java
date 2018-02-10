package frc.team2522.robot;

import edu.wpi.first.wpilibj.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.kauailabs.navx.frc.AHRS;

import frc.team2522.robot.autonomous.*;
import frc.team2522.robot.subsystems.*;

public class Robot extends IterativeRobot {
    Joystick controller = new Joystick(0);

    Drivebase drivebase = new Drivebase();
    DriveData driveDataLeft = new DriveData("left", 2, 3, true);
    DriveData driveDataRight = new DriveData("right", 6,7, false);
    DriveController driveController = new DriveController(drivebase, driveDataLeft, driveDataRight);

    AHRS gyro = new AHRS(SPI.Port.kMXP);

    @Override
    public void robotInit() {
        gyro.reset();

        SmartDashboard.putString("example/test-string", "hello world");
        SmartDashboard.putBoolean("example/test-boolean", true);
        SmartDashboard.putNumber("example/test-number", 42);
    }

    @Override
    public void disabledInit() { }

    @Override
    public void disabledPeriodic() { }

    @Override
    public void autonomousInit() {
        driveDataLeft.reset();
        driveDataRight.reset();
        driveController.Start();
    }

    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void teleopInit() {
        driveDataLeft.reset();
        driveDataRight.reset();
        driveController.Stop();
    }

    int teleopCounter = 0;

    @Override
    public void teleopPeriodic() {
        SmartDashboard.putNumber("teleop/counter", teleopCounter++);
        driveDataLeft.getPosition();
        driveDataRight.getPosition();

        drivebase.setPower(controller.getRawAxis(1), controller.getRawAxis(5));
    }
}