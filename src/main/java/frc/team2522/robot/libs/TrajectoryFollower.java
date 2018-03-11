package frc.team2522.robot.libs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import jaci.pathfinder.Trajectory;
import edu.wpi.first.wpilibj.Encoder;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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
    private double invert;

    private double accelerationFeed;
    private double velocityFeed;

    private double distanceProportional;
    private double distanceIntegral;
    private double distanceDerivative;

    private double lastTime;
    private double[] lastPositions;
    private double[] lastPowers;
    private double[] lastErrors;

    private Trajectory[] trajectories = null;
    private Encoder[] encoders = null;
    private IMotorController[] controllers = null;

    PrintStream ps = null;

    public TrajectoryFollower(Trajectory trajectory, boolean reverse, Encoder encoder, IMotorController controller, double kVf, double kAf, double kP, double kI, double kD) {
        this(new Trajectory[] {trajectory}, reverse, new Encoder[] { encoder }, new IMotorController[] { controller }, kVf, kAf, kP, kI, kD);
    }

    public TrajectoryFollower(Trajectory[] trajectories, boolean reverse, Encoder[] encoders, IMotorController[] controllers, double kVf, double kAf, double kP, double kI, double kD) {
        this.trajectories = trajectories;
        this.encoders = encoders;
        this.controllers = controllers;

        this.trajectoryInterval = trajectories[0].get(1).dt;

        this.isRunning = false;
        this.isFinished = false;

        this.velocityFeed = kVf;
        this.accelerationFeed = kAf;

        this.distanceProportional = kP;
        this.distanceIntegral = kI;
        this.distanceDerivative = kD;

        this.invert = 1.0;
        if (reverse) {
            this.invert = -1.0;
        }
    }

    public void start() {
        this.startPositions = new double[trajectories.length];
        this.lastPositions = new double[trajectories.length];
        this.lastPowers = new double[trajectories.length];
        this.lastErrors = new double[trajectories.length];

        for(int i = 0; i < this.trajectories.length; i++) {
            this.startPositions[i] = this.encoders[i].getDistance();
            this.lastPositions[i] = 0.0;
            this.lastPowers[i] = 0.0;
            this.lastErrors[i] = 0.0;
        }

        File f = new File("/home/lvuser/TrajectoryFollower_0.csv");
        for(int i = 0;f.exists();i++)
        {
            f = new File("/home/lvuser/TrajectoryFollower_" + i + ".csv");
        }

        try
        {
            this.ps = new PrintStream(f);
            System.out.println("Created LogFile: " + f.getName());
            this.ps.println("Time,Index,Expected Velocity,Expected Distance,Actual Distance,Error,Power");
        }
        catch(IOException e)
        {
            this.ps = null;
            e.printStackTrace();
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
        for(int i = 0; i < controllers.length; i++) {
            controllers[i].set(ControlMode.PercentOutput, 0.0);
        }

        if (this.ps != null) {
            this.ps.close();
            this.ps = null;
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
        double time = this.getTime();

        if (! this.isFinished) {
            for(int i = 0; i < this.trajectories.length; i++) {
                double result = 0.0;
                double position = (this.encoders[i].getDistance() - this.startPositions[i]) * this.invert;

                int segmentIndex = (int) Math.round(time / this.trajectoryInterval);

                if (segmentIndex < this.trajectories[i].length()) {
                    Trajectory.Segment segment = this.trajectories[i].get(segmentIndex);
                    double error = segment.position - position;

                    result = this.distanceProportional * error +
                            this.distanceDerivative * ((error - this.lastErrors[i]) / (time - this.startTime)) +
                            this.velocityFeed * segment.velocity +
                            this.accelerationFeed * segment.acceleration;

                    result = result * this.invert;

                    if (this.ps != null) {
                        this.ps.println(time+","+segmentIndex+","+segment.velocity+","+segment.position+","+position+","+error+","+result);
                    }

                    this.lastTime = time;
                    this.lastPositions[i] = position;
                    this.lastPowers[i] = result;
                    this.lastErrors[i] = error;
                } else {
                    this.isFinished = true;
                }

                this.controllers[i].set(ControlMode.PercentOutput, result);
            }
        }
    }

    private double getTime() {
        return (double)(System.nanoTime() - this.startTime) / 1000000000.0;
    }
}
