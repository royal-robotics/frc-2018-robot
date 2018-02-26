package frc.team2522.robot.libs;

import java.time.Duration;
import java.time.Instant;

public class Stopwatch {

    Instant startTime, endTime;
    Duration duration;
    boolean isRunning = false;

    public static Stopwatch StartNew() {
        Stopwatch  stopwatch = new Stopwatch();
        stopwatch.start();
        return stopwatch;
    }

    public void start() {
        System.out.println("START");
        if (isRunning) {
            throw new RuntimeException("Stopwatch is already running.");
        }
        this.isRunning = true;
        startTime = Instant.now();
    }

    public Duration stop() {
        endTime = Instant.now();
        if (!isRunning) {
            throw new RuntimeException("Stopwatch has not been started yet");
        }
        isRunning = false;
        return getElapsedTime();
    }

    public Duration getElapsedTime() {
        Instant currentTime = Instant.now();
        return Duration.between(startTime, currentTime);
    }

    public void reset() {
        if (this.isRunning) {
            this.stop();
        }
        this.duration = null;
    }
}