package frc.team2522.robot.autonomous.BuildingBlocks;

import frc.team2522.robot.autonomous.AutoStep;
import frc.team2522.robot.subsystems.Elevator.Elevator;

public class AutoIntakeArm extends AutoStep {

    private long startTime;
    private boolean isCompleted = false;

    Elevator elevatorController;

    boolean moveUp = false;
    boolean isDone = false;

    public AutoIntakeArm(Elevator elevatorController) {
        this(elevatorController, false);
    }

    public AutoIntakeArm(Elevator elevatorController, boolean moveUp) {
        this.elevatorController = elevatorController;
        this.moveUp = moveUp;
    }

    @Override
    public void initialize() {
        System.out.println("AutoIntake Angle move " + (this.moveUp ? "UP" : "DOWN"));
        this.isDone = false;
    }

    @Override
    public boolean isCompleted() {
        //if (!this.isDone) {
            if (this.moveUp) {
                this.isDone = this.elevatorController.intake.isArmUp();
            } else {
                this.isDone = this.elevatorController.intake.isArmDown();
            }
        //}

        return isDone;
    }

    @Override
    public void periodic() {
        if (!this.isDone) {
            if (this.moveUp) {
                this.elevatorController.intake.moveArmUp();
            } else {
                this.elevatorController.intake.moveArmDown();
            }
        }
    }

    @Override
    public void stop() {
        this.isDone = true;
        this.elevatorController.intake.moveArm(0.0);
    }
}
