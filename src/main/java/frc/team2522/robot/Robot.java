package frc.team2522.robot;

import java.lang.*;
import java.util.*;

import frc.team2522.robot.autonomous.*;
import frc.team2522.robot.subsystems.*;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.*;

import com.kauailabs.navx.frc.AHRS;

public class Robot extends IterativeRobot {
    Joystick leftStick = new Joystick(0);
    Joystick rightStick = new Joystick(1);

    Drivebase drivebase = new Drivebase();

    Servo servo = new Servo(9);

    AHRS gyro = new AHRS(SPI.Port.kMXP);

    AutoController autoController;

    @Override
    public void robotInit() {
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
        List<AutoStep> autoSteps = new ArrayList<>();
        autoSteps.add(new AutoDrive(drivebase));
        autoController = new AutoController(autoSteps);
    }

    @Override
    public void autonomousPeriodic() {
        autoController.periodic();
    }

    @Override
    public void teleopInit() { }

    @Override
    public void teleopPeriodic() {
        double leftPower = leftStick.getY(GenericHID.Hand.kLeft);
        double rightPower = rightStick.getY(GenericHID.Hand.kRight);
        drivebase.setPower(leftPower, rightPower);

        double angle = SmartDashboard.getNumber("Servo/angle", 0);
        servo.setAngle(angle);
    }
}
