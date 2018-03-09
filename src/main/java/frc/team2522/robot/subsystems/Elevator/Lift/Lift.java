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

import javax.naming.ldap.Control;
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

    boolean climbOn = false;
    private void fmsUpdateTeleopManualMode() {
        if (Controls.activateClimbPressed) {
            climbOn = true;
        } else {
            climbOn = false;
        }

        if (climbOn) {
            brake.set(DoubleSolenoid.Value.kReverse);
        } else {
//        System.out.println("encoder:" + data.encoder.getDistance());
            if(Controls.liftAxisPressed) {
                brake.set(DoubleSolenoid.Value.kReverse);
                double power = -Controls.liftAxisValue;
                if(power > 0)
                    power *= 0.5;
                else
                    power *= 0.25;

                liftMotors.set(ControlMode.PercentOutput, power);
            } else {
                if(isCalibrating()) {
                    System.out.println("Calibrating: " + liftMotors.getOutputCurrent());
                } else if(follower != null) {
                    System.out.println("Following: ");
                } else {
                    brake.set(DoubleSolenoid.Value.kForward);
                    liftMotors.set(ControlMode.PercentOutput, 0.0);
                }
            }

            if(Controls.calibratePressed) {
                calibrate();
            }

            if(Controls.moveliftPressed) {
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

            if (!isCalibrating() && follower != null) {
                if (follower.isFinished()) {
                    brake.set(DoubleSolenoid.Value.kForward);
                    liftMotors.set(ControlMode.PercentOutput, 0.0);
                } else {
                    brake.set(DoubleSolenoid.Value.kReverse);
                }
            }
        }

        SmartDashboard.putBoolean("Climb/LiftThinksClimbOn", climbOn);
    }

    private void fmsUpdateTeleopEncoderMode() {

    }

    public void calibrate() {
        final long msCalibrateTick = 10;
        final double hallEffectOffset = 5.5;
        final double floorOffset = 0.0;

        final double stallCurrent = 13.8;

        if(!isCalibrating()) {
            brake.set(DoubleSolenoid.Value.kReverse);
            liftMotors.set(ControlMode.PercentOutput, -0.20);

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