package frc.team2522.robot.libs;

import com.ctre.phoenix.drive.DriveMode;
import com.ctre.phoenix.mechanical.Gearbox;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class TankDrive {
    private Gearbox _left;
    private Gearbox _right;

    public TankDrive(Gearbox left, Gearbox right) {
        _left = left;
        _right = right;
    }


    public void set(DriveMode driveMode, double left, double right)
    {
        switch (driveMode) {
            case Voltage:
                _left.enableVoltageCompensation(true);
                _right.enableVoltageCompensation(true);
                _left.set(ControlMode.PercentOutput, left);
                _right.set(ControlMode.PercentOutput, right);
                break;
            case PercentOutput:
                _left.enableVoltageCompensation(false);
                _right.enableVoltageCompensation(false);
                _left.set(ControlMode.PercentOutput, left);
                _right.set(ControlMode.PercentOutput, right);
                break;
        }
    }
}
