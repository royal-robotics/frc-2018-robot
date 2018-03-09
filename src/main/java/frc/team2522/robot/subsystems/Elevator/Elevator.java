package frc.team2522.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import frc.team2522.robot.Controls;
import frc.team2522.robot.libs.ObservableBoolean;
import frc.team2522.robot.subsystems.Elevator.Intake.Intake;
import frc.team2522.robot.subsystems.Elevator.Lift.Lift;

import java.util.Timer;
import java.util.TimerTask;

public class Elevator {
    Joystick driver;
    ObservableBoolean isClimbingMode;

    TalonSRX carriage = new TalonSRX(8);

    Intake intake;
    Lift lift;

    public Elevator(Joystick driver, ObservableBoolean isClimbingMode) {
        this.driver = driver;
        this.isClimbingMode = isClimbingMode;

        intake = new Intake(driver, carriage);
        lift = new Lift(driver, carriage);

        //setupIntakeManager();
    }

    boolean liftOpenManual = false;

    public void fmsUpdateTeleop() {
        //TODO: intake and lift shouldn't have fmsUpdate functions, and shouldn't
        //take joystick values in. Since they both control the carriage it could lead
        //to conflicting modes. Elevator should control all the modes and who should
        // be doing what.

        intake.fmsUpdateTeleop();
        lift.fmsUpdateTeleop();

        if(Controls.Elevator.Intake.toggleIntake.isPressed()) {
            liftOpenManual = !liftOpenManual;
        }

        //TODO: Checking intake buttons is a kludge, better to ask intake if it's running
        if(!liftOpenManual) {
            intake.setClosed();
        } else if(Controls.Elevator.Intake.pullCube.isPressed() || Controls.Elevator.Intake.pushCube.isPressed()) {
            intake.setPickup();
        } else {
            intake.setOpen();
        }
    }

    // This is code to manage the intake position based on the lift height
    // It's disabled because we think cassidy is more reliable than the encoder :)
    /*Timer autoIntakeMovementTimer = null;
    boolean isAutoIntakeMode = false;
    boolean isManualModeClosed = true;
    private void setupIntakeManager() {
        final long msAutoLiftTick = 100;
        autoIntakeMovementTimer = new Timer();
        autoIntakeMovementTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if(Controls.Elevator.Intake.toggleIntake.isPressed()) {
                    isAutoIntakeMode = false;
                    isManualModeClosed = !isManualModeClosed;
                }

                if(Controls.Elevator.Intake.autoIntakeMode.isPressed()) {
                    isAutoIntakeMode = true;
                }

                boolean liftOpen = true;
                if(isAutoIntakeMode && lift.getPosition() < 20) {
                    liftOpen = false;
                } else if (!isAutoIntakeMode && !isManualModeClosed) {
                    liftOpen = false;
                }

                if(liftOpen) {
                    intake.setOpen();
                } else if(Controls.Elevator.Intake.pullCube.isPressed() || Controls.Elevator.Intake.pushCube.isPressed()) {
                    intake.setPickup();
                } else {
                    intake.setClosed();
                }
            }
        }, msAutoLiftTick, msAutoLiftTick);
    }
    */
}
