package frc.team2522.robot.subsystems.Elevator.Lift;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;

public class LiftData {
    Encoder encoder = new Encoder(new DigitalInput(14), new DigitalInput(15));
    //DigitalInput lift
    public LiftData() {

    }
}
