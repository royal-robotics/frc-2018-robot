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

        if(Controls.Elevator.Intake.pullCube()) {
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
}
