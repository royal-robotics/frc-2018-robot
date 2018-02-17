package frc.team2522.robot.subsystems;

import jaci.pathfinder.Trajectory;

public class TrajectoryControl {
    Trajectory trajectory;
    DriveData driveData;

    double lastMotionError = 0;

    public long msSinceStartLast = 0;

    public TrajectoryControl(Trajectory t, DriveData dd) {
        trajectory = t;
        driveData = dd;
    }

    //TODO:Consider adjusting power based on velocity + acceleration error
    //TODO:Consider adjusting power based on bearing error
    //double BError = motionStartBearing - robot.getBearing();
    //leftPower = leftPower + (0.015 * BError);
    //rightPower = rightPower - (0.015 * BError);

    public double getPower(long dt, long msSinceStart) {
        final double kDp = 0.0165;
        //final double kVf = 1.0 / 175.0;
        final double kVf = 1.0 / 78;
        final double kDd = 0.0006;

        Trajectory.Segment segment = trajectory.get((int)dt);
        double pos = segment.position;
        double vel = segment.velocity;

        double power = (kVf * vel) + (0.2);

        double error = pos - driveData.getPosition(); // Assumes we start at 0
        double pAdjust = kDp * error;

        double dError = ((error - lastMotionError) / (msSinceStart - msSinceStartLast)) - vel;
        double dAdjust = (kDd * dError);

        //Update feedback values for next iteration
        lastMotionError = dError; //Should we use error here?
        msSinceStartLast = msSinceStart;

        //System.out.printf("%f\t%f\t%f\t%f\t%f\n", power, kVf * vel, vel, pos, driveData.getPosition());
        System.out.printf("%f\t%f\t%f\t%f\t%f\n", pos, driveData.getPosition(), power, pAdjust, dAdjust);

        return power + dAdjust + pAdjust;
    }

    public double getPowerOld(long dt, long msSinceStart) {
        final double kDp = 0.0165;
        final double kVf = 1.0 / 175.0;
        final double kDd = 0.0006;

        Trajectory.Segment segment = trajectory.get((int)dt);
        double pos = segment.position;
        double vel = segment.velocity;

        double power = kVf * vel;
        double error = pos - driveData.getPosition(); // Assumes we start at 0
        power = power + (kDp * error);

        double leftDError = ((error - lastMotionError) / (msSinceStart - msSinceStartLast)) - vel;
        power = power + (kDd * leftDError);

        //Update feedback values for next iteration
        lastMotionError = error;
        msSinceStartLast = msSinceStart;

        return power;
    }
}
