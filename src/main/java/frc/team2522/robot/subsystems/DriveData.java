package frc.team2522.robot.subsystems;

import edu.wpi.first.wpilibj.*;
import frc.team2522.robot.libs.CircularList;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DriveData {
    private final double driveDistancePerPulse = (3.50 * 3.1415) / (360.00);
    private static final int msUpdate = 10;


    private Timer timer = new Timer();
    Encoder encoder;

    double lastPosition = 0;
    CircularList<Double> lastVelocities = new CircularList<Double>(10);
    CircularList<Double> lastPositionDiffs = new CircularList<Double>(10);

    long nsLastTime = 0;

    public DriveData(int aDio, int bDio, boolean reverseDirection) {
        this.encoder = new Encoder(new DigitalInput(aDio), new DigitalInput(bDio));
        encoder.setReverseDirection(reverseDirection);
        encoder.setDistancePerPulse(driveDistancePerPulse);

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
        long nsCurrentTime = System.nanoTime();
        long nsDiffTime = nsCurrentTime - nsLastTime;
        nsLastTime = nsCurrentTime;
        double msActualTime = nsDiffTime / 1000000.0;

        double newPosition = encoder.getDistance();
        double changeInPosition = newPosition - lastPosition;
        lastPositionDiffs.add(changeInPosition);
        //System.out.println("Ticks: " + changeInPosition / driveDistancePerPulse);
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

//        final int minPulses = 15;
//
//        double totalDistance = 0;
//        double pulses = 0;
//        for (int i = 0; i < lastPositionDiffs.size(); i++) {
//            double changeInPostion = lastPositionDiffs.get(i);
//            pulses += changeInPostion / driveDistancePerPulse;
//
//            if(pulses >= minPulses) {
//                totalDistance += pulses * driveDistancePerPulse;
//                pulses = 0;
//            }
//        }
//
//        return totalDistance / (msUpdate * lastPositionDiffs.size());
    }

    public double getAcceleration() {
        if(lastVelocities.size() < 2)
            return 0;

        double totalAcceleration = 0.0;
        int numAcc = 0;

        int i = 0;
        do {
            int iStart = i;
            double v1 = lastVelocities.get(i);
            double pulses = lastPositionDiffs.get(i) /  driveDistancePerPulse;

            do {
                i++;
                pulses += lastPositionDiffs.get(i) /  driveDistancePerPulse;
                //Or there isn't anymore diffs
            } while (pulses < 15);
            double v2 = lastVelocities.get(i);
            double a1 = (v2 - v1) / ((msUpdate * (i - iStart) / 1000.0));

            totalAcceleration += a1;
            numAcc++;
        } while (i < lastVelocities.size() - 1);

        return totalAcceleration / numAcc;
    }
}
