package frc.team2522.robot.subsystems;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

import java.util.Timer;
import java.util.TimerTask;

//MAX ROBOT VELOCITY IS 175 inches/second
//MAX ROBOT ACCELERATION IS 333.33 inches/second^2

public class DriveController {

    final double wheelbase_width = 31.25;

    final Trajectory.Config config = new Trajectory.Config(
            Trajectory.FitMethod.HERMITE_CUBIC,
            Trajectory.Config.SAMPLES_HIGH,
            0.01, //0.01=10ms
            40,
            50,
            100.0);

    final Waypoint[] points = new Waypoint[] {
            new Waypoint(0, 0, Pathfinder.d2r(90)),
            //new Waypoint(-200, 250, Pathfinder.d2r(135)),
            new Waypoint(0, 150, Pathfinder.d2r (90)),
    };

    Drivebase drivebase;

    long nsStart;
    private Timer timer = new Timer();

    int cSegments;
    TrajectoryControl leftControl;
    TrajectoryControl rightControl;

    public DriveController(Drivebase db, DriveData ddL, DriveData ddR) {
        drivebase = db;

//        Trajectory trajectory = Pathfinder.generate(points, config);
//        TankModifier modifier = new TankModifier(trajectory).modify(wheelbase_width);
//
//        cSegments = trajectory.segments.length;
//        leftControl = new TrajectoryControl(modifier.getLeftTrajectory(), ddL);
//        rightControl = new TrajectoryControl(modifier.getRightTrajectory(), ddR);
    }

    public void Start() {
        nsStart = System.nanoTime();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                update();
            }
        }, 0, 10);
    }

    private void update() {
        long nsSinceStart = System.nanoTime() - nsStart;
        long msSinceStart = nsSinceStart / 1000000;
        long dt = msSinceStart / 10; //Since we have 100ms dt values

        //System.out.printf("Max Seg: %d, Cur Seg: %d\n",  cSegments, dt);

        if(dt < cSegments) {
            double leftPower = leftControl.getPower(dt, msSinceStart);
            //double rightPower = rightControl.getPower(dt, msSinceStart);
            //System.out.printf("Segment: %d, Left: %f, Right: %f\n",  dt, leftPower, rightPower);
            drivebase.setPower(-leftPower, -0);
        } else {
            drivebase.setPower(0, 0);
        }
    }
}