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

public class TrajectoryFollower implements ITrajectoryFollower {

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

    private ADXRS450_Gyro gyro;
    private double gyroAngleOffset;

    PrintStream ps[] = null;


    public TrajectoryFollower(String name, boolean reverse, ADXRS450_Gyro gyro,
                              Trajectory trajectory, Encoder encoder, IMotorController controller,
                              double kVf, double kAf, double kP, double kI, double kD)
    {
        this(new String[] {name},
                gyro, new Trajectory[] {trajectory},
                new Encoder[] { encoder }, new double[] {reverse ? -1.0: 1.0},
                new IMotorController[] { controller }, new double[] {reverse ? -1.0: 1.0},
                kVf, kAf, kP, kI, kD);
    }

    public TrajectoryFollower(String name, boolean reverse, ADXRS450_Gyro gyro,
                              Trajectory leftTrajectory, Encoder leftEncoder, IMotorController leftMotor,
                              Trajectory rightTrajectory, Encoder rightEncoder, IMotorController rightMotor,
                              double kVf, double kAf, double kP, double kI, double kD)
    {
        this(new String[] {name+"-left", name+"-right"}, gyro,
                new Trajectory[] {leftTrajectory, rightTrajectory},
                reverse ? new Encoder[] { rightEncoder, leftEncoder } : new Encoder[] { leftEncoder, rightEncoder },
                new double[] {reverse ? -1.0: 1.0,reverse ? -1.0: 1.0},
                reverse ? new IMotorController[] { rightMotor, leftMotor } :  new IMotorController[] { leftMotor, rightMotor },
                new double[] {reverse ? -1.0: 1.0,reverse ? -1.0: 1.0},
                kVf, kAf, kP, kI, kD);
    }

    public TrajectoryFollower(String[] names,  ADXRS450_Gyro gyro, Trajectory[] trajectories, Encoder[] encoders, double[] distanceScales, IMotorController[] controllers, double[] powerScales, double kVf, double kAf, double kP, double kI, double kD) {
        this.names = names;
        this.gyro = gyro;
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

    /**
     *
     */
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
                this.ps[i].println("Time,Index,ExpectedVelocity,ExpectedPosition,ActualPosition,PositionAdj,ExpectedAngle,ActualAngle,AngleError,AngleErrorAdj,AnglePosAdj,TotalPosAdj,PowerAdj,Power");
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

    /**
     *
     */
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

            this.isRunning = false;
            this.isFinished = true;
        }
    }

    /**
     *
     * @return
     */
    public boolean isRunning() { return this.isRunning; }

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
            result = result + (this.lastPositions[i] * this.distanceScales[i]);
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
     */
    public void followPath() {
        final double kWheelBaseWidth = 25.0;    // TODO: this value should be passed in to the follower.
        final double kInchesPerDegree = (kWheelBaseWidth * Math.PI) / 360.0;

        double time = this.getTime();

        if (! this.isFinished)
        {
            int segmentIndex = (int) Math.round(time / this.trajectoryInterval);

            if (segmentIndex < this.trajectories[0].length())
            {
                // Calculate angle error adjustment if we have a gyro.
                //
                double expectedAngle = 0.0;;
                double actualAngle = 0.0;
                double angleError = 0.0;
                double angleErrorAdj = 0.0;

                if ((this.gyro != null) && (this.trajectories.length == 2)) {
                    Trajectory.Segment leftSeg = this.trajectories[0].get(segmentIndex);
                    Trajectory.Segment rightSeg = this.trajectories[1].get(segmentIndex);

                    expectedAngle = Pathfinder.r2d(leftSeg.heading);
                    actualAngle = this.getAngle();
                    angleError = Pathfinder.boundHalfDegrees(expectedAngle - actualAngle);

                    // Calculate the angle induced by the left and right wheel position errors so it can be subtracted from the gyro observed error.
                    //
                    double leftPosError = leftSeg.position - ((this.encoders[0].getDistance() - this.startPositions[0]) * this.distanceScales[0]);
                    double rightPosError = rightSeg.position - ((this.encoders[1].getDistance() - this.startPositions[1]) * this.distanceScales[1]);
                    angleErrorAdj = Pathfinder.r2d(Math.asin((leftPosError - rightPosError) / kWheelBaseWidth));
                }

                //  Assume 0 is left and 1 is right
                for(int i = 0; i < this.trajectories.length; i++) {
                    double power = 0.0;
                    double angleAdj = 0.0;
                    double position = (this.encoders[i].getDistance() - this.startPositions[i]) * this.distanceScales[i];

                    Trajectory.Segment segment = this.trajectories[i].get(segmentIndex);

                    // Calculate position error adjustment plus the on going angle adjustment
                    //
                    double positionError = (segment.position - position);

                    // Calculate angle error adjustment if we have a gyro.
                    //
                    if ((this.gyro != null) && (this.trajectories.length == 2)) {
                        angleAdj = (angleError - angleErrorAdj) * kInchesPerDegree;
                        if (i == 0) {   // left wheel
                            angleAdj =  -angleAdj;
                        }
                        else {          // right wheel
                            angleAdj = angleAdj;
                        }

                        positionError += angleAdj * 0.75;
                    }

                    double powerAdj = this.distanceProportional * positionError +
                                      this.distanceDerivative * ((positionError - this.lastErrors[i]) / (time - this.startTime));

                    power = this.velocityFeed * segment.velocity +
                            this.accelerationFeed * segment.acceleration +
                            powerAdj;

                    if (this.ps[i] != null) {
                        this.ps[i].println(time+","+segmentIndex+","+segment.velocity+","+segment.position+","+position+"," + (positionError - angleAdj) + "," + expectedAngle+","+actualAngle+","+angleError+","+angleErrorAdj+","+angleAdj+","+positionError+","+powerAdj+","+power);
                    }

                    this.lastTime = time;
                    this.lastPositions[i] = position;
                    this.lastPowers[i] = power;
                    this.lastErrors[i] = positionError;

                    this.controllers[i].set(ControlMode.PercentOutput, power * this.powerScales[i]);
                }
            }
            else {
                this.isFinished = true;
            }
        }
        else {
            for(int i = 0; i < this.controllers.length; i++) {
                this.controllers[i].set(ControlMode.PercentOutput, 0.0);
            }
        }
    }
}
