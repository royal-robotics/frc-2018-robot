package frc.team2522.robot.subsystems.Elevator.Lift;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.*;

public class Lift {

    TalonSRX liftMotors = new TalonSRX(2);
    VictorSPX lift2 = new VictorSPX(3);
    VictorSPX lift3 = new VictorSPX(4);
    TalonSRX carriage = null; //Shared with Lift;

    DoubleSolenoid brake = new DoubleSolenoid(0, 1, 6);

    Encoder encoder = new Encoder(14, 15);

    Joystick driver;

    public Lift(Joystick driver, TalonSRX carriage) {
        this.driver = driver;
        this.carriage = carriage;

        lift2.follow(liftMotors);
        lift3.follow(liftMotors);

        //TODO: Figure this out
        final double inchesPerPulse = 0.1;
        encoder.setDistancePerPulse(inchesPerPulse);
        encoder.setReverseDirection(false);
        encoder.reset();
    }

    public void fmsUpdateTeleop() {
        driver.setRumble(GenericHID.RumbleType.kLeftRumble, 1.0);
        driver.setRumble(GenericHID.RumbleType.kRightRumble, 1.0);

        System.out.println("Lift Encoder: " + encoder.getDistance());

        if(driver.getPOV() == 0) {
            liftMotors.set(ControlMode.PercentOutput, 0.8);
        } else if(driver.getPOV() == 180) {
            liftMotors.set(ControlMode.PercentOutput, -0.8);
        } else {
            liftMotors.set(ControlMode.PercentOutput, 0.0);
        }
    }
}