package frc.team2522.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.Controls;
import frc.team2522.robot.libs.RoyalEncoder;
import frc.team2522.robot.libs.TrajectoryFollower;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

import java.util.Timer;
import java.util.TimerTask;

public class Lift {
    private Intake intake;
    private IMotorController liftMotor;
    private RoyalEncoder liftEncoder;
    private DoubleSolenoid liftBrake;
    private DoubleSolenoid liftRatchet;
    private DigitalInput liftSensor;

    private double liftPower = 0.0;


    //Calibration state
    boolean isCalibrated = false;
    Timer calibrationTimer = null;
    long calibrationStartTime;

    Timer moveToTimer = null;

    public Lift(Intake intake, IMotorController liftMotor, RoyalEncoder liftEncoder, DigitalInput liftSensor, DoubleSolenoid liftBrake, DoubleSolenoid liftRatchet) {
        this.intake = intake;
        this.liftMotor = liftMotor;
        this.liftEncoder = liftEncoder;
        this.liftSensor = liftSensor;
        this.liftBrake = liftBrake;
        this.liftRatchet = liftRatchet;

        final double inchesPerPulse = (3.5 * Math.PI) / 256.0;

        this.liftEncoder.setDistancePerPulse(inchesPerPulse);
        this.liftEncoder.setReverseDirection(false);

        this.reset();
    }

    public void reset() {
        this.setPower(0.0);
        this.setBreak(true);
    }

    public void autoCalibrate() {
        this.liftEncoder.reset(23.5);
        this.isCalibrated = true;
    }

    public void teleopPeriodic() {
        if(Controls.inClimberMode()) {
            stopFollower();
            stopCalibration();
            this.setBreak(false);
            this.setPower(0.0);
            this.setRatchet(true);
            this.intake.setClosed();
        }
        else {
            this.setRatchet(false);

            if (Controls.Elevator.Lift.getLiftAxisOn()) {
                stopFollower();
                stopCalibration();

                this.setBreak(false);
                this.setPower(Controls.Elevator.Lift.getLiftAxisValue());
            } else if (!isCalibrating() && !isFollowing()) {
                this.setBreak(true);
                this.setPower(0.0);
            }

            if (Controls.Elevator.Lift.startCalibration()) {
                if (!this.isCalibrating()) {
                    startCalibration();
                } else {
                    this.stopCalibration();
                }
            }

            if (Controls.Elevator.Lift.moveBottom()) {
                moveTo(0.0);
            } else if (Controls.Elevator.Lift.moveSwitch()) {
                moveTo(40.0);
            } else if (Controls.Elevator.Lift.moveScale()) {
                moveTo(81.0);
            } else if (Controls.Elevator.Lift.moveClimb()) {
                moveTo(66.0);
            } else if (!isCalibrating()) {
                stopFollower();
            }
        }

        this.writeToDashboard();
    }

    public double getPosition() {
        return this.liftEncoder.getDistance();
    }

    public void setPower(double power) {
        this.liftMotor.set(ControlMode.PercentOutput, power);
        this.liftPower = power;
    }

    public double getPower() {
        return this.liftPower;
    }

    public void setBreak(boolean enabled) {
        if (enabled) {
            this.liftBrake.set(DoubleSolenoid.Value.kForward);
        }
        else {
            this.liftBrake.set(DoubleSolenoid.Value.kReverse);
        }
    }

    public void setRatchet(boolean enabled) {
        if (enabled) {
            this.liftRatchet.set(DoubleSolenoid.Value.kForward);
        }
        else {
            this.liftRatchet.set(DoubleSolenoid.Value.kReverse);
        }
    }

    /**
     *
     */
    public void writeToDashboard() {
        SmartDashboard.putNumber("LiftPosition", this.getPosition());
    }


    TrajectoryFollower follower = null;

    public TrajectoryFollower moveTo(double moveTo) {
        // Instead of ignoring follow requests when we're already following we should cancel/slow down
        // and then start the requested follower.
        if(isCalibrated && !isCalibrating() && follower == null) {
            Waypoint[] points = new Waypoint[] {
                    new Waypoint(this.getPosition(), 0.0, Pathfinder.d2r(0)),
                    new Waypoint(moveTo, 0.0, Pathfinder.d2r(0))
            };

            Trajectory.Config config = new Trajectory.Config(
                    Trajectory.FitMethod.HERMITE_CUBIC,
                    Trajectory.Config.SAMPLES_FAST,
                    0.01, //10ms
                    100,
                    300.0,
                    500.0);

            Trajectory trajectory = Pathfinder.generate(points, config);
            System.out.println("Generated LiftMove path from " + this.getPosition() + " to " + moveTo + " ETA: " + (trajectory.length() * 0.01));

            follower = new TrajectoryFollower("LiftMove", this.getPosition() > moveTo, null, trajectory,this.liftEncoder, this.liftMotor, .04, 0.0, 0.8, 0.0, 0.0);

            this.setBreak(false);
            follower.start();

            this.moveToTimer = new Timer();
            this.moveToTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (follower.isFinished()) {
                        stopFollower();
                    }
                }
            }, 0, 10);
        }

        return this.follower;
    }

    private boolean isFollowing() {
        return follower != null;
    }

    public void startCalibration() {
        final long msCalibrateTick = 10;

        if(!isCalibrating()) {
            this.setBreak(false);
            this.setPower(0.25);

            calibrationStartTime = System.nanoTime();
            calibrationTimer = new Timer();
            calibrationTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    final double upTime = 0.3;

                    double dt = (double)(System.nanoTime() - calibrationStartTime) / 1000000000.0;

                    if (dt > upTime) {
                        intake.setOpen();
                        setPower(-0.10);
                    }

                    if(dt > 1.0) {
                        liftEncoder.reset();
                        isCalibrated = true;
                        System.out.println("Calibrate Time: " + dt + " Calibrate Current: " + liftMotor.getOutputCurrent());
                        stopCalibration();
                    }
                }
            }, msCalibrateTick, msCalibrateTick);
        }
    }

    /**
     *
     */
    public void stopCalibration() {
        if(calibrationTimer != null) {
            calibrationTimer.cancel();
            this.setBreak(true);
            this.setPower(0.0);
            calibrationTimer = null;
        }
    }

    /**
     *
     */
    public void stopFollower() {
        if (this.moveToTimer != null) {
            this.moveToTimer.cancel();
            this.moveToTimer = null;
        }

        if(follower != null) {
            follower.stop();
            this.setBreak(true);
            this.setPower(0.0);
            follower = null;
        }
    }

    public boolean isCalibrating() {
        return calibrationTimer != null;
    }
}