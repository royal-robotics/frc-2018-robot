package frc.team2522.robot.subsystems.Drivebase.DriveSystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.libs.DriveType;

public class DriveSystem {
    private DoubleSolenoid shift = new DoubleSolenoid(1, 2, 5);

    private DriveType driveType = DriveType.TankDrive;
    private boolean driveTypePressed = false;

    private boolean shiftOn = false;
    private boolean shiftOnPressed = false;

    public DriveType updateDriveType(boolean button) {
        if (button && !driveTypePressed) {
            driveTypePressed = true;
            toggleDriveType();
        } else if (!button) {
            driveTypePressed = false;
        }

        SmartDashboard.putString("Drive/DriveType", driveType.toString());

        return driveType;
    }

    public boolean updateShift(boolean leftButton, boolean rightButton) {
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

        SmartDashboard.putString("Drive/Gear", shiftOn ? "High" : "Low");

        return shiftOn;
    }

    private void toggleDriveType() {
        if (driveType == DriveType.TankDrive) {
            driveType = DriveType.CheesyDrive;
        } else {
            driveType = DriveType.TankDrive;
        }
    }
}
