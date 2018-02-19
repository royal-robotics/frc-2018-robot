package frc.team2522.robot.subsystems.Elevator.Lift;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;
import frc.team2522.robot.subsystems.Drivebase.DriveData;
import frc.team2522.robot.subsystems.Elevator.Carriage.Carriage;
import jdk.nashorn.internal.scripts.JO;

public class Lift {

    TalonSRX liftMotors = new TalonSRX(2);
    VictorSPX lift2 = new VictorSPX(3);
    VictorSPX lift3 = new VictorSPX(4);


    Joystick driver;
    Carriage carriage;

    public Lift(Joystick driver, Carriage carriage) {
        this.driver = driver;
        this.carriage = carriage;

        lift2.follow(liftMotors);
        lift3.follow(liftMotors);
    }

    public void fmsUpdateTeleop() {

    }
}
