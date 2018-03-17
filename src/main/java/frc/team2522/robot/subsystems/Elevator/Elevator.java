package frc.team2522.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.IMotorController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import frc.team2522.robot.Controls;

public class Elevator {
    public Intake intake;
    public Lift lift;


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
        else if(Controls.Elevator.Intake.pushCube() && this.intake.getArmsOut()) {
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

        if (!Controls.Elevator.Intake.pullCube() && !Controls.Elevator.Intake.pushCube() && this.intake.getArmsOut()) {
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
            double spitPower = Controls.Elevator.Intake.pushCubeModifier() ? 0.5 : 0.75;
            intake.setPush(spitPower);
        } else {
            intake.stopRotate();
            intake.setStop();
        }
    }
    public void robotPeriodic() {
        lift.writeToDashboard();
    }
}
