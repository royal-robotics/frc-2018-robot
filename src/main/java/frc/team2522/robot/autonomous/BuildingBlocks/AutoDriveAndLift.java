package frc.team2522.robot.autonomous.BuildingBlocks;

import frc.team2522.robot.autonomous.AutoStep;

import frc.team2522.robot.libs.TrajectoryFollower;
import frc.team2522.robot.subsystems.Drivebase.DriveController;
import frc.team2522.robot.subsystems.Elevator.Elevator;

public class AutoDriveAndLift extends AutoStep {

    private TrajectoryFollower driveFollower;
    private boolean isCompleted = false;

    DriveController driveController;
    Elevator elevatorController;
    double liftDestination;
    double liftStart;

    TrajectoryFollower liftFollower;

    String pathName;
    double driveDistance;

    public AutoDriveAndLift(DriveController driveController, double distance, Elevator elevatorController, double liftDestination, double liftStart) {
        this.driveController = driveController;
        this.driveDistance = distance;
        this.pathName = null;

        this.elevatorController = elevatorController;
        this.liftDestination = liftDestination;
        this.liftStart = liftStart;
    }

    public AutoDriveAndLift(DriveController driveController, String pathName, Elevator elevatorController, double liftDestination, double liftStart) {
        this.driveController = driveController;
        this.pathName = pathName;

        this.elevatorController = elevatorController;
        this.liftDestination = liftDestination;
        this.liftStart = liftStart;
    }

    @Override
    public void initialize() {
        if (this.pathName != null) {
            driveFollower = driveController.drivePath(this.pathName, false);
        }
        else {
            driveFollower = driveController.driveDistance(this.driveDistance, 100, 100, 200);
        }
    }

    @Override
    public boolean isCompleted() {
        if (driveFollower.isFinished()) {
            if (this.liftFollower != null) {
                return this.liftFollower.isFinished();
            }
            else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void periodic() {
        if (driveFollower.getPosition() >= this.liftStart) {
            this.liftFollower = this.elevatorController.lift.moveTo(this.liftDestination);
        }
    }
}
