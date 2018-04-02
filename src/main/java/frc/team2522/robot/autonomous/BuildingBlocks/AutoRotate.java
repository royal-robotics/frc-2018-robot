package frc.team2522.robot.autonomous.BuildingBlocks;

import frc.team2522.robot.autonomous.AutoStep;
import frc.team2522.robot.libs.TrajectoryFollower;
import frc.team2522.robot.subsystems.Drivebase.DriveController;

public class AutoRotate extends AutoStep {

    private TrajectoryFollower follower;

    DriveController driveController;
    double angle;

    public AutoRotate(DriveController driveController, double angle) {
        this.driveController = driveController;
        this.angle = angle;
    }

    @Override
    public void initialize() {
        follower = driveController.driveRotate(angle, 80, 120, 200);
    }


    @Override
    public boolean isCompleted() {
        return follower.isFinished();
    }

    @Override
    public void periodic() {
    }
}
