package frc.team2522.robot.autonomous.BuildingBlocks;

import frc.team2522.robot.autonomous.AutoStep;
import frc.team2522.robot.subsystems.Elevator.Elevator;

public class AutoIntakeArms extends AutoStep {

    public enum ArmPosition {
        closed,
        pickup,
        open
    }

    Elevator elevatorController;

    ArmPosition position;

    public AutoIntakeArms(Elevator elevatorController, ArmPosition position) {
        this.elevatorController = elevatorController;
        this.position = position;
    }

    @Override
    public void initialize() {
    }

    @Override
    public boolean isCompleted() {
        switch(this.position) {
            case open:
            {
                this.elevatorController.intake.setOpen();
                break;
            }
            case pickup:
            {
                this.elevatorController.intake.setPickup();
                break;
            }
            case closed:
            {
                this.elevatorController.intake.setClosed();
                break;
            }
            default:
            {
                this.elevatorController.intake.setClosed();
                break;
            }
        }
        return true;
    }

    @Override
    public void periodic() {
    }
}
