package frc.team2522.robot.subsystems.Elevator.Carriage;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Carriage {
    TalonSRX carriage = new TalonSRX(8);

    public Carriage() {
    }

    public void set(double power) {
        carriage.set(ControlMode.PercentOutput, power);
    }
}
