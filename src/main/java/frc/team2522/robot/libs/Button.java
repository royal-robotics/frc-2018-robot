package frc.team2522.robot.libs;

import edu.wpi.first.wpilibj.Joystick;
import frc.team2522.robot.Controls;

public class Button implements IButton {
    protected final ButtonType type;
    protected final Joystick stick;
    protected final Controls.Buttons button;
    protected boolean previousValue;

    /***
     * Create a standard Joystick button instance.
     *
     * @param stick 	The Joystick that the button is on.
     * @param button	The Id/Index of the button.
     * @param type		The type of button to create.
     */
    public Button(Joystick stick, Controls.Buttons button, ButtonType type)
    {
        this.stick = stick;
        this.button = button;
        this.type = type;
        this.previousValue = false;
    }

    /***
     * Get the position of the button.
     *
     * @return true if the button is pressed and false otherwise.
     */
    public boolean getPosition()
    {
        return this.stick.getRawButton(this.button.id);
    }

    /***
     * Get the button isPressed state for this type of button.
     *
     * @return	true if button action should happen, false otherwise.
     */
    public boolean isPressed()
    {
        boolean value = this.getPosition();
        boolean retValue = false;

        switch(this.type)
        {
            case Hold:
            {
                retValue = value;
                break;
            }
            case Toggle:
            {
                retValue = !this.previousValue && value;
                break;
            }
        }

        this.previousValue = value;

        return retValue;
    }
}