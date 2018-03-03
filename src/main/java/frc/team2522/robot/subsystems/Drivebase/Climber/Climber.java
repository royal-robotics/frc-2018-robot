package frc.team2522.robot.subsystems.Drivebase.Climber;

import com.ctre.phoenix.drive.DriveMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.libs.Axis;
import frc.team2522.robot.libs.TankDrive;

public class Climber {
    TankDrive tankDrive;

    private DoubleSolenoid ratchet = new DoubleSolenoid(0, 2, 5);
    private DoubleSolenoid pto = new DoubleSolenoid(1, 1, 6);

    Boolean isClimbingMode;

    public Climber(TankDrive tankDrive, Boolean isClimbingMode) {
        this.tankDrive = tankDrive;
        this.isClimbingMode = isClimbingMode;
        ratchet.set(DoubleSolenoid.Value.kReverse);
        pto.set(DoubleSolenoid.Value.kReverse);
    }

    public void turnClimbModeOn() {
        isClimbingMode = true;
        ratchet.set(DoubleSolenoid.Value.kForward);
        pto.set(DoubleSolenoid.Value.kForward);
    }

    public void climb(Axis left, Axis right) {
        double leftPower = left.getValue();
        double rightPower = right.getValue();

        double power = (leftPower + rightPower) / 2;

        SmartDashboard.putNumber("Drive/ClimbDrive/Percent", power);

        tankDrive.set(DriveMode.PercentOutput, power, power);
    }

    public void writeToDashboard() { SmartDashboard.putBoolean("Climber/ClimbEnabled", isClimbingMode);
    }
}
