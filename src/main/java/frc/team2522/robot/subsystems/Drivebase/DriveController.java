package frc.team2522.robot.subsystems.Drivebase;

import com.ctre.phoenix.drive.DriveMode;
import com.ctre.phoenix.mechanical.Gearbox;
import com.ctre.phoenix.motorcontrol.IMotorController;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.Controls;
import frc.team2522.robot.libs.*;

import java.util.TimerTask;

/**
 *
 */
public class DriveController {

    // TODO: calculate these values
    //
    public static final double kHighGearMaxVelocity = 175.0;    // inches / second
    public static final double kHighGearDistancePerPulse = 6.0 * Math.PI / 256.0;
    public static final double kLowGearMaxVelocity = 100.0;     // inches / second
    public static final double kLowGearDistancePerPulse = 6.0 * Math.PI / 256.0;

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
    private long lastUpdateTime = System.nanoTime();

    private double leftPower = 0.0;
    private double leftVelocity = 0.0;
    private double leftLastDistance = 0.0;
    private double leftLastVelocity = 0.0;

    private double rightPower = 0.0;
    private double rightVelocity = 0.0;
    private double rightLastDistance = 0.0;
    private double rightLastVelocity = 0.0;


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

        this.lastUpdateTime = System.nanoTime();
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
    }

    /**
     *
     */
    public void update() {
        long time = System.nanoTime();
        double leftDistance = this.leftEncoder.getDistance();
        double rightDistance = this.rightEncoder.getDistance();

        double dt = (double)(time - this.lastUpdateTime) / 1000000000.0;



        // TODO write to log file

        this.leftLastDistance = leftDistance;
        this.leftLastVelocity = leftVelocity;
        this.rightLastDistance = rightDistance;
        this.rightLastVelocity = rightVelocity;
        this.lastUpdateTime = time;
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
            if (Controls.DriveSystem.getDriveType() == DriveType.TankDrive) {
                this.drive(Controls.DriveSystem.TankDrive.getLeftThrottleValue(), Controls.DriveSystem.TankDrive.getRightThrottleValue());
            }
            else { // DriveType.DiffDrive
                double forwardPower = Controls.DriveSystem.DiffDrive.getThrottleValue();
                double turnPower = Controls.DriveSystem.DiffDrive.getTurnValue() * Controls.DriveSystem.DiffDrive.turnDampener();
                this.drive(forwardPower + turnPower, forwardPower - turnPower);
            }

            if (Controls.DriveSystem.isHighGear()) {
                if (this.getGear() != Gear.High) {
                    this.setGear(Gear.High);
                }
            }
            else {
                if (this.getGear() != Gear.Low) {
                    this.setGear(Gear.Low);
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
