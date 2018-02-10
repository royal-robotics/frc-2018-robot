package frc.team2522.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import java.lang.*;
import com.kauailabs.navx.frc.AHRS;

public class Robot extends IterativeRobot {
    Joystick controller = new Joystick(0);

    SpeedControllerGroup leftMotors = new SpeedControllerGroup(new VictorSP(0), new VictorSP(1));
    SpeedControllerGroup rightMotors = new SpeedControllerGroup(new VictorSP(2), new VictorSP(3));
    DifferentialDrive driveControl = new DifferentialDrive(leftMotors, rightMotors);

    DoubleSolenoid gearPushout = new DoubleSolenoid(0, 0, 7);
    boolean previousButtonValueA = false;
    boolean previousButtonValueB = false;
    boolean isAutoDrivingMode = false;

    Encoder leftDriveEncoder = new Encoder(new DigitalInput(2), new DigitalInput(3));
    Encoder rightDriveEncoder = new Encoder(new DigitalInput(4), new DigitalInput(5));

    AHRS gyro = new AHRS(SPI.Port.kMXP);

    int stepNumber;


    @Override
    public void robotInit() {
        System.out.println("ROBOT INIT");

        final double driveTranDistancePerPulse = (3.50 * 3.1415) / (360.00);
        leftDriveEncoder.setReverseDirection(true);
        leftDriveEncoder.setDistancePerPulse(driveTranDistancePerPulse);
        leftDriveEncoder.reset();
        rightDriveEncoder.setDistancePerPulse(driveTranDistancePerPulse);
        rightDriveEncoder.reset();
    }

    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void autonomousInit() {
        rightDriveEncoder.reset();
        leftDriveEncoder.reset();
        gyro.reset();
        stepNumber = 1;
    }

    @Override
    public void autonomousPeriodic() {
        if (stepNumber == 1) {
            stepNumberOne();
        } else if (stepNumber == 2) {
            stepNumberTwo();
        } else if (stepNumber == 3) {
            stepNumberThree();
        } else {
            driveControl.tankDrive(0,0);
        }

        //driveControl.tankDrive(0.5,0.5); //move forward
    }

    @Override
    public void teleopInit() {
        final double driveTranDistancePerPulse = (3.50 * 3.1415) / (360.00);
        leftDriveEncoder.setReverseDirection(true);
        leftDriveEncoder.setDistancePerPulse(driveTranDistancePerPulse);
        leftDriveEncoder.reset();
        rightDriveEncoder.setDistancePerPulse(driveTranDistancePerPulse);
        rightDriveEncoder.reset();

        gearPushout.set(DoubleSolenoid.Value.kReverse);
    }

    @Override
    public void teleopPeriodic() {
//        driveControl.tankDrive(controller.getRawAxis(1), controller.getRawAxis(5))
        gearPushoutToggle();
        forwardWheelToggle();

    }

    private void forwardWheelToggle() {
        boolean buttonValueB = controller.getRawButton(2);

        if (buttonValueB && !previousButtonValueB) {
            isAutoDrivingMode = !isAutoDrivingMode;
            if (isAutoDrivingMode) {
                leftDriveEncoder.reset();
            }
        }

        final double deadband = 0.1;
        double leftJoystickValue = controller.getRawAxis(1);
        double rightJoystickValue = controller.getRawAxis(5);

        if (Math.abs(leftJoystickValue) > deadband || Math.abs(rightJoystickValue) > deadband) {
            isAutoDrivingMode = false;
        }

        System.out.println(leftDriveEncoder.getDistance());
        if (leftDriveEncoder.getDistance() > Math.abs(50)) {
        isAutoDrivingMode = false;
    }

        if (isAutoDrivingMode) {
            driveControl.tankDrive(10, 10);
        } else {
            driveControl.tankDrive(controller.getRawAxis(1), controller.getRawAxis(5));
        }

        previousButtonValueB = buttonValueB;
    }

    private void gearPushoutToggle() {
        boolean buttonValueA = controller.getRawButton(1);

        if (buttonValueA == true && previousButtonValueA == false) {
            if (gearPushout.get() == DoubleSolenoid.Value.kReverse) {
                gearPushout.set(DoubleSolenoid.Value.kForward);
            } else {
                gearPushout.set(DoubleSolenoid.Value.kReverse);
            }
        }

        previousButtonValueA = buttonValueA;
    }
    public void stepNumberOne () {
        double angle = gyro.getAngle();

        if (leftDriveEncoder.getDistance() < 90) {
            driveControl.tankDrive(-0.8, -0.8);
        } else {
            rightDriveEncoder.reset();
            leftDriveEncoder.reset();
            driveControl.tankDrive(0, 0);
            stepNumber = 2;
        }
    }

    public void stepNumberTwo () {
        double angle = gyro.getAngle();
        if (angle > -90 ) {
            driveControl.tankDrive(0, -0.8);
        } else {
            rightDriveEncoder.reset();
            leftDriveEncoder.reset();
            driveControl.tankDrive(0, 0);
            stepNumber = 3;
        }
    }

    public void stepNumberThree () {
        if (leftDriveEncoder.getDistance() < 50) {
            driveControl.tankDrive(-0.9, -0.9);
        } else {
            rightDriveEncoder.reset();
            leftDriveEncoder.reset();
            stepNumber = 4;
        }
    }
}