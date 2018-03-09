package frc.team2522.robot.subsystems.Elevator.Lift;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.Controls;
import frc.team2522.robot.libs.TrajectoryFollower;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

import java.util.Timer;
import java.util.TimerTask;

public class Lift {

    TalonSRX liftMotors = new TalonSRX(2);
    VictorSPX lift2 = new VictorSPX(3);
    VictorSPX lift3 = new VictorSPX(4);
    TalonSRX carriage = null; //Shared with Lift;

    DoubleSolenoid brake = new DoubleSolenoid(0, 1, 6);

    LiftData data = new LiftData();

    Joystick driver;

    //Calibration state
    private DigitalInput hallEffect = new DigitalInput(0);
    private boolean lastHallEffect = hallEffect.get();
    boolean isCalibrated = false;
    Timer calibrationTimer = null;

    public Lift(Joystick driver, TalonSRX carriage) {
        this.driver = driver;
        this.carriage = carriage;

        lift2.follow(liftMotors);
        lift3.follow(liftMotors);
    }

    public void fmsUpdateTeleop() {
        fmsUpdateTeleopManualMode();
    }

    TrajectoryFollower follower = null;

    private void fmsUpdateTeleopManualMode() {
        //TODO: save climber mode state inside of controls
        if(Controls.activateClimbPressed) {
            stopFollower();
            stopCalibration();
            brake.set(DoubleSolenoid.Value.kReverse);
            liftMotors.set(ControlMode.PercentOutput, 0.0);
            return;
        }

        if(Controls.Elevator.Lift.liftAxis.isPressed()) {
            stopFollower();
            stopCalibration();

            double power = -Controls.Elevator.Lift.liftAxis.getValue();
            power *= power > 0 ? 0.5 : 0.25;

            brake.set(DoubleSolenoid.Value.kReverse);
            liftMotors.set(ControlMode.PercentOutput, power);
        } else if (!isCalibrating() && !isFollowing()) {
            brake.set(DoubleSolenoid.Value.kForward);
            liftMotors.set(ControlMode.PercentOutput, 0.0);
        }

        if(Controls.Elevator.Lift.calibrate.isPressed()) {
            calibrate();
        }

        if(Controls.Elevator.Lift.moveBottom.isPressed()) {
            brake.set(DoubleSolenoid.Value.kReverse);
            createFollower(0);
        } else if(Controls.Elevator.Lift.moveSwitch.isPressed()) {
            brake.set(DoubleSolenoid.Value.kReverse);
            createFollower(20);
        } else if(Controls.Elevator.Lift.moveScale.isPressed()) {
            brake.set(DoubleSolenoid.Value.kReverse);
            createFollower(60);
        } else if(!isCalibrating()) {
            stopFollower();
        }

        if(isFollowing() && follower.isFinished()) {
            brake.set(DoubleSolenoid.Value.kForward);
            liftMotors.set(ControlMode.PercentOutput, 0.0);
        }
    }

    private void createFollower(double moveTo) {
        // Instead of ignoring follow requests when we're already following we should cancel/slow down
        // and then start the requested follower.
        if(isCalibrated && !isCalibrating() && follower == null) {
            Waypoint[] points = new Waypoint[] {
                    new Waypoint(data.getPosition(), 0.0, Pathfinder.d2r(0)),
                    new Waypoint(moveTo, 0.0, Pathfinder.d2r(0))
//                    new Waypoint(data.getPosition() + 20, 0.0, Pathfinder.d2r(0))
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

            //Position seems to always go from 0 to end point... so we might have to invert it.
//            boolean invertPosition = data.getPosition() > moveTo;
//            for (int i = 0; i < trajectory.segments.length; i++) {
//                if(invertPosition)
//                    trajectory.segments[i].position = -trajectory.segments[i].position;
//            }

            System.out.println("Gen Time: " + (double)(nanoGenerateStart - System.nanoTime()) / 1000000.0);
            follower = new TrajectoryFollower(trajectory, data.encoder, liftMotors, .016, 0.0, 0.2, 0.0, 0.0);
            follower.start();
        }
    }

    private boolean isFollowing() {
        return follower != null;
    }

    public void calibrate() {
        final long msCalibrateTick = 10;
        final double hallEffectOffset = 5.5;
        final double floorOffset = 0.0;

        final double stallCurrent = 13.8;

        if(!isCalibrating()) {
            brake.set(DoubleSolenoid.Value.kReverse);
            liftMotors.set(ControlMode.PercentOutput, -0.10);

            calibrationTimer = new Timer();
            calibrationTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    boolean hallEffectReading = hallEffect.get();
                    boolean isHallEffectEdge = !hallEffectReading && lastHallEffect;
                    lastHallEffect = hallEffectReading;
                    if(isHallEffectEdge) {
                        data.reset(hallEffectOffset);
                        isCalibrated = true;
                        System.out.println("Hall effect reset");
                        stopCalibration();
                    } else if(liftMotors.getOutputCurrent() > stallCurrent) {
                        data.reset(floorOffset);
                        isCalibrated = true;
                        System.out.println("stall reset");
                        stopCalibration();
                    }
                }
            }, msCalibrateTick, msCalibrateTick);
        }
    }

    public void stopCalibration() {
        if(calibrationTimer != null) {
            calibrationTimer.cancel();
            brake.set(DoubleSolenoid.Value.kForward);
            liftMotors.set(ControlMode.PercentOutput, 0.0);
            calibrationTimer = null;
        }
    }

    public void stopFollower() {
        if(follower != null) {
            follower.stop();
            brake.set(DoubleSolenoid.Value.kForward);
            liftMotors.set(ControlMode.PercentOutput, 0.0);
            follower = null;
        }
    }

    public boolean isCalibrating() {
        return calibrationTimer != null;
    }

    public double getPosition() {
        return data.getPosition();
    }
}