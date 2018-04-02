package frc.team2522.robot.libs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import jaci.pathfinder.Pathfinder;
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

    private double angleErrorScale = 1.0;

    private ADXRS450_Gyro gyro;
    private double gyroAngleOffset;

    PrintStream ps[] = null;


    public TrajectoryFollower(String name, boolean reverse, ADXRS450_Gyro gyro,
                              Trajectory trajectory, Encoder encoder, IMotorController controller,
                              double kVf, double kAf, double kP, double kI, double kD)
    {
        this(new String[] {name}, gyro, 1.0, new Trajectory[] {trajectory}, new Encoder[] { encoder }, new double[] {reverse ? -1.0: 1.0}, new IMotorController[] { controller }, new double[] {1.0}, kVf, kAf, kP, kI, kD);
    }

    public TrajectoryFollower(String name, boolean reverse, ADXRS450_Gyro gyro,
                              Trajectory leftTrajectory, Encoder leftEncoder, IMotorController leftMotor,
                              Trajectory rightTrajectory, Encoder rightEncoder, IMotorController rightMotor,
                              double kVf, double kAf, double kP, double kI, double kD)
    {
        this(new String[] {name+"-left", name+"-right"}, gyro, 1.0,
                new Trajectory[] {leftTrajectory, rightTrajectory},
                reverse ? new Encoder[] { rightEncoder, leftEncoder } : new Encoder[] { leftEncoder, rightEncoder },
                new double[] {reverse ? -1.0: 1.0,reverse ? -1.0: 1.0},
                reverse ? new IMotorController[] { rightMotor, leftMotor } :  new IMotorController[] { leftMotor, rightMotor },
                reverse ? new double[] {-1.0, 1.0} : new double[] {1.0, -1.0},
                kVf, kAf, kP, kI, kD);
    }

    public TrajectoryFollower(String[] names,  ADXRS450_Gyro gyro, double angleErrorScale, Trajectory[] trajectories, Encoder[] encoders, double[] distanceScales, IMotorController[] controllers, double[] powerScales, double kVf, double kAf, double kP, double kI, double kD) {
        this.names = names;
        this.gyro = gyro;
        this.angleErrorScale = angleErrorScale;
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

        if (this.gyro != null) {
            this.gyroAngleOffset = Pathfinder.r2d(trajectories[0].get(0).heading) + this.gyro.getAngle();
        }

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
                this.ps[i].println("Time,Index,Expected Velocity,Expected Distance,Actual Distance,Distance Error,Expected Angle, Actual Angle,Angle Error,Power,DistanceAdj,AngleAdj");
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
        synchronized (this) {
            if (this.timer != null)
                this.timer.cancel();

            this.timer = null;
            for (int i = 0; i < controllers.length; i++) {
                controllers[i].set(ControlMode.PercentOutput, 0.0);

                if (this.ps[i] != null) {
                    this.ps[i].close();
                    this.ps[i] = null;
                }
            }

            this.isFinished = true;
        }
    }

    /**
     * Returns true after the follower has completed.
     *
     * @return boolean has the follower finished.
     */
    public boolean isFinished() {
        return this.isFinished;
    }

    /**
     * Returns the time in seconds since follower was started.
     *
     * @return double elapsed time since start of follower.
     */
    public double getTime() {
        return (double)(System.nanoTime() - this.startTime) / 1000000000.0;
    }

    /**
     * Get angle in radians that robot is currently oriented.
     * @return
     */
    public double getAngle() {
        if (this.gyro == null) {
            return 0.0;
        }

        return -this.gyro.getAngle() + this.gyroAngleOffset;
    }

    /**
     * Returns the current position in terms of distance along the first trajectory that the follower has traveled.
     *
     * @return
     */
    public double getPosition() {
        double result = 0.0;

        for(int i = 0; i < this.lastPositions.length; i++) {
            result = result + this.lastPositions[i];
        }

        return result / ((double)this.lastPositions.length);
    }

    /**
     * Returns the length in distance traveled of the first trajectory in the list.
     *
     * @return
     */
    public double getLength() {
        return this.trajectories[0].get(this.trajectories.length - 1).position;
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
                double power = 0.0;
                double position = (this.encoders[i].getDistance() - this.startPositions[i]) * this.distanceScales[i];

                int segmentIndex = (int) Math.round(time / this.trajectoryInterval);

                if (segmentIndex < this.trajectories[i].length()) {
                    Trajectory.Segment segment = this.trajectories[i].get(segmentIndex);

                    // Calculate distance error adjustment
                    //
                    double distanceError = segment.position - position;
                    double distanceAdj =
                            this.distanceProportional * distanceError +
                            this.distanceDerivative * ((distanceError - this.lastErrors[i]) / (time - this.startTime));

                    // Calculate angle error adjustment if we have a gyro.
                    //
                    // Assume motors[0] is left and motors[1] is right
                    //
                    double expectedAngle = Pathfinder.r2d(segment.heading);;
                    double actualAngle = 0.0;
                    double angleError = 0.0;
                    double angleAdj = 0.0;
                    if (this.gyro != null) {
                        actualAngle = this.getAngle();
                        expectedAngle = Pathfinder.r2d(segment.heading);
                        angleError = Pathfinder.boundHalfDegrees(expectedAngle - actualAngle);

                        angleAdj = -0.25 * this.angleErrorScale * angleError;

                        if (i == 1) {   // Right Motor
                            angleAdj = -1.0 * angleAdj;
                        }
                    }

                    power = this.velocityFeed * segment.velocity +
                            this.accelerationFeed * segment.acceleration +
                            distanceAdj + angleAdj;

                    power = power * this.distanceScales[i];

                    if (this.ps[i] != null) {
                        this.ps[i].println(time+","+segmentIndex+","+segment.velocity+","+segment.position+","+position+","+distanceError+"," + expectedAngle+","+actualAngle+","+angleError+","+power+","+distanceAdj+","+angleAdj);
                    }

                    this.lastTime = time;
                    this.lastPositions[i] = position;
                    this.lastPowers[i] = power;
                    this.lastErrors[i] = distanceError;
                } else {
                    this.isFinished = true;
                }

                this.controllers[i].set(ControlMode.PercentOutput, power * this.powerScales[i]);
            }
        }
    }
}
