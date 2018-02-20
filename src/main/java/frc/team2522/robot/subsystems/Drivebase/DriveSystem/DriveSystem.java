package frc.team2522.robot.subsystems.Drivebase.DriveSystem;

import com.ctre.phoenix.drive.DiffDrive;
import com.ctre.phoenix.drive.DriveMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
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
    private boolean driveTypePressed = false;

    private boolean shiftOn = false;
    private boolean shiftOnPressed = false;

    public DriveSystem(TankDrive tankDrive, DiffDrive diffDrive) {
        this.tankDrive = tankDrive;
        this.diffDrive = diffDrive;
    }

    public void updateDriveType(boolean button) {
        if (button && !driveTypePressed) {
            driveTypePressed = true;
            toggleDriveType();
        } else if (!button) {
            driveTypePressed = false;
        }

        SmartDashboard.putString("DriveSystem/DriveType", driveType.toString());
    }

    public void updateShift(boolean leftButton, boolean rightButton) {
        if ((leftButton || rightButton) && !shiftOnPressed) {
            shiftOnPressed = true;
            shiftOn = !shiftOn;
        } else if (!leftButton && !rightButton) {
            shiftOnPressed = false;
        }

        if (shiftOn) {
            shift.set(DoubleSolenoid.Value.kForward);
        } else {
            shift.set(DoubleSolenoid.Value.kReverse);
        }

        SmartDashboard.putString("DriveSystem/Gear", shiftOn ? "High" : "Low");
    }

    public void drive(double left, double right, double forward, double turn, double deadzone) {
        if (driveType == DriveType.TankDrive) {
            if (left < deadzone && left > -deadzone) {
                left = 0.0;
            }
            if (right < deadzone && right > -deadzone) {
                right = 0.0;
            }

            SmartDashboard.putNumber("DriveSystem/TankDrive/LeftPercent", left);
            SmartDashboard.putNumber("DriveSystem/TankDrive/RightPercent", right);

            tankDrive.set(DriveMode.PercentOutput, left, right);
        } else {  // currentDriveType == DriveType.CheesyDrive
            if (forward < deadzone && forward > -deadzone) {
                forward = 0.0;
            }
            if (turn < deadzone && turn > -deadzone) {
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

    private void toggleDriveType() {
        if (driveType == DriveType.TankDrive) {
            driveType = DriveType.CheesyDrive;
        } else {
            driveType = DriveType.TankDrive;
        }
    }
}
