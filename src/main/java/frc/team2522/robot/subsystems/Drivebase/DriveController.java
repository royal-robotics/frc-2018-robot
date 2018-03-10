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

import java.util.TimerTask;

/**
 *
 */
public class DriveController {

    // TODO: calculate these values
    //
    public static final double kHighGearMaxVelocity = 161.0;    // inches / second
    public static final double kHighGearDistancePerPulse = 0.3429 * 6.0 * Math.PI / 256.0;
    public static final double kLowGearMaxVelocity = 100.0;     // inches / second
    public static final double kLowGearDistancePerPulse = 0.1667 * 6.0 * Math.PI / 256.0;

    public static final double kUpdateFrequency = 0.01;  // 100 times per second
    public static final double kProportionalFactor = 0.4;
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
    TankDrive tankDrive;

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
        this.setGear(Gear.High);
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
                    final Trajectory.Config config = new Trajectory.Config(
                            Trajectory.FitMethod.HERMITE_CUBIC,
                            Trajectory.Config.SAMPLES_HIGH,
                            0.01, //0.01=10ms
                            150,
                            300,
                            500);

                    final Waypoint[] points = new Waypoint[]{
                            new Waypoint(0, 0, Pathfinder.d2r(0)),
                            new Waypoint(Controls.getMoveDistance(), 0, Pathfinder.d2r(0)),
                    };

                    Trajectory trajectory = Pathfinder.generate(points, config);

                    final double wheelbase_width = 31.25;
                    TankModifier modifier = new TankModifier(trajectory).modify(wheelbase_width);
                    Trajectory[] trajectories = new Trajectory[]{modifier.getLeftTrajectory(), modifier.getRightTrajectory()};
                    Encoder[] encoders = new Encoder[]{this.leftEncoder, this.rightEncoder};
                    IMotorController[] motors = new IMotorController[]{this.leftMotor, this.rightMotor};

System.out.println("Starting Move Distance of " + Controls.getMoveDistance() + " ETA: " + ((double)trajectories[0].length() * 0.01));
                    this.follower = new TrajectoryFollower(trajectories, Controls.getMoveDistance() < 0.0, encoders, motors, 1.0 / 161.0, 0.0, 0.8, 0.0, 0.0);
                    this.follower.start();
                }
            }
            else {
                if (this.follower != null) {
                    this.follower.stop();
                    this.follower = null;
                }

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
