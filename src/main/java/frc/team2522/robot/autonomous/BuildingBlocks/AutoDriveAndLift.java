package frc.team2522.robot.autonomous.BuildingBlocks;

import frc.team2522.robot.autonomous.AutoStep;

import frc.team2522.robot.libs.TrajectoryFollower;
import frc.team2522.robot.subsystems.Drivebase.DriveController;
import frc.team2522.robot.subsystems.Elevator.Elevator;

import java.util.ArrayList;

public class AutoDriveAndLift extends AutoStep {

    public class MoveStep {
        public double startDistance;
        public double liftDestination;
        public boolean hasMoved;

        public MoveStep(double startDistance, double liftDestination) {
            this.startDistance = startDistance;
            this.liftDestination = liftDestination;
            this.hasMoved = false;
        }
    }

    private TrajectoryFollower driveFollower;
    private boolean isCompleted = false;

    DriveController driveController;
    Elevator elevatorController;

    ArrayList<MoveStep> moveSteps = new ArrayList<MoveStep>();

    TrajectoryFollower liftFollower;

    String pathName;
    double driveDistance;

    public AutoDriveAndLift(DriveController driveController, double distance, Elevator elevatorController) {
        this.driveController = driveController;
        this.driveDistance = distance;
        this.pathName = null;

        this.elevatorController = elevatorController;
    }

    public AutoDriveAndLift(DriveController driveController, String pathName, Elevator elevatorController) {
        this.driveController = driveController;
        this.pathName = pathName;

        this.elevatorController = elevatorController;
    }

    /**
     *
     * @param startDistance
     * @param liftDestination
     */
    public void AddLiftMove(double startDistance, double liftDestination) {
        this.moveSteps.add(new MoveStep(startDistance, liftDestination));
    }

    @Override
    public void initialize() {
        if (this.pathName != null) {
            driveFollower = driveController.drivePath(this.pathName, false);
        }
        else {
            driveFollower = driveController.driveDistance(this.driveDistance, 100, 150, 300);
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
        for(int i = 0; i < this.moveSteps.size(); i++) {
            MoveStep moveStep = this.moveSteps.get(i);
            if (!moveStep.hasMoved && (driveFollower.getPosition() >= moveStep.startDistance)) {
                if (this.liftFollower != null) {
                    this.elevatorController.lift.stopFollower();
                }
                this.liftFollower = this.elevatorController.lift.moveTo(moveStep.liftDestination);
                moveStep.hasMoved = true;
            }
        }

        if (this.liftFollower != null && this.liftFollower.isFinished()) {
            this.elevatorController.lift.stopFollower();
            this.liftFollower = null;
        }
    }
}
