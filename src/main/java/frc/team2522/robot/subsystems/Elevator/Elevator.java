package frc.team2522.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import frc.team2522.robot.subsystems.Elevator.Intake.Intake;
import frc.team2522.robot.subsystems.Elevator.Lift.Lift;

public class Elevator {
    Joystick driver;

    TalonSRX carriage = new TalonSRX(8);
    
    Intake intake;
    Lift lift;

    public Elevator(Joystick driver) {
        this.driver = driver;

        intake = new Intake(driver, carriage);
        lift = new Lift(driver, carriage);
    }

    public void fmsUpdateTeleop() {
        //TODO: intake and lift shouldn't have fmsUpdate functions, and shouldn't
        //take joystick values in. Since they both control the carriage it could lead
        //to conflicting modes. Elevator should control all the modes and who should
        // be doing what.

        intake.fmsUpdateTeleop();
        lift.fmsUpdateTeleop();
    }
}
