package frc.team2522.robot.libs;

import edu.wpi.first.wpilibj.Joystick;

public class Axis {
    private Joystick joystick;
    private int axisIndex;
    private double deadband;

    private double lastReading = 0.0;

    public Axis(Joystick joystick, int axisIndex, double deadband) {
        this.joystick = joystick;
        this.axisIndex = axisIndex;
        this.deadband = deadband;
    }

    public double getValue() {
        lastReading = joystick.getRawAxis(axisIndex);
        return lastReading;
    }

    public boolean isPressed(double edge) {
        return getValue() > edge;
    }

    public boolean isToggled(double edge) {
        double currentValue = joystick.getRawAxis(axisIndex);
        boolean value = lastReading < edge && currentValue > edge;
        lastReading = currentValue;
        return value;
    }
}
