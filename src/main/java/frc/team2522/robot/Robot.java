package frc.team2522.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

<<<<<<< HEAD
    Joystick leftStick = new Joystick(0);
    Joystick rightStick = new Joystick(1);

    VictorSP leftDrive1 = new VictorSP(0);
    VictorSP leftDrive2 = new VictorSP(1);
    VictorSP rightDrive1 = new VictorSP(2);
    VictorSP rightDrive2 = new VictorSP(3);

    public RobotDrive myDrive = new RobotDrive(leftDrive1, leftDrive2, rightDrive1, rightDrive2);
=======
    Servo servo = new Servo(0);
>>>>>>> connection-and-tunable-ui

    @Override
    public void robotInit() {
        SmartDashboard.putNumber("Servo/angle", 0);

        SmartDashboard.putString("example/test-string", "hello world");
        SmartDashboard.putBoolean("example/test-boolean", true);
        SmartDashboard.putNumber("example/test-number", 42);
    }

    @Override
    public void disabledInit() { }

    @Override
    public void autonomousInit() { }

    @Override
    public void teleopInit() { }

    @Override
    public void testInit() { }

    @Override
    public void disabledPeriodic() { }
    
    @Override
    public void autonomousPeriodic() { }

    @Override
    public void teleopPeriodic() {
<<<<<<< HEAD
        double leftPower = leftStick.getRawAxis(3);
        double rightPower = rightDrive1.get();

        //myDrive.tankDrive(leftPower, rightPower);
        myDrive.tankDrive(leftStick, rightStick, /*squaredInputs*/ true);
=======
        double angle = SmartDashboard.getNumber("Servo/angle", 0);
        System.out.println(angle);
        servo.setAngle(angle);
>>>>>>> connection-and-tunable-ui
    }

    @Override
    public void testPeriodic() { }
}
