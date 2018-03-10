package frc.team2522.robot.libs;

import edu.wpi.first.wpilibj.Joystick;
import frc.team2522.robot.Controls;

public class MultiButton implements IButton {
    public enum MultiButtonType {
        EitherButton,
        BothButton
    }
    private Joystick stick;
    private Controls.Logitech310Button[] buttonIds;
    private ButtonType type;
    private MultiButtonType multiType;
    private boolean prevValue;

    public MultiButton(Joystick stick, Controls.Logitech310Button[] buttonIds, ButtonType type, MultiButtonType multiType) {
        this.stick = stick;
        this.buttonIds = buttonIds;
        this.type = type;
        this.multiType = multiType;
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
        boolean result =  false;

        switch(multiType){
            case BothButton: {
                result = true;
                break;
            }
            case EitherButton: {
                result = false;
                break;
            }
        }

        for (Controls.Logitech310Button buttonId : buttonIds) {
            switch(multiType) {
                case BothButton: {
                    result = result && stick.getRawButton(buttonId.id);
                    break;
                }
                case EitherButton: {
                    result = result || stick.getRawButton(buttonId.id);
                    break;
                }
            }
        }

        return result;
    }
}
