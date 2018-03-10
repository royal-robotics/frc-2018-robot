package frc.team2522.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.Controls;
import frc.team2522.robot.libs.TrajectoryFollower;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

import java.util.Timer;
import java.util.TimerTask;

public class Lift {
    private Intake intake;
    private IMotorController liftMotor;
    private Encoder liftEncoder;
    private DoubleSolenoid liftBrake;
    private DoubleSolenoid liftRatchet;
    private DigitalInput liftSensor;

    private double liftPower = 0.0;


    //Calibration state
    boolean isCalibrated = false;
    Timer calibrationTimer = null;
    long calibrationStartTime;

    public Lift(Intake intake, IMotorController liftMotor, Encoder liftEncoder, DigitalInput liftSensor, DoubleSolenoid liftBrake, DoubleSolenoid liftRatchet) {
        this.intake = intake;
        this.liftMotor = liftMotor;
        this.liftEncoder = liftEncoder;
        this.liftSensor = liftSensor;
        this.liftBrake = liftBrake;
        this.liftRatchet = liftRatchet;

        this.reset();
    }

    public void reset() {
        this.setPower(0.0);
        this.setBreak(true);
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

                double power = Controls.Elevator.Lift.getLiftAxisValue();
                power *= power > 0 ? 0.5 : 0.25;
                this.setBreak(false);
                this.setPower(power);
            } else if (!isCalibrating() && !isFollowing()) {
                this.setBreak(true);
                this.setPower(0.0);
            }

            if (Controls.Elevator.Lift.doCalibrate()) {
                if (!this.isCalibrating()) {
                    startCalibration();
                }
            } else {
                this.stopCalibration();
            }

            if (Controls.Elevator.Lift.moveBottom()) {
                this.setBreak(false);
                createFollower(0.0);
            } else if (Controls.Elevator.Lift.moveSwitch()) {
                this.setBreak(false);
                createFollower(20.0);
            } else if (Controls.Elevator.Lift.moveScale()) {
                this.setBreak(false);
                createFollower(75.0);
            } else if (Controls.Elevator.Lift.moveClimb()) {
                this.setBreak(false);
                createFollower(65.0);
            } else if (!isCalibrating()) {
                stopFollower();
            }

            if (isFollowing() && follower.isFinished()) {
                this.setBreak(true);
                this.setPower(0.0);
            }
        }

        this.writeToDashboard();
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
        SmartDashboard.putNumber("Lift/Position", this.getPosition());
    }


    TrajectoryFollower follower = null;

    private void createFollower(double moveTo) {
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
                    20,
                    50,
                    100.0);

            long nanoGenerateStart = System.nanoTime();
            Trajectory trajectory = Pathfinder.generate(points, config);
            System.out.println("Lift Gen Time: " + (double)(nanoGenerateStart - System.nanoTime()) / 1000000.0);

            follower = new TrajectoryFollower(trajectory, this.liftEncoder, this.liftMotor, .02, 0.0, 0.4, 0.0, 0.0);
            follower.start();
        }
    }

    private boolean isFollowing() {
        return follower != null;
    }

    public void startCalibration() {
        final long msCalibrateTick = 10;

        final double stallCurrent = 13.8;

        if(!isCalibrating()) {
            this.setBreak(false);
            this.setPower(25.0);

            calibrationStartTime = System.nanoTime();
            calibrationTimer = new Timer();
            calibrationTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    double dt = Math.round((double)(System.nanoTime() - calibrationStartTime) / 1000000000.0);

                    if (dt > 0.25) {
                        intake.setOpen();
                    }

                    if (dt > 0.5) {
                        setPower(-10.0);
                    }

                    if((dt > 1.0) && liftMotor.getOutputCurrent() > stallCurrent) {
                        liftEncoder.reset();
                        isCalibrated = true;
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

    public double getPosition() {
        return this.liftEncoder.getDistance();
    }
}