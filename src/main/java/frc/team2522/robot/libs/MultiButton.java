package frc.team2522.robot.libs;

import edu.wpi.first.wpilibj.Joystick;

public class MultiButton implements IButton {
    public enum MultiButtonType {
        EitherButton,
        BothButton
    }
    private Joystick stick;
    private int[] buttonIds;
    private ButtonType type;
    private MultiButtonType multiType;
    private boolean prevValue;

    public MultiButton(Joystick stick, int[] buttonIds, ButtonType type, MultiButtonType multiType) {
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
        for (int buttonId : buttonIds) {
            switch(multiType) {
                case BothButton: {
                    result = result && stick.getRawButton(buttonId);
                    break;
                }
                case EitherButton: {
                    result = result || stick.getRawButton(buttonId);
                    break;
                }
            }
        }

        return result;
    }
}
