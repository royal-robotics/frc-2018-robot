package frc.team2522.robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.team2522.robot.libs.Axis;
import frc.team2522.robot.libs.Button;
import frc.team2522.robot.libs.IButton;

public class Controls {
    public static Joystick driver = new Joystick(0);
    private static boolean onePersonMode = true;

    public static class Elevator {
        public static class Intake {
            public static Button pickup = new Button(driver, 1, Button.ButtonType.Toggle); // A Button
            public static Button closed = new Button(driver, 2, Button.ButtonType.Toggle); // B Button
            public static Button open = new Button(driver, 3, Button.ButtonType.Toggle); // X Button

            public static IButton pullCube = new Axis(driver, 2, 0.1); // Left Trigger
            public static IButton rotateCube = new Axis(driver, 3, 0.1); // Right Trigger
        }
        public static class Lift {

        }
    }
}
