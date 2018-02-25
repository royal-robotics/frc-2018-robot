package frc.team2522.robot.subsystems.Elevator.Lift;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.libs.CircularList;

import java.util.Timer;
import java.util.TimerTask;

public class LiftData {
    private final int msUpdate = 10;

    //Average velocity state
    private Timer timer = new Timer();
    private double lastPosition = 0;
    private CircularList<Double> lastPositionDiffs = new CircularList<>(10);
    CircularList<Double> lastVelocities = new CircularList<>(10);

    //Robot components
    private Encoder encoder = new Encoder(14, 15);
    private DigitalInput hallEffect = new DigitalInput(0);

    public LiftData() {
        // TODO: The spool is actually bigger when the lift is down since the cord increases
        // the diameter. We should adjust this value as the lift moves.
        final double inchesPerPulse = (3.5 * Math.PI) / 256.0;

        encoder.setDistancePerPulse(inchesPerPulse);
        encoder.setReverseDirection(false);
        encoder.reset();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                tick();
            }
        }, msUpdate, msUpdate);
    }

    private void tick() {
        //TODO: use System.nanoTime to get a more precise time measurement
        double newPosition = encoder.getDistance();
        double changeInPosition = newPosition - lastPosition;
        lastPositionDiffs.add(changeInPosition);
        lastPosition = newPosition;

        double newVelocity = changeInPosition / (msUpdate / 1000.0);
        lastVelocities.add(newVelocity);

        SmartDashboard.putNumber("Lift/Data/position", getPosition());
        SmartDashboard.putNumber("Lift/Data/velocity", getVelocity());

        //System.out.println(getPosition());
    }

    public double getPosition() {
        return encoder.getDistance();
    }

    public double getVelocity() {
        if(lastVelocities.size() < 1)
            return 0;

        double total = 0;
        for(int i = 0; i < lastVelocities.size(); i++) {
            total += lastVelocities.get(i);
        }

        return total / lastVelocities.size();
    }
}
