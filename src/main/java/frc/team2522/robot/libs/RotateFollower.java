package frc.team2522.robot.libs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Encoder;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

public class RotateFollower implements ITrajectoryFollower
{
    private double angle;
    private double inchesPerDegree;

    private String[] names;
    Trajectory[] trajectories;
    private Encoder[] encoders = null;
    private IMotorController[] controllers = null;

    private ADXRS450_Gyro gyro;
    private double gyroAngleOffset;
    private double[] startPositions;
    private double[] lastErrors;

    private Timer timer;
    private long startTime;
    private double trajectoryInterval;
    private boolean isRunning;
    private boolean isFinished;

    private double accelerationFeed;
    private double velocityFeed;

    private double distanceProportional;
    private double distanceIntegral;
    private double distanceDerivative;

    PrintStream ps[] = null;

    public RotateFollower(String name, double angle, double wheelbaseWidth, double sampleInterval,
                          ADXRS450_Gyro gyro,
                          Encoder leftEncoder, IMotorController leftMotors,
                          Encoder rightEncoder, IMotorController rightMotors,
                          double maxVelocity, double maxAcceleration, double maxJerk,
                          double kVf, double kAf, double kP, double kI, double kD) {

        this.angle = angle;
        this.gyro = gyro;

        this.inchesPerDegree = (wheelbaseWidth * Math.PI) / 360.0;

        this.names = new String[] {name+"-left", name+"-right"};
        this.trajectories = this.getRotationTrajectories(angle, wheelbaseWidth, sampleInterval, maxVelocity, maxAcceleration, maxJerk);
        this.encoders = new Encoder[] {leftEncoder, rightEncoder};
        this.controllers = new IMotorController[] {leftMotors, rightMotors};

        this.trajectoryInterval = this.trajectories[0].get(1).dt;

        this.velocityFeed = kVf;
        this.accelerationFeed = kAf;

        this.distanceProportional = kP;
        this.distanceIntegral = kI;
        this.distanceDerivative = kD;
    }

    /**
     * Calculate and return an array of trajectories for tank drive rotation.
     * [0] is left gearbox and [1] is right gearbox.
     *
     * @param angle
     * @param wheelbaseWidth
     * @param sampleInterval
     * @param maxVelocity
     * @param maxAcceleration
     * @param maxJerk
     *
     * @return The left[0] and right[1] trajectory paths for the specified rotation.
     */
    public static Trajectory[] getRotationTrajectories(double angle, double wheelbaseWidth, double sampleInterval,
                                                       double maxVelocity, double maxAcceleration, double maxJerk)
    {
        long startGeneration = System.nanoTime();
        final double kInchesPerDegree = (wheelbaseWidth * Math.PI) / 360.0;

        final Trajectory.Config config = new Trajectory.Config(
                Trajectory.FitMethod.HERMITE_CUBIC,
                Trajectory.Config.SAMPLES_FAST,
                sampleInterval,
                maxVelocity,
                maxAcceleration,
                maxJerk);

        final Waypoint[] points = new Waypoint[]{
                new Waypoint(0, 0, Pathfinder.d2r(0)),
                new Waypoint(Math.abs(kInchesPerDegree * angle), 0, Pathfinder.d2r(0)),
        };

        Trajectory trajectory = Pathfinder.generate(points, config);
        TankModifier modifier = new TankModifier(trajectory).modify(wheelbaseWidth);
        Trajectory leftTrajectory = modifier.getLeftTrajectory();
        Trajectory rightTrajectory = modifier.getRightTrajectory();

        for(int i = 0; i < leftTrajectory.length(); i++) {
            Trajectory.Segment left = leftTrajectory.get(i);
            Trajectory.Segment right = rightTrajectory.get(i);

            if (angle > 0.0) {
                right.position = -right.position;
                right.velocity = -right.velocity;
                right.acceleration = -right.acceleration;
                right.jerk = -right.jerk;
            }
            else {
                left.position = -left.position;
                left.velocity = -left.velocity;
                left.acceleration = -left.acceleration;
                left.jerk = -left.jerk;
            }

            double heading = Pathfinder.d2r(right.position / kInchesPerDegree);

            left.heading = heading;
            left.x = left.y = 0.0;

            right.heading = heading;
            right.x = right.y = 0.0;
        }

        System.out.println("getRotationTrajectories() Path Generation Time: " + ((double)(System.nanoTime() - startGeneration) / 1000000000.0) + " seconds.");
        return new Trajectory[]{leftTrajectory, rightTrajectory};
    }

    public void start()
    {
        this.startPositions = new double[trajectories.length];
        this.lastErrors = new double[trajectories.length];
        this.ps = new PrintStream[trajectories.length];

        if (this.gyro != null) {
            this.gyroAngleOffset = Pathfinder.r2d(trajectories[0].get(0).heading) + this.gyro.getAngle();
        }

        for(int i = 0; i < this.trajectories.length; i++) {
            this.startPositions[i] = this.encoders[i].getDistance();
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
                this.ps[i].println("Time,Index,ExpectedVelocity,ExpectedPosition,ActualPosition,PositionError,ExpectedAngle,ActualAngle,AngleError,AnglePosAdj,TotalPosAdj,PowerAdj,Power");
            }
            catch(IOException e)
            {
                this.ps[i] = null;
                e.printStackTrace();
            }
        }

        if (this.gyro != null) {
            this.gyroAngleOffset = Pathfinder.r2d(trajectories[0].get(0).heading) + this.gyro.getAngle();
        }

        this.isRunning = true;
        this.isFinished = false;

        System.out.println("RotateFollower starting: " + this.angle + " deg, ETA: " + ((double)trajectories[0].length() * trajectories[0].get(0).dt) + " seconds.");

        this.startTime = System.nanoTime();
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                followPath();
            }
        }, 0, Math.round(this.trajectoryInterval * 1000.0));
    }

    public void stop()
    {
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

    public boolean isRunning()
    {
        return this.isRunning;
    }

    public boolean isFinished()
    {
        return this.isFinished;
    }

    /**
     * Get the current position of this follower.
     *
     * @return  The angle that the robot has traveled through (clockwise positive).
     */
    public double getPosition()
    {
        return -this.getAngle();
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
     * Get angle in degrees that robot is currently oriented.  This value is returned counter clockwise positive like a unit circle.
     *
     * @return
     */
    public double getAngle() {
        return -this.gyro.getAngle() + this.gyroAngleOffset;
    }

    /**
     *
     */
    public void followPath()
    {
        if (! this.isFinished) {
            double time = this.getTime();

            //  Left is index 0 and right is index 1
            //
            for(int i = 0; i < this.trajectories.length; i++) {
                double power = 0.0;
                double position = this.encoders[i].getDistance() - this.startPositions[i];

                int segmentIndex = (int) Math.round(time / this.trajectoryInterval);

                if (segmentIndex < this.trajectories[i].length()) {
                    Trajectory.Segment segment = this.trajectories[i].get(segmentIndex);

                    // Calculate angle error adjustment, because of wheel slippage during rotations encoder position erros are not usable.
                    //
                    double expectedAngle = Pathfinder.r2d(segment.heading);;
                    double actualAngle = 0.0;
                    double angleError = 0.0;

                    actualAngle = this.getAngle();
                    expectedAngle = Pathfinder.r2d(segment.heading);
                    angleError = Pathfinder.boundHalfDegrees(expectedAngle - actualAngle);

                    double angleAdj = angleError * this.inchesPerDegree;
                    if (i == 0) {   // left wheel
                        angleAdj =  -angleAdj;
                    }
                    else {          // right wheel
                        angleAdj = angleAdj;
                    }

                    double positionError = angleAdj;

                    double powerAdj = this.distanceProportional * positionError +
                                      this.distanceDerivative * ((positionError - this.lastErrors[i]) / (time - this.startTime));

                    power = this.velocityFeed * segment.velocity +
                            this.accelerationFeed * segment.acceleration +
                            powerAdj;

                    if (this.ps[i] != null) {
                        this.ps[i].println(time+","+segmentIndex+","+segment.velocity+","+segment.position+","+position+"," + (segment.position - position) + "," + expectedAngle+","+actualAngle+","+angleError+","+angleAdj+","+angleAdj+","+powerAdj+","+power);
                    }

                    this.lastErrors[i] = positionError;
                } else {
                    this.isFinished = true;
                }

                this.controllers[i].set(ControlMode.PercentOutput, power);
            }
        }
        else {
            for(int i = 0; i < this.controllers.length; i++) {
                this.controllers[i].set(ControlMode.PercentOutput, 0.0);
            }
        }
    }

}
