package frc.team2522.robot.subsystems.Climber;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;

public class Climber {

    DoubleSolenoid ratchet = new DoubleSolenoid(0, 2, 5);
    DoubleSolenoid pto = new DoubleSolenoid(1, 1, 6);

    //TODO: The climber will need access to the drive motors
    public Climber(Joystick driver) {

    }

    public void fmsUpdateTeleop() {

    }
}
