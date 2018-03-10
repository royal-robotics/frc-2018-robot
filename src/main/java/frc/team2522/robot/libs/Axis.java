package frc.team2522.robot.libs;

import edu.wpi.first.wpilibj.Joystick;
import frc.team2522.robot.Controls;

public class Axis implements IButton{
    private Joystick joystick;
    private Controls.Logitech310Axis axisIndex;
    private double deadband;

    private double defaultEdge = 0.25;
    private double lastReading = 0.0;

    public Axis(Joystick joystick, Controls.Logitech310Axis axisIndex, double deadband) {
        this.joystick = joystick;
        this.axisIndex = axisIndex;
        this.deadband = deadband;
    }

    public double getValue() {
        double value = joystick.getRawAxis(axisIndex.id);
        if (value < deadband && value > -deadband) {
            value = 0.0;
        }
        lastReading = value;
        return lastReading;
    }

    public boolean isPressed() {
        return isPressed(deadband);
    }

    public boolean isPressed(double edge) {
        return Math.abs(getValue()) > edge;
    }

    public boolean isToggled(double edge) {
        double currentValue = getValue();
        boolean value = lastReading < edge && currentValue > edge;
        return value;
    }
}
