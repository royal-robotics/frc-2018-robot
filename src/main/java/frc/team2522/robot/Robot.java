package frc.team2522.robot;

import edu.wpi.first.wpilibj.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import frc.team2522.robot.autonomous.*;
import frc.team2522.robot.subsystems.*;

import jaci.pathfinder.*;
import jaci.pathfinder.modifiers.TankModifier;

public class Robot extends IterativeRobot {
    Joystick leftStick = new Joystick(0);
    Joystick rightStick = new Joystick(1);

    Drivebase drivebase = new Drivebase();

    Servo servo = new Servo(9);

    DoubleSolenoid solenoid = new DoubleSolenoid(3, 4);

    AHRS gyro = new AHRS(SPI.Port.kMXP);

    AutoController autoController;

    Trajectory left;
    Trajectory right;

    private void generateMotionProfile() {

        //MAX ROBOT VELOCITY IS 175 inches/second
        //MAX ROBOT ACCELERATION IS 333.33 inches/second^2

        double wheelbase_width = 31.25;

        Waypoint[] points = new Waypoint[] {
                new Waypoint(0, 0, Pathfinder.d2r(90)),
                new Waypoint(-200, 250, Pathfinder.d2r(135)),
                new Waypoint(0, 500, Pathfinder.d2r(90)),
        };

        Trajectory.Config config = new Trajectory.Config(
                Trajectory.FitMethod.HERMITE_CUBIC,
                Trajectory.Config.SAMPLES_HIGH, //???
                0.01, //10ms
                150,
                150,
                600.0);


        Trajectory trajectory = Pathfinder.generate(points, config);

        // Wheelbase Width = 0.5m
        TankModifier modifier = new TankModifier(trajectory).modify(wheelbase_width);

        // Do something with the new Trajectories...
        left = modifier.getLeftTrajectory();
        right = modifier.getRightTrajectory();
    }

    @Override
    public void robotInit() {
        //generateMotionProfile();

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

    long autoStartTime;

    @Override
    public void autonomousInit() {
//        List<AutoStep> autoSteps = new ArrayList<>();
//        autoSteps.add(new AutoDrive(drivebase));
//        autoController = new AutoController(autoSteps);

        autoStartTime = System.nanoTime();
    }

    @Override
    public void autonomousPeriodic() {
//        autoController.periodic();

        long diffTime = System.nanoTime() - autoStartTime;

        double diffMilliSeconds = ((double) diffTime) / 1000000;

        int seg = ((int) Math.round(diffMilliSeconds)) / 10;
//        System.out.printf("seg number: %d, left-length: %d\n", seg, left.length());
        if (seg < left.length() - 1) {
            double leftVel = left.get(seg).velocity; //inches/second
            double rightVel = right.get(seg).velocity; //inches/second

            double leftPower = leftVel / 175;
            double rightPower = rightVel / 175;

            drivebase.setPower(-leftPower, -rightPower);
        } else {
            drivebase.setPower(0, 0);
        }
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

        if (leftStick.getRawButton(1)) {
            solenoid.set(DoubleSolenoid.Value.kForward);
        } else {
            solenoid.set(DoubleSolenoid.Value.kReverse);
        }
    }
}
