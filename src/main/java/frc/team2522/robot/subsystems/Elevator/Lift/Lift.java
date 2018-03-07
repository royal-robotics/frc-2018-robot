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

    //If we fail to calibrate, or detect a problem switch to manual mode
    boolean isManualMode = true;

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
        if(isManualMode)
            fmsUpdateTeleopManualMode();
        else
            fmsUpdateTeleopEncoderMode();
    }

    TrajectoryFollower follower = null;

    private void fmsUpdateTeleopManualMode() {
        if(Controls.Elevator.Lift.liftUp.isPressed()) {
            brake.set(DoubleSolenoid.Value.kReverse);
            SmartDashboard.putNumber("Lift/Motors1/current", liftMotors.getOutputCurrent());
            liftMotors.set(ControlMode.PercentOutput, 0.5);
        } else if(Controls.Elevator.Lift.liftdown.isPressed()) {
            brake.set(DoubleSolenoid.Value.kReverse);
            liftMotors.set(ControlMode.PercentOutput, -0.25);
            SmartDashboard.putNumber("Lift/Motors1/current", liftMotors.getOutputCurrent());
        } else {
            if(!isCalibrating()) {
                brake.set(DoubleSolenoid.Value.kForward);
                liftMotors.set(ControlMode.PercentOutput, 0.0);
            } else {
                System.out.println("Calibrating: " + liftMotors.getOutputCurrent());
            }
        }

        if(Controls.Elevator.Lift.calibrate.isPressed()) {
            calibrate();
        }

        if(Controls.Elevator.Lift.moveLift.isPressed()) {
            if(follower == null) {
                Waypoint[] points = new Waypoint[] {
                    new Waypoint(data.getPosition(), 0.0, Pathfinder.d2r(0)),
                    new Waypoint(data.getPosition() + 20, 0.0, Pathfinder.d2r(0))
                };
                Trajectory.Config config = new Trajectory.Config(
                        Trajectory.FitMethod.HERMITE_CUBIC,
                        Trajectory.Config.SAMPLES_FAST,
                        0.01, //10ms
                        20,
                        50,
                        100.0);

                Trajectory trajectory = Pathfinder.generate(points, config);
                follower = new TrajectoryFollower(trajectory, data.encoder, liftMotors, .016, 0.0, 0.2, 0.0, 0.0);
                follower.start();
            }
        } else {
            if(follower != null) {
                follower.stop();
                follower = null;
            }
        }
    }

    private void fmsUpdateTeleopEncoderMode() {

    }


    public void calibrate() {
        final long msCalibrateTick = 10;
        final double hallEffectOffset = 5.95;
        final double floorOffset = 0.0;

        final double stallCurrent = 13.8;

        if(!isCalibrating()) {
            brake.set(DoubleSolenoid.Value.kReverse);
            liftMotors.set(ControlMode.PercentOutput, -0.25);

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
        calibrationTimer.cancel();
        calibrationTimer = null;

        System.out.println("stop calibration");

        brake.set(DoubleSolenoid.Value.kForward);
        liftMotors.set(ControlMode.PercentOutput, 0.0);
    }

    public boolean isCalibrating() {
        return calibrationTimer != null;
    }
}