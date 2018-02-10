package frc.team2522.robot;

import java.lang.*;
import java.util.*;

import frc.team2522.robot.autonomous.*;
import frc.team2522.robot.subsystems.*;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.*;

import com.kauailabs.navx.frc.AHRS;

import jaci.pathfinder.*;
import jaci.pathfinder.modifiers.TankModifier;
import javafx.scene.Camera;

public class Robot extends IterativeRobot {
    //Joystick leftStick = new Joystick(0);
    //Joystick rightStick = new Joystick(1);
    Joystick controller = new Joystick(0);

    Drivebase drivebase = new Drivebase();
    DriveData driveDataLeft = new DriveData("left", 2, 3, true);
    DriveData driveDataRight = new DriveData("right", 4,5, false);
    DriveController driveController = new DriveController(drivebase, driveDataLeft, driveDataRight);

    Servo servo = new Servo(9);

    AHRS gyro = new AHRS(SPI.Port.kMXP);

    @Override
    public void robotInit() {
        CameraServer.getInstance().startAutomaticCapture();
        System.out.println("robot init!");

        gyro.reset();
        SmartDashboard.putNumber("Servo/angle", 0);

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
        //drivebase.setPower(0, 0);
        //System.out.printf("left-vel: %f\n", driveDataLeft.getVelocity());
        //drivebase.setPower(-1, -1);
    }

    @Override
    public void teleopInit() {
        driveController.Stop();
    }

    @Override
    public void teleopPeriodic() {
        //double leftPower = leftStick.getY(GenericHID.Hand.kLeft);
        //double rightPower = rightStick.getY(GenericHID.Hand.kRight);
        drivebase.setPower(controller.getRawAxis(1), controller.getRawAxis(5));

        double angle = SmartDashboard.getNumber("Servo/angle", 0);
        servo.setAngle(angle);
    }
}
