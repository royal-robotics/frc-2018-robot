package frc.team2522.robot.autonomous.BuildingBlocks;

import frc.team2522.robot.autonomous.AutoStep;
import frc.team2522.robot.libs.TrajectoryFollower;
import frc.team2522.robot.subsystems.Elevator.Elevator;
import jaci.pathfinder.Pathfinder;

public class AutoLift extends AutoStep {
    Elevator elevatorController;

    double height;
    TrajectoryFollower follower;

    public AutoLift(Elevator elevatorController, double height) {
        this.elevatorController = elevatorController;
        this.height = height;
    }

    @Override
    public void initialize() {
        this.follower = this.elevatorController.lift.moveTo(this.height);
    }

    @Override
    public boolean isCompleted() {
        if (this.follower != null && this.follower.isFinished()) {
            this.elevatorController.lift.stopFollower();
            return true;
        }

        return false;
    }

    @Override
    public void periodic() {
        if (this.follower.isFinished()) {
            this.elevatorController.lift.stopFollower();
        }
    }

}
