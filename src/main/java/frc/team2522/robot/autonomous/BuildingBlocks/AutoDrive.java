package frc.team2522.robot.autonomous.BuildingBlocks;

import com.ctre.phoenix.drive.DriveMode;
import edu.wpi.first.wpilibj.Timer;
import frc.team2522.robot.autonomous.AutoStep;
import frc.team2522.robot.libs.TankDrive;
import frc.team2522.robot.libs.TrajectoryFollower;
import frc.team2522.robot.subsystems.Drivebase.DriveController;

public class AutoDrive extends AutoStep {

    private TrajectoryFollower follower;
    private boolean isCompleted = false;

    DriveController driveController;
    double distance;

    public AutoDrive(DriveController driveController, double distance) {
        this.driveController = driveController;
        this.distance = distance;
    }

    @Override
    public void initialize() {
        follower = driveController.driveDistance(distance, 100, 100, 300);
    }


    @Override
    public boolean isCompleted() {
        return follower.isFinished();
    }

    @Override
    public void periodic() {
    }
}
