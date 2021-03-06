package frc.team2522.robot.autonomous.BuildingBlocks;

import frc.team2522.robot.autonomous.AutoStep;
import frc.team2522.robot.subsystems.Elevator.Elevator;

public class AutoIntakeWheels extends AutoStep {

    private long startTime;
    private boolean isCompleted = false;

    Elevator elevatorController;

    double duration;
    double power = 0.0;

    public AutoIntakeWheels(Elevator elevatorController) {
        this(elevatorController, 0.5);
    }

    public AutoIntakeWheels(Elevator elevatorController, double duration) {
        this.elevatorController = elevatorController;
        this.duration = duration;
    }

    public AutoIntakeWheels(Elevator elevatorController, double duration, double power) {
        this.elevatorController = elevatorController;
        this.duration = duration;
        this.power = power;
    }

    @Override
    public void initialize() {
        System.out.println("AutoIntake");
        this.startTime = System.nanoTime();
        if (this.power != 0.0) {
            this.elevatorController.intake.setPush(power);
        }
        else {
            this.elevatorController.intake.setPush();
        }
    }

    @Override
    public boolean isCompleted() {
        if (((double)(System.nanoTime() - this.startTime) / 1000000000.0) >= duration) {
            this.elevatorController.intake.setStop();
            return true;
        }

        return false;
    }

    @Override
    public void periodic() {
        if (((double)(System.nanoTime() - this.startTime) / 1000000000.0) >= duration) {
            this.elevatorController.intake.setStop();
        }
    }

    public void stop(){
        this.startTime = 0;
        this.elevatorController.intake.setStop();
    }
}
