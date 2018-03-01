package frc.team2522.robot.libs;

import edu.wpi.first.wpilibj.Joystick;

public class POVButton implements IButton {
    protected final Joystick driver;
    protected final int povAngle;

    public POVButton(Joystick driver, int povAngle){
        this.driver = driver;
        this.povAngle =  povAngle;
    }

    public boolean isPressed(){
        int currentPov = driver.getPOV();
        return this.povAngle == currentPov;
    }
}
