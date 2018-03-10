package frc.team2522.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.IMotorController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import frc.team2522.robot.Controls;

public class Elevator {
    private Intake intake;
    private Lift lift;


    public Elevator(Intake intake, Lift lift) {
        this.intake = intake;
        this.lift = lift;

        //setupIntakeManager();
    }

    public void reset() {
        this.intake.reset();
        this.lift.reset();
    }

    public void teleopPeriodic() {
        intake.teleopPeriodic();
        lift.teleopPeriodic();

        if(Controls.Elevator.Intake.armsClose()) {
            intake.setStop();
            intake.setClosed();
        }

        if(Controls.Elevator.Intake.pullCube() || Controls.Elevator.Intake.pushCube()) {
            if (Controls.Elevator.Intake.armsOpen()) {
                intake.setOpen();
            }
            else {
                intake.setPickup();
            }
        }
        else if (Controls.Elevator.Intake.armsOpen()) {
            intake.setOpen();
        }

        if(Controls.Elevator.Intake.pullCube() && Controls.Elevator.Intake.pushCube()) {
            intake.startRotate();
        }
        else if (Controls.Elevator.Intake.pullCube()) {
            intake.stopRotate();
            intake.setPull();
        } else if (Controls.Elevator.Intake.pushCube()) {
            intake.stopRotate();
            intake.setPush();
        } else {
            intake.stopRotate();
            intake.setStop();
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
