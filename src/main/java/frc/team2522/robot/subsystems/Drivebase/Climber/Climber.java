package frc.team2522.robot.subsystems.Drivebase.Climber;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {

    private DoubleSolenoid ratchet = new DoubleSolenoid(0, 2, 5);
    private DoubleSolenoid pto = new DoubleSolenoid(1, 1, 6);

    Boolean isClimbingMode;

    public Climber(Boolean isClimbingMode) {
        this.isClimbingMode = isClimbingMode;
    }

    public Boolean updateClimbing(boolean leftButton, boolean rightButton) {
        if ((leftButton && rightButton) && !isClimbingMode) {
            isClimbingMode = true;
            ratchet.set(DoubleSolenoid.Value.kForward);
            pto.set(DoubleSolenoid.Value.kForward);
        }

        SmartDashboard.putBoolean("Climber/ClimbEnabled", isClimbingMode);

        return isClimbingMode;
    }
}
