package frc.team2522.robot;

import java.lang.*;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    Joystick leftStick = new Joystick(0);
    Joystick rightStick = new Joystick(1);

    VictorSP leftDrive1 = new VictorSP(0);
    VictorSP leftDrive2 = new VictorSP(1);
    VictorSP rightDrive1 = new VictorSP(2);
    VictorSP rightDrive2 = new VictorSP(3);

    SpeedControllerGroup leftMotors = new SpeedControllerGroup(leftDrive1, leftDrive2);
    SpeedControllerGroup rightMotors = new SpeedControllerGroup(rightDrive1, rightDrive2);
    DifferentialDrive driveControl = new DifferentialDrive(leftMotors, rightMotors);

    Servo servo = new Servo(9);

    AHRS gyro = new AHRS(SPI.Port.kMXP);

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
    public void autonomousInit() { }

    @Override
    public void autonomousPeriodic() { }

    @Override
    public void teleopInit() { }

    @Override
    public void teleopPeriodic() {
        double leftPower = leftStick.getY(GenericHID.Hand.kLeft);
        double rightPower = rightStick.getY(GenericHID.Hand.kRight);
        driveControl.tankDrive(leftPower, rightPower,true);

        double angle = SmartDashboard.getNumber("Servo/angle", 0);
        servo.setAngle(angle);
    }
}
