package frc.team2522.robot.subsystems.Drivebase.DriveSystem;

import com.ctre.phoenix.drive.DiffDrive;
import com.ctre.phoenix.drive.DriveMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.libs.Axis;
import frc.team2522.robot.libs.DriveType;
import frc.team2522.robot.libs.TankDrive;

public class DriveSystem {
    private TankDrive tankDrive;
    private DiffDrive diffDrive;

    private DoubleSolenoid shift = new DoubleSolenoid(1, 2, 5);

    DriveData driveDataLeft = new DriveData(10, 11, true);
    DriveData driveDataRight = new DriveData(12,13, false);

    private DriveType driveType = DriveType.CheesyDrive;

    public DriveSystem(TankDrive tankDrive, DiffDrive diffDrive) {
        this.tankDrive = tankDrive;
        this.diffDrive = diffDrive;
        shift.set(DoubleSolenoid.Value.kForward);
    }

    public DriveType getDriveType() {
        return driveType;
    }

    public void toggleControlsType() {
        if(driveType == DriveType.TankDrive)
            driveType = DriveType.CheesyDrive;
        else
            driveType = DriveType.TankDrive;
    }

    public void toggleShift() {
        if (shift.get() == DoubleSolenoid.Value.kForward) {
            shift.set(DoubleSolenoid.Value.kReverse);
        } else {
            shift.set(DoubleSolenoid.Value.kForward);
        }
    }

    public void drive(double left, double right, double turn) {
        if (driveType == DriveType.TankDrive) {
            SmartDashboard.putNumber("DriveSystem/TankDrive/LeftPercent", left);
            SmartDashboard.putNumber("DriveSystem/TankDrive/RightPercent", right);

            tankDrive.set(DriveMode.PercentOutput, -left, right);
        } else {  // currentDriveType == DriveType.CheesyDrive
            double forwardPower = left;
            double turnPower = (3*turn)/4;

            SmartDashboard.putNumber("Drive/CheesyDrive/forwardPercent", forwardPower);
            SmartDashboard.putNumber("Drive/CheesyDrive/turnPercent", turnPower);

            diffDrive.set(DriveMode.PercentOutput, forwardPower, turnPower);
        }
    }

    public void reset() {
        driveDataLeft.reset();
        driveDataRight.reset();
    }

    public void writeToDashboard() {
        SmartDashboard.putString("DriveSystem/DriveType", driveType.toString());
        boolean shiftOn = shift.get() == DoubleSolenoid.Value.kForward;
        SmartDashboard.putString("DriveSystem/Gear", shiftOn ? "High" : "Low");
    }
}
