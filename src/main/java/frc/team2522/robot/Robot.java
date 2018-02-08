package frc.team2522.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Robot extends IterativeRobot {
    Joystick controller = new Joystick(0);

    SpeedControllerGroup leftMotors = new SpeedControllerGroup(new VictorSP(0), new VictorSP(1));
    SpeedControllerGroup rightMotors = new SpeedControllerGroup(new VictorSP(2), new VictorSP(3));
    DifferentialDrive driveControl = new DifferentialDrive(leftMotors, rightMotors);

    DoubleSolenoid gearPushout = new DoubleSolenoid(0, 0, 7);
    boolean previousButtonValueA = false;
    boolean previousButtonValueB = false;
    boolean isAutoDrivingMode = false;
    @Override
    public void robotInit() {
        System.out.println("ROBOT INIT");

    }

    @Override
    public void disabledInit() { }

    @Override
    public void disabledPeriodic() { }

    @Override
    public void autonomousInit() {
    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopInit() {
        gearPushout.set(DoubleSolenoid.Value.kReverse);
    }

    @Override
    public void teleopPeriodic() {
//        driveControl.tankDrive(controller.getRawAxis(1), controller.getRawAxis(5));

        boolean buttonValueA = controller.getRawButton(1);

       if (buttonValueA == true && previousButtonValueA == false) {
            if (gearPushout.get() == DoubleSolenoid.Value.kReverse) {
                gearPushout.set(DoubleSolenoid.Value.kForward);
            } else {
                gearPushout.set(DoubleSolenoid.Value.kReverse);
           }
        }

        previousButtonValueA = buttonValueA;

        boolean buttonValueB = controller.getRawButton (2);

        if (buttonValueB && !previousButtonValueB) {
            isAutoDrivingMode = !isAutoDrivingMode;

        }
        if (isAutoDrivingMode) {
            driveControl.tankDrive(10,10);
        } else {
            driveControl.tankDrive(controller.getRawAxis(1), controller.getRawAxis(5));
        }
        previousButtonValueB = buttonValueB;
    }



}

