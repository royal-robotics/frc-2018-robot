package frc.team2522.robot.autonomous;

public abstract class AutoStep {

    public abstract void initialize();
    public abstract void stop();
    public abstract boolean isCompleted();
    public abstract void periodic();
}