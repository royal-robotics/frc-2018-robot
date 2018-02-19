package frc.team2522.robot.subsystems.Elevator.Lift;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

public class LiftController {
    private final double maxHeight = 100.0;
    private final double resetUpHeight = 10.0;

    //TODO: pull this from a file deployed with the robot code
    Trajectory trajectorySlow = generateTrajectory();

    public LiftController() {

    }

    public static Trajectory generateTrajectory() {
        final Waypoint[] points = new Waypoint[] {
                new Waypoint(0, 0, 0),
                new Waypoint(100, 0, 0),
        };

        final Trajectory.Config config = new Trajectory.Config(
                Trajectory.FitMethod.HERMITE_CUBIC,
                Trajectory.Config.SAMPLES_HIGH,
                0.01, //0.01=10ms
                40, //TODO: figure this out
                50, //TODO: figure this out
                100.0); //TODO: figure this out

        return Pathfinder.generate(points, config);
    }
}
