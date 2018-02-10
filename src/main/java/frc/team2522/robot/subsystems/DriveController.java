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
    private Timer timer;

    int cSegments;
    TrajectoryControl leftControl;
    TrajectoryControl rightControl;

    DriveData ddL;
    DriveData ddR;

    public DriveController(Drivebase db, DriveData ddL, DriveData ddR) {
        drivebase = db;

//        Trajectory trajectory = Pathfinder.generate(points, config);
//        TankModifier modifier = new TankModifier(trajectory).modify(wheelbase_width);
//
//        cSegments = trajectory.segments.length;
//        leftControl = new TrajectoryControl("left", modifier.getLeftTrajectory(), ddL);
//        rightControl = new TrajectoryControl("right", modifier.getRightTrajectory(), ddR);

        this.ddL = ddL;
        this.ddR = ddR;
    }

    public void Start() {
        nsStart = System.nanoTime();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                update();
            }
        }, 0, 10);
    }

    public void Stop() {
        if(timer != null) {
            timer.cancel();
            System.out.println("stoping drive control timer");
        }

    }

    private void update() {
        long nsSinceStart = System.nanoTime() - nsStart;
        long msSinceStart = nsSinceStart / 1000000;
        long dt = msSinceStart / 10; //Since we have 100ms dt values

        //System.out.printf("Max Seg: %d, Cur Seg: %d\n",  cSegments, dt);

        if(dt < cSegments) {
            double leftPower = leftControl.getPower(dt, msSinceStart);
            double rightPower = rightControl.getPower(dt, msSinceStart);


//            System.out.printf("Left: %f, Right: %f ------ %f, %f\n", leftPower, rightPower, ddL.getPosition(), ddR.getPosition());

            drivebase.setPower(-leftPower, -rightPower);
        } else {
            System.out.println("AUTO OVER");
            timer.cancel();
            drivebase.setPower(0, 0);
        }
    }
}