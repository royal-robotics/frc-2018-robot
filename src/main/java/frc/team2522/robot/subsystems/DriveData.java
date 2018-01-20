package frc.team2522.robot.subsystems;

import edu.wpi.first.wpilibj.*;
import frc.team2522.robot.libs.CircularList;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DriveData {
    private static final int msUpdate = 30;

    private Timer timer = new Timer();

    Encoder encoder;
    double lastPosition = 0;
    CircularList<Double> lastVelocities = new CircularList<Double>(10);

    public DriveData(Encoder encoder) {
        this.encoder = encoder;
        reset();

        timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    getValues();
                }
            }, msUpdate, msUpdate);
    }

    public void reset() {
        lastPosition = 0;
        encoder.reset();
        lastVelocities.clear();
    }

    public void getValues() {
        double newPosition = encoder.getDistance();
        double changeInPosition = newPosition - lastPosition;
        lastPosition = newPosition;

        double newVelocity = changeInPosition / (msUpdate / 1000.0);
        lastVelocities.add(newVelocity);
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

    public double getAcceleration() {
        if(lastVelocities.size() < 2)
            return 0;

        double total = 0;
        for (int i = 0; i < lastVelocities.size() - 1; i++) {
            double v1 = lastVelocities.get(i);
            double v2 = lastVelocities.get(i + 1);
            total += (v2 - v1) / (msUpdate / 1000.0);
        }
        return total / (lastVelocities.size() - 1);
    }
}
