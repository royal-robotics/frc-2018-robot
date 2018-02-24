package frc.team2522.robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.team2522.robot.libs.Button;

public class Controls {
    public static Joystick driver = new Joystick(0);

    public static void setMode(boolean onePersonMode) {

    }

    public static class Elevator {
        public static class Intake {
            public static Button pickup = new Button(driver, 1, Button.ButtonType.Toggle);
            public static Button closed = new Button(driver, 2, Button.ButtonType.Toggle);
            public static Button open = new Button(driver, 3, Button.ButtonType.Toggle);


        }
    }
}
