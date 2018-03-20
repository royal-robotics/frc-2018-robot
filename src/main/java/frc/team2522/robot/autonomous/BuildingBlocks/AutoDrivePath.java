package frc.team2522.robot.autonomous.BuildingBlocks;

import frc.team2522.robot.autonomous.AutoStep;
import frc.team2522.robot.libs.TrajectoryFollower;
import frc.team2522.robot.subsystems.Drivebase.DriveController;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class AutoDrivePath  extends AutoStep {

    private TrajectoryFollower follower;
    private boolean isCompleted = false;

    DriveController driveController;

    String pathName;

    public AutoDrivePath(DriveController driveController, String pathName) {
        this.driveController = driveController;
        this.pathName = pathName;
    }

    @Override
    public void initialize() {
        follower = driveController.drivePath(this.pathName, false);
    }

    @Override
    public boolean isCompleted() {
        return childStepsCompleted() && follower.isFinished();
    }

    @Override
    public void periodic() {
        triggerChildSteps();

        //Feed child steps TODO: each step should have its own TimerTask :p
        for(AbstractMap.SimpleEntry<Double, AutoStep> step : childSteps) {
            step.getValue().periodic();
        }
    }

    //TODO: Move child step logic (below here) to base autoStep.
    //TODO: Instead of double (which is a distance trigger) there should be an abstract trigger
    //TODO: Default trigger should be immediate.
    private List<AbstractMap.SimpleEntry<Double, AutoStep>> childSteps = new ArrayList<>();

    private void triggerChildSteps() {
        for(AbstractMap.SimpleEntry<Double, AutoStep> step : childSteps) {
            if(!step.getValue().isStarted() && follower.getPosition() >= step.getKey())
                step.getValue().start();
        }
    }

    private boolean childStepsCompleted() {
        for(AbstractMap.SimpleEntry<Double, AutoStep> step : childSteps) {
            if(!step.getValue().isCompleted())
                return false;
        }

        return true;
    }

    public void addChildStep(double positionTrigger, AutoStep step) {
        childSteps.add(new AbstractMap.SimpleEntry<Double, AutoStep>(positionTrigger, step));
    }
}
