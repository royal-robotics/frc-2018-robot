package frc.team2522.robot.subsystems.Drivebase.Climber;

import com.ctre.phoenix.drive.DriveMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

    public void updateClimbing(boolean leftButton, boolean rightButton) {
        if ((leftButton && rightButton) && !isClimbingMode) {
            isClimbingMode = true;
            ratchet.set(DoubleSolenoid.Value.kForward);
            pto.set(DoubleSolenoid.Value.kForward);
        }

        SmartDashboard.putBoolean("Climber/ClimbEnabled", isClimbingMode);
    }

    public void climb(double left, double right, double deadzone) {
        if (left < deadzone) {
            left = 0.0;
        }
        if (right < deadzone) {
            right = 0.0;
        }

        double power = (left + right) / 2;

        SmartDashboard.putNumber("Drive/ClimbDrive/Percent", power);

        tankDrive.set(DriveMode.PercentOutput, power, power);
    }
}
