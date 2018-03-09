package frc.team2522.robot.libs;

import edu.wpi.first.wpilibj.Joystick;
import frc.team2522.robot.Controls;

public class MultiButton implements IButton {
    public enum MultiButtonType {
        EitherButton,
        BothButton
    }

    private IButton[] buttons;
    private ButtonType type;
    private MultiButtonType multiType;
    private boolean prevValue = false;

    public MultiButton(Joystick stick, Controls.Buttons[] buttons, ButtonType type, MultiButtonType multiType) {
        IButton[] actualButtons = new IButton[buttons.length];
        for (int i = 0; i < buttons.length; i++) {
            actualButtons[i] = new Button(stick, buttons[i], ButtonType.Hold);  // Button type doesn't matter, since we're only checking position
        }
        this.buttons = actualButtons;
        this.type = type;
        this.multiType = multiType;
    }

    public MultiButton(IButton[] buttons, ButtonType type, MultiButtonType multiType) {
        this.buttons = buttons;
        this.type = type;
        this.multiType = multiType;
    }

    public boolean isPressed() {
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

    public boolean getPosition() {
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
        for (IButton button : buttons) {
            switch(multiType) {
                case BothButton: {
                    result = result && button.getPosition();
                    break;
                }
                case EitherButton: {
                    result = result || button.getPosition();
                    break;
                }
            }
        }

        return result;
    }
}
