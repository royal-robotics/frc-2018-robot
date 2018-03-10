package frc.team2522.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.libs.CircularList;
import jaci.pathfinder.Pathfinder;

import java.util.Timer;
import java.util.TimerTask;

public class LiftData {
    //100% power = 177 inches / second (According to Rafi)

    //Observed velocity data:
    //.4% = 39 inches / second
    //.6% = 69 inches / second

    private final int msUpdate = 10;

    //Average velocity state
    private Timer timer = new Timer();
    private double lastPosition = 0;
    private CircularList<Double> lastPositionDiffs = new CircularList<>(10);
    CircularList<Double> lastVelocities = new CircularList<>(5);

    //Robot components
    public Encoder encoder = new Encoder(14, 15);

    //Position state
    double offset = Double.NaN;

    public LiftData() {
        final double inchesPerPulse = (3.7 * Math.PI) / 256.0;

        encoder.setDistancePerPulse(inchesPerPulse);
        encoder.setReverseDirection(false);

        reset(Double.NaN);

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

        //dashboard update
        SmartDashboard.putNumber("Lift/Data/position", getPosition());
        SmartDashboard.putNumber("Lift/Data/velocity", getVelocity());
    }

    public void reset(double offset) {
        this.offset = offset;
        encoder.reset();
    }


    public double getPosition() {
        return encoder.getDistance() + offset;
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
