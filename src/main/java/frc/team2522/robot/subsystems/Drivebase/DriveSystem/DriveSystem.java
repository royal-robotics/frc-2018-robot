package frc.team2522.robot.subsystems.Drivebase.DriveSystem;

import com.ctre.phoenix.drive.DiffDrive;
import com.ctre.phoenix.drive.DriveMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.libs.DriveType;
import frc.team2522.robot.libs.TankDrive;

public class DriveSystem {
    private TankDrive tankDrive;
    private DiffDrive diffDrive;

    private DoubleSolenoid shift = new DoubleSolenoid(1, 2, 5);

    DriveData driveDataLeft = new DriveData(10, 11, true);
    DriveData driveDataRight = new DriveData(12,13, false);

    private DriveType driveType = DriveType.TankDrive;

    public DriveSystem(TankDrive tankDrive, DiffDrive diffDrive) {
        this.tankDrive = tankDrive;
        this.diffDrive = diffDrive;
    }

    public void toggleControlsType() {
        if(driveType == DriveType.TankDrive)
            driveType = DriveType.CheesyDrive;
        else
            driveType = DriveType.TankDrive;

        SmartDashboard.putString("DriveSystem/DriveType", driveType.toString());
    }

    public void toggleShift() {
        if (shift.get() == DoubleSolenoid.Value.kForward) {
            shift.set(DoubleSolenoid.Value.kReverse);
        } else {
            shift.set(DoubleSolenoid.Value.kForward);
        }

        boolean shiftOn = shift.get() == DoubleSolenoid.Value.kForward;
        SmartDashboard.putString("DriveSystem/Gear", shiftOn ? "High" : "Low");
    }

    public void drive(Joystick driver) {
        final double DEADZONE = 0.2;

        if (driveType == DriveType.TankDrive) {
            double left = driver.getRawAxis(1);
            double right = driver.getRawAxis(5);

            if (left < DEADZONE && left > -DEADZONE) {
                left = 0.0;
            }
            if (right < DEADZONE && right > -DEADZONE) {
                right = 0.0;
            }

            SmartDashboard.putNumber("DriveSystem/TankDrive/LeftPercent", left);
            SmartDashboard.putNumber("DriveSystem/TankDrive/RightPercent", right);

            tankDrive.set(DriveMode.PercentOutput, left, right);
        } else {  // currentDriveType == DriveType.CheesyDrive
            double forward = driver.getRawAxis(1);
            double turn = driver.getRawAxis(4);

            if (forward < DEADZONE && forward > -DEADZONE) {
                forward = 0.0;
            }
            if (turn < DEADZONE && turn > -DEADZONE) {
                turn = 0.0;
            }

            SmartDashboard.putNumber("Drive/CheesyDrive/ForwardPercent", forward);
            SmartDashboard.putNumber("Drive/CheesyDrive/TurnPercent", turn);

            diffDrive.set(DriveMode.PercentOutput, forward, turn);
        }
    }

    public void reset() {
        driveDataLeft.reset();
        driveDataRight.reset();
    }
}
