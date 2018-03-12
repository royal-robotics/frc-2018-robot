package frc.team2522.robot.autonomous.BuildingBlocks;

import frc.team2522.robot.autonomous.AutoStep;
import frc.team2522.robot.libs.TrajectoryFollower;
import frc.team2522.robot.subsystems.Drivebase.DriveController;

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
        return follower.isFinished();
    }

    @Override
    public void periodic() {
    }
}
