package frc.team2522.robot.libs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import jaci.pathfinder.Trajectory;
import edu.wpi.first.wpilibj.Encoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrajectoryFollower {

    private Timer timer;
    private long startTime;
    private double[] startPositions;
    private boolean isRunning;
    private boolean isFinished;

    private double trajectoryInterval;

    private double accelerationFeed;
    private double velocityFeed;

    private double distanceProportional;
    private double distanceIntegral;
    private double distanceDerivative;

    private double lastTime;
    private double[] lastPositions;
    private double[] lastPowers;
    private double[] lastErrors;

    private List<Trajectory> trajectories = new ArrayList<Trajectory>();
    private List<Encoder> encoders = new ArrayList<Encoder>();
    private List<IMotorController> controllers = new ArrayList<IMotorController>();

    public TrajectoryFollower(Trajectory trajectory, Encoder encoder, IMotorController controller, double kV, double kA, double kP, double kI, double kD) {
        this.trajectories.add(trajectory);
        this.encoders.add(encoder);
        this.controllers.add(controller);

        this.trajectoryInterval = trajectory.get(1).dt;

        this.isRunning = false;
        this.isFinished = false;

        this.velocityFeed = kV;
        this.accelerationFeed = kA;

        this.distanceProportional = kP;
        this.distanceIntegral = kI;
        this.distanceDerivative = kD;
    }

    public void start() {
        this.startPositions = new double[trajectories.size()];
        this.lastPositions = new double[trajectories.size()];
        this.lastPowers = new double[trajectories.size()];
        this.lastErrors = new double[trajectories.size()];

        for(int i = 0; i < this.trajectories.size(); i++) {
            this.startPositions[i] = this.encoders.get(i).getDistance();
            this.lastPositions[i] = 0.0;
            this.lastPowers[i] = 0.0;
            this.lastErrors[i] = 0.0;
        }

        this.isRunning = true;
        this.isFinished = false;

        this.startTime = System.nanoTime();
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                followPath();
            }
        }, 0, Math.round(this.trajectoryInterval * 1000.0));
    }

    public void stop() {
        this.timer.cancel();
        this.timer = null;
        for(int i = 0; i < controllers.size(); i++) {
            controllers.get(i).set(ControlMode.PercentOutput, 0.0);
        }
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    /**
     *  Calculate the motor value power to set based on the current time and position
     *
     * @return  Motor power that should be set.
     */
    public void followPath() {
        double result = 0.0;
        double time = this.getTime();

        if (! this.isFinished) {
            for(int i = 0; i < this.trajectories.size(); i++) {
                double position = this.encoders.get(i).getDistance() - this.startPositions[i];

                int segmentIndex = (int) Math.round(time / this.trajectoryInterval);

                if (segmentIndex < this.trajectories.get(i).length()) {
                    Trajectory.Segment segment = this.trajectories.get(i).get(segmentIndex);
                    double error = segment.position - position;

                    result = this.distanceProportional * error +
                            this.distanceDerivative * ((error - this.lastErrors[i]) / (time - this.startTime)) +
                            this.velocityFeed * segment.velocity +
                            this.accelerationFeed * segment.acceleration;

                    this.lastTime = time;
                    this.lastPositions[i] = position;
                    this.lastPowers[i] = result;
                    this.lastErrors[i] = error;
                } else {
                    this.isFinished = true;
                }

                this.controllers.get(i).set(ControlMode.PercentOutput, result);
            }
        }
    }

    private double getTime() {
        return (double)(System.nanoTime() - this.startTime) / 1000000000.0;
    }
}
