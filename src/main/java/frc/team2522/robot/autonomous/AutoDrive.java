package frc.team2522.robot.autonomous;

import edu.wpi.first.wpilibj.Timer;
import frc.team2522.robot.subsystems.Drivebase.DriveController;

public class AutoDrive extends AutoStep {

    private final Timer timer = new Timer();
    private final DriveController drivecontroller;

    private boolean isCompleted = false;

    public AutoDrive(DriveController drivecontroller) {
        this.drivecontroller = drivecontroller;
    }

    @Override
    public void initialize() {
        timer.start();
    }

    @Override
    public void stop() {
        //drivebase.setPower(0.0, 0.0);
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public void periodic() {
        //drivebase.setPower(0.5, 0.5);
        if(timer.hasPeriodPassed(2)) {
            isCompleted = true;
        }
    }
}
