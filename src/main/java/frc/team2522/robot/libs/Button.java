package frc.team2522.robot.libs;

import edu.wpi.first.wpilibj.Joystick;

public class Button
{
    public enum ButtonType {
        /***
         * isPressed function returns true while the button is down
         */
        Hold,

        /***
         * isPressed function returns true a single time until button is toggled up and back down
         */
        Toggle, //isPressed function returns true a single time until button is toggled up and back down
    }

    protected final ButtonType type;
    protected final Joystick stick;
    protected final int buttonId;
    protected boolean previousValue;

    /***
     * Create a standard Joystick button instance.
     *
     * @param stick 	The Joystick that the button is on.
     * @param buttonId	The Id/Index of the button.
     * @param type		The type of button to create.
     */
    public Button(Joystick stick, int buttonId, ButtonType type)
    {
        this.stick = stick;
        this.buttonId = buttonId;
        this.type = type;
        this.previousValue = false;
    }

    /***
     * Get the position of the button.
     *
     * @return true if the button is pressed and false otherwise.
     */
    protected synchronized boolean getPosition()
    {
        return this.stick.getRawButton(this.buttonId);
    }

    /***
     * Get the button isPressed state for this type of button.
     *
     * @return	true if button action should happen, false otherwise.
     */
    public synchronized boolean isPressed()
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