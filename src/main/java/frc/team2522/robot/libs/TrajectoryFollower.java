package frc.team2522.robot.libs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import jaci.pathfinder.Trajectory;
import edu.wpi.first.wpilibj.Encoder;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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

    private String[] names;
    private Trajectory[] trajectories = null;
    private Encoder[] encoders = null;
    private double[] distanceScales;
    private IMotorController[] controllers = null;
    private double[] powerScales = null;

    PrintStream ps[] = null;


    public TrajectoryFollower(String name, boolean reverse,
                              Trajectory trajectory, Encoder encoder, IMotorController controller,
                              double kVf, double kAf, double kP, double kI, double kD)
    {
        this(new String[] {name}, new Trajectory[] {trajectory}, new Encoder[] { encoder }, new double[] {reverse ? -1.0: 1.0}, new IMotorController[] { controller }, new double[] {1.0}, kVf, kAf, kP, kI, kD);
    }

    public TrajectoryFollower(String name, boolean reverse,
                              Trajectory leftTrajectory, Encoder leftEncoder, IMotorController leftMotor, double leftMotorScale,
                              Trajectory rightTrajectory, Encoder rightEncoder, IMotorController rightMotor, double rightMotorScale,
                              double kVf, double kAf, double kP, double kI, double kD)
    {
        this(new String[] {name+"-left", name+"-right"},
                new Trajectory[] {leftTrajectory, rightTrajectory},
                new Encoder[] { leftEncoder, rightEncoder }, new double[] {reverse ? -1.0: 1.0,reverse ? -1.0: 1.0},
                new IMotorController[] { leftMotor, rightMotor }, new double[] {1.0, -1.0},
                kVf, kAf, kP, kI, kD);
    }

    public TrajectoryFollower(String[] names, Trajectory[] trajectories, Encoder[] encoders, double[] distanceScales, IMotorController[] controllers, double[] powerScales, double kVf, double kAf, double kP, double kI, double kD) {
        this.names = names;
        this.trajectories = trajectories;
        this.encoders = encoders;
        this.distanceScales = distanceScales;
        this.controllers = controllers;
        this.powerScales = powerScales;

        this.trajectoryInterval = trajectories[0].get(1).dt;

        this.isRunning = false;
        this.isFinished = false;

        this.velocityFeed = kVf;
        this.accelerationFeed = kAf;

        this.distanceProportional = kP;
        this.distanceIntegral = kI;
        this.distanceDerivative = kD;
    }

    public void start() {
        this.startPositions = new double[trajectories.length];
        this.lastPositions = new double[trajectories.length];
        this.lastPowers = new double[trajectories.length];
        this.lastErrors = new double[trajectories.length];
        this.ps = new PrintStream[trajectories.length];

        for(int i = 0; i < this.trajectories.length; i++) {
            this.startPositions[i] = this.encoders[i].getDistance();
            this.lastPositions[i] = 0.0;
            this.lastPowers[i] = 0.0;
            this.lastErrors[i] = 0.0;

            File f = new File("/home/lvuser/" + this.names[i]+ "_0.csv");
            for(int j = 0;f.exists();j++)
            {
                f = new File("/home/lvuser/" + this.names[i] + "_" + j + ".csv");
            }

            try
            {
                this.ps[i] = new PrintStream(f);
                System.out.println("Created LogFile: " + f.getName());
                this.ps[i].println("Time,Index,Expected Velocity,Expected Distance,Actual Distance,Error,Power");
            }
            catch(IOException e)
            {
                this.ps[i] = null;
                e.printStackTrace();
            }
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

            if (this.ps[i] != null) {
                this.ps[i].close();
                this.ps[i] = null;
            }
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
                double position = (this.encoders[i].getDistance() - this.startPositions[i]) * this.distanceScales[i];

                int segmentIndex = (int) Math.round(time / this.trajectoryInterval);

                if (segmentIndex < this.trajectories[i].length()) {
                    Trajectory.Segment segment = this.trajectories[i].get(segmentIndex);
                    double error = segment.position - position;

                    result = this.distanceProportional * error +
                            this.distanceDerivative * ((error - this.lastErrors[i]) / (time - this.startTime)) +
                            this.velocityFeed * segment.velocity +
                            this.accelerationFeed * segment.acceleration;

                    result = result * this.distanceScales[i];

                    if (this.ps[i] != null) {
                        this.ps[i].println(time+","+segmentIndex+","+segment.velocity+","+segment.position+","+position+","+error+","+result);
                    }

                    this.lastTime = time;
                    this.lastPositions[i] = position;
                    this.lastPowers[i] = result;
                    this.lastErrors[i] = error;
                } else {
                    this.isFinished = true;
                }

                this.controllers[i].set(ControlMode.PercentOutput, result * this.powerScales[i]);
            }
        }
    }

    private double getTime() {
        return (double)(System.nanoTime() - this.startTime) / 1000000000.0;
    }
}
