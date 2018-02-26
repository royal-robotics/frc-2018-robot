package frc.team2522.robot.subsystems.Elevator.Lift;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

public class LiftController {

    public enum MovementType {
        Slow,
        Quick
    }

    private final double maxHeight = 100.0;
    private final double minHeight = 0.0;

    //TODO: pull this from a file deployed with the robot code
    //Trajectory trajectorySlow = generateTrajectory();

    public void BeginMovement(MovementType type, double setPoint) {

    }

    public void cancelMovement() {
        //quickly decelerate and then stopMovement
    }

    public void stopMovement() {

    }

    public void quickMovement(double position) {
        //This should be ignored if operatorMovement is running
    }



    private static Trajectory generateTrajectory() {
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
