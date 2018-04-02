package frc.team2522.robot.autonomous.BuildingBlocks;

import frc.team2522.robot.autonomous.AutoStep;
import frc.team2522.robot.libs.TrajectoryFollower;
import frc.team2522.robot.subsystems.Elevator.Elevator;
import frc.team2522.robot.subsystems.Elevator.Lift;
import jaci.pathfinder.Pathfinder;

public class AutoLift extends AutoStep {
    Lift lift;

    double height;

    public AutoLift(Elevator elevatorController, double height) {
        this.lift = elevatorController.lift;
        this.height = height;
    }

    boolean isDone = false;
    boolean isStarted = false;
    boolean moveUp = true;
    long startTime = 0;

    @Override
    public void initialize() {
        startTime = System.nanoTime();
        double startPosition = this.lift.getPosition();
        moveUp = height > startPosition;
        this.isStarted = true;

        this.move(moveUp ? 0.65 : -0.25);
    }

    @Override
    public boolean isCompleted() {
        if(isDone) {
            return true;
        }

        if (this.isStarted) {
            double time = (double) (System.nanoTime() - startTime) / 1000000000.0;
            if (time > 2.0) {
                System.out.println("Auto Lift timeout! :(");
                stop();
            }

            double position = this.lift.getPosition();
            if ((moveUp && (position >= height)) || (!moveUp && (position <= height))) {
                stop();
                isDone = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public void periodic() {

    }

    @Override
    public void stop() {
        this.lift.setBreak(true);
        this.lift.setPower(0);
        isDone = true;
    }

    private void move(double power) {
        this.lift.setBreak(false);
        this.lift.setPower(power);
    }

}
