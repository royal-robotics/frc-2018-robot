package frc.team2522.robot.libs;

public interface ITrajectoryFollower
{
    public void start();

    public void stop();

    public boolean isRunning();

    public boolean isFinished();

    public double getPosition();
}
