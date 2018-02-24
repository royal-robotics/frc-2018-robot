package frc.team2522.robot.libs;

import edu.wpi.first.wpilibj.Joystick;

public class MultiButton implements IButton {
    private Joystick stick;
    private int[] buttonIds;
    private ButtonType type;
    private boolean prevValue;

    public MultiButton(Joystick stick, int[] buttonIds, ButtonType type) {
        this.stick = stick;
        this.buttonIds = buttonIds;
        this.type = type;
        this.prevValue = false;
    }

    public synchronized boolean isPressed() {
        boolean result = false;
        boolean position = getPosition();
        switch (type) {
            case Hold:
            {
                result = position;
                break;
            }
            case Toggle:
            {
                result = !prevValue && position;
                break;
            }
        }

        prevValue = position;
        return result;
    }

    private synchronized boolean getPosition() {
        boolean result = true;
        for (int buttonId : buttonIds) {
            result = result && stick.getRawButton(buttonId);
        }

        return result;
    }
}
