package frc.team2522.robot.subsystems.Drivebase;

import com.ctre.phoenix.drive.DriveMode;
import com.ctre.phoenix.mechanical.Gearbox;
import com.ctre.phoenix.motorcontrol.IMotorController;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.Controls;
import frc.team2522.robot.libs.*;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

import java.io.File;
import java.util.TimerTask;

/**
 *
 */
public class DriveController {

    // TODO: calculate these values
    //
    public static final double kWheelbaseWidth = 25.0;
    public static final double kHighGearMaxVelocity = 160.0;    // inches / second
    public static final double kHighGearDistancePerPulse = 0.3429 * 6.0 * Math.PI / 256.0;
    public static final double kLowGearMaxVelocity = 90.0;     // inches / second
    public static final double kLowGearDistancePerPulse = 0.1667 * 6.0 * Math.PI / 256.0;

    public static final double kUpdateFrequency = 0.01;  // 100 times per second
    public static final double kProportionalFactor = 0.4;
    public static final double kIntegralFactor = 0.0;
    public static final double kDifferentialFactor = 0.0;

    public enum Gear {
        High,
        Low
    }

    private IMotorController leftMotor;
    private Gearbox leftMotors;
    private Encoder leftEncoder;

    private IMotorController rightMotor;
    private Gearbox rightMotors;
    private Encoder rightEncoder;

    private DoubleSolenoid shifter;
    private DoubleSolenoid pto;

    private Gear gear = Gear.High;
    private double maxVelocity;

    private DriveType driveType = DriveType.DiffDrive;
    public TankDrive tankDrive;

    private java.util.Timer timer = null;
    private long leftLastUpdateTime = System.nanoTime();
    private long rightLastUpdateTime = System.nanoTime();

    private double leftPower = 0.0;
    private double leftVelocity = 0.0;
    private double leftLastDistance = 0.0;
    private double leftLastVelocity = 0.0;

    private double rightPower = 0.0;
    private double rightVelocity = 0.0;
    private double rightLastDistance = 0.0;
    private double rightLastVelocity = 0.0;

    private double maxDetectedVelocity = 0.0;
    private TrajectoryFollower follower = null;


    public DriveController(IMotorController leftDriveMotor, Encoder leftDriveEncoder, IMotorController rightDriveMotor, Encoder rightDriveEncoder, DoubleSolenoid shifter, DoubleSolenoid pto) {
        this.leftMotor = leftDriveMotor;
        this.leftMotors = new Gearbox(leftDriveMotor);
        this.leftEncoder = leftDriveEncoder;

        this.rightMotor = rightDriveMotor;
        this.rightMotors = new Gearbox(rightDriveMotor);
        this.rightEncoder = rightDriveEncoder;

        this.shifter = shifter;
        this.pto = pto;

        this.driveType = DriveType.DiffDrive;
        this.tankDrive = new TankDrive(leftMotors, rightMotors);

        this.reset();

        this.leftLastUpdateTime = System.nanoTime();
        this.timer = new java.util.Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                update();
            }
        }, 0, Math.round(kUpdateFrequency * 1000.0));
    }

    /**
     *
     */
    public void reset() {
        this.setGear(Gear.High);
        this.setPTO(false);
        this.leftEncoder.reset();
        this.rightEncoder.reset();
        this.maxDetectedVelocity = 0.0;
    }

    /**
     *
     */
    public void update() {
        long time = System.nanoTime();
        double leftDistance = this.leftEncoder.getDistance();
        double rightDistance = this.rightEncoder.getDistance();

        double lDt = (double)(time - this.leftLastUpdateTime) / 1000000000.0;
        double lDp = leftDistance - this.leftLastDistance;
        if ((lDp > 0.00001) || (lDp < -0.00001)) {
            this.leftVelocity = lDp / lDt;
            this.leftLastUpdateTime = time;
            this.leftLastDistance = leftDistance;
        }

        double rDt = (double)(time - this.rightLastUpdateTime) / 1000000000.0;
        double rDp = rightDistance - this.rightLastDistance;
        if ((lDp > 0.00001) || (lDp < -0.00001)) {
            this.rightVelocity = lDp / lDt;
            this.rightLastUpdateTime = time;
            this.rightLastDistance = rightDistance;
        }

        if (this.leftVelocity > this.maxDetectedVelocity) {
            this.maxDetectedVelocity = this.leftVelocity;
            SmartDashboard.putNumber("DriveController/MaxVelocity", this.maxDetectedVelocity);
        }
        SmartDashboard.putNumber("DriveController/LeftVelocity", this.leftLastVelocity);
        SmartDashboard.putNumber("DriveController/LeftDistance", this.leftLastDistance);
        SmartDashboard.putNumber("DriveController/RightVelocity", this.rightVelocity);
        SmartDashboard.putNumber("DriveController/RightDistance", this.rightLastDistance);

        // TODO write to log file
    }

    public void robotPeriodic() {
        this.writeToDashboard();
    }

    public void disablePeriodic() {

    }

    public void teleopPeriodic() {
        if (Controls.inClimberMode()) {
            this.setPTO(true);
        }
        else {
            this.setPTO(false);
        }

        if (isPTOEnabled()) {
            this.drive(-Controls.liftAxis.getValue(), -Controls.liftAxis.getValue());
        }
        else {

            if (Controls.debugDriveForward()) {
                if (this.follower == null) {
//                    this.driveDistance(Controls.getMoveDistance(), 150, 100, 300);
                    this.drivePath("motion-profile", false);
//                    this.driveRotate(90.0, 50, 100, 300);
                }
                else if (this.follower.isFinished()) {
//                    this.driveDistance(-Controls.getMoveDistance(), 150, 100, 300);
//                    this.drivePath("motion-profile", true);
//                    this.driveRotate(-90.0, 50, 100, 300);
                }
            }
            else {
                this.stopFollowing();

                if (Controls.DriveSystem.getDriveType() == DriveType.TankDrive) {
                    this.drive(Controls.DriveSystem.TankDrive.getLeftThrottleValue(), Controls.DriveSystem.TankDrive.getRightThrottleValue());
                } else { // DriveType.DiffDrive
                    double forwardPower = Controls.DriveSystem.DiffDrive.getThrottleValue();
                    double turnPower = Controls.DriveSystem.DiffDrive.getTurnValue() * Controls.DriveSystem.DiffDrive.turnDampener();
                    this.drive(forwardPower + turnPower, forwardPower - turnPower);
                }

                if (Controls.DriveSystem.isHighGear()) {
                    if (this.getGear() != Gear.High) {
                        this.setGear(Gear.High);
                    }
                } else {
                    if (this.getGear() != Gear.Low) {
                        this.setGear(Gear.Low);
                    }
                }
            }
        }
    }

    public void drivePath(String pathName, boolean reverse) {
        this.stopFollowing();

        final String pathDirectory = "/home/lvuser/";

        File leftFile = new File(pathDirectory+pathName+"-left.bin");
        File rightFile = new File(pathDirectory+pathName+"-right.bin");

        if (!leftFile.exists()) {
            System.out.println("Missing path file: " + leftFile.getName());
            return;
        }

        if (!rightFile.exists()) {
            System.out.println("Missing path file: " + rightFile.getName());
            return;
        }

        Trajectory leftTrajectory = Pathfinder.readFromFile(leftFile);
        Trajectory rightTrajectory = Pathfinder.readFromFile(rightFile);

        System.out.println("drivePath: " + pathName + " ETA: " + ((double)leftTrajectory.length() * leftTrajectory.get(0).dt) + " seconds.");

        this.follower = new TrajectoryFollower(pathName, reverse,
                Pathfinder.readFromFile(leftFile), leftEncoder, leftMotor, 1.0,
                Pathfinder.readFromFile(rightFile), rightEncoder, rightMotor, -1.0,
                1.0 / this.maxVelocity, 0.0, kProportionalFactor, kIntegralFactor, kDifferentialFactor);

        this.follower.start();
    }

    public TrajectoryFollower driveDistance(double distance, double maxVelocity, double maxAcceleration, double maxJerk) {
        this.stopFollowing();

        final Trajectory.Config config = new Trajectory.Config(
                Trajectory.FitMethod.HERMITE_CUBIC,
                Trajectory.Config.SAMPLES_FAST,
                kUpdateFrequency,
                maxVelocity,
                maxAcceleration,
                maxJerk);

        final Waypoint[] points = new Waypoint[]{
                new Waypoint(0, 0, Pathfinder.d2r(0)),
                new Waypoint(Math.abs(distance), 0, Pathfinder.d2r(0)),
        };

        Trajectory trajectory = Pathfinder.generate(points, config);
        TankModifier modifier = new TankModifier(trajectory).modify(kWheelbaseWidth);
        Trajectory[] trajectories = new Trajectory[]{modifier.getLeftTrajectory(), modifier.getRightTrajectory()};
        Encoder[] encoders = new Encoder[]{this.leftEncoder, this.rightEncoder};
        double[] distanceScales = new double[] {(distance < 0.0)?-1.0:1.0, (distance < 0.0)?-1.0:1.0};
        IMotorController[] motors = new IMotorController[]{this.leftMotor, this.rightMotor};
        double[] motorScales = new double[] {1.0, -1.0};

        System.out.println("driveDistance: " + distance + " ETA: " + ((double)trajectories[0].length() * kUpdateFrequency) + " seconds.");
        this.follower = new TrajectoryFollower(new String[] {"DriveDistance-left", "DriveDistance-right"}, trajectories, encoders, distanceScales, motors, motorScales,
                1.0 / this.maxVelocity, 0.0, kProportionalFactor, kIntegralFactor, kDifferentialFactor);
        this.follower.start();

        return follower;
    }

    public TrajectoryFollower driveRotate(double angle, double maxVelocity, double maxAcceleration, double maxJerk) {
        this.stopFollowing();

        final Trajectory.Config config = new Trajectory.Config(
                Trajectory.FitMethod.HERMITE_CUBIC,
                Trajectory.Config.SAMPLES_FAST,
                kUpdateFrequency,
                maxVelocity,
                maxAcceleration,
                maxJerk);

        double distance = 80.0 * (angle / 360.0);

        final Waypoint[] points = new Waypoint[]{
                new Waypoint(0, 0, Pathfinder.d2r(0)),
                new Waypoint(Math.abs(distance), 0, Pathfinder.d2r(0)),
        };

        Trajectory trajectory = Pathfinder.generate(points, config);
        TankModifier modifier = new TankModifier(trajectory).modify(kWheelbaseWidth);
        Trajectory[] trajectories = new Trajectory[]{modifier.getLeftTrajectory(), modifier.getRightTrajectory()};
        Encoder[] encoders = new Encoder[]{this.leftEncoder, this.rightEncoder};
        double[] distanceScales = new double[] {(distance < 0.0)?-1.0:1.0, (distance < 0.0)?1.0:-1.0};
        IMotorController[] motors = new IMotorController[]{this.leftMotor, this.rightMotor};
        double[] motorScales = new double[] {1.0, -1.0};

        System.out.println("driveRotate: " + angle + " ETA: " + ((double)trajectories[0].length() * kUpdateFrequency) + " seconds.");
        this.follower = new TrajectoryFollower(new String[] {"DriveRotate-left", "DriveRotate-right"}, trajectories, encoders, distanceScales, motors, motorScales,
                1.0 / this.maxVelocity, 0.0, kProportionalFactor, kIntegralFactor, kDifferentialFactor);

        this.follower.start();
        return follower;
    }

    public void stopFollowing() {
        if (this.follower != null) {
            this.follower.stop();
            this.drive(0.0, 0.0);
            this.follower = null;
        }
    }


    public void autonomousPeriodic() {

    }

    /**
     *
     * @param leftPower
     * @param rightPower
     */
    public void drive(double leftPower, double rightPower) {
        this.tankDrive.set(DriveMode.PercentOutput, leftPower, -rightPower);

        this.leftPower = leftPower;
        this.rightPower = rightPower;
    }

    /**
     *
     * @param enabled
     */
    public void setPTO(boolean enabled) {
        if (enabled) {
            this.pto.set(DoubleSolenoid.Value.kForward);
        }
        else {
            this.pto.set(DoubleSolenoid.Value.kReverse);
        }
    }

    /**
     *
     * @return
     */
    public boolean isPTOEnabled() {
        return this.pto.get() == DoubleSolenoid.Value.kForward;
    }

    public Gear getGear() {
        return this.gear;
    }

    public void setGear(Gear newGear) {
        if (newGear == Gear.High) {
            this.shifter.set(DoubleSolenoid.Value.kForward);
            this.leftEncoder.setDistancePerPulse(kHighGearDistancePerPulse);
            this.rightEncoder.setDistancePerPulse(kHighGearDistancePerPulse);
            this.maxVelocity = kHighGearMaxVelocity;
        }
        else {
            this.shifter.set(DoubleSolenoid.Value.kReverse);
            this.leftEncoder.setDistancePerPulse(kLowGearDistancePerPulse);
            this.rightEncoder.setDistancePerPulse(kLowGearDistancePerPulse);
            this.maxVelocity = kLowGearMaxVelocity;
        }

        this.gear = newGear;
    }

    public double getLeftPower() {
        return this.leftPower;
    }

    public double getRightPower() {
        return this.rightPower;
    }

    public double getLeftVelocity() {
        return this.leftVelocity;
    }

    public double getLeftDistance() {
        return this.leftEncoder.getDistance();
    }

    public double getRightDistance() {
        return this.rightEncoder.getDistance();
    }

    public double getRightVelocity() {
        return this.rightVelocity;
    }

    public void writeToDashboard() {
        SmartDashboard.putBoolean("DriveController/PTOEnabled", this.isPTOEnabled());
        SmartDashboard.putString("DriveController/Gear", this.gear == Gear.High ? "High" : "Low");
        SmartDashboard.putString("DriveController/DriveType", Controls.DriveSystem.getDriveType() == DriveType.TankDrive ? "TankDrive" : "DiffDrive");

        SmartDashboard.putNumber("DriveController/LeftPower", this.leftPower);
        SmartDashboard.putNumber("DriveController/RightPower", this.rightPower);
        SmartDashboard.putNumber("DriveController/LeftVelocity", this.leftVelocity);
        SmartDashboard.putNumber("DriveController/RightVelocity", this.rightVelocity);
    }
}
