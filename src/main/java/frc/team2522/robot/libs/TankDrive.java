package frc.team2522.robot.libs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;

public class TankDrive {
    private IMotorController _left;
    private IMotorController _right;

    public TankDrive(IMotorController left, IMotorController right) {
        _left = left;
        _right = right;
    }


    public void set( double left, double right)
    {
        _left.enableVoltageCompensation(false);
        _right.enableVoltageCompensation(false);
        _left.set(ControlMode.PercentOutput, left);
        _right.set(ControlMode.PercentOutput, right);
    }
}
