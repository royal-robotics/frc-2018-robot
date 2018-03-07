package frc.team2522.robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.team2522.robot.libs.*;

public class Controls {
    public static Joystick driver = new Joystick(0);
    public static Joystick operator = new Joystick (1);
    private static boolean onePersonMode = true;

    public static class Elevator {
        public static class Intake {
            public static IButton pickup = new Button(operator, 1, Button.ButtonType.Toggle); // A Button
            public static IButton closed = new Button(operator, 2, Button.ButtonType.Toggle); // B Button
            public static IButton open = new Button(operator, 3, Button.ButtonType.Toggle); // X Button

            //If both of these buttons are on rotate runs (we should make a multi-button that handles this)
            public static IButton pullCube = new Axis(operator, 2, 0.1); // Left Trigger
            public static IButton pushCube = new Axis(operator, 3, 0.1); // Right Trigger
        }
        public static class Lift {
            public static IButton liftUp = new POVButton(operator, 0);
            public static IButton liftdown = new POVButton(operator, 180);
        }
    }
    public static class Drivebase {
        public static class Climber {
            public static IButton activateClimb = new MultiButton(operator, new int[] {5,6}, IButton.ButtonType.Toggle, MultiButton.MultiButtonType.BothButton);
        }
        public static class DriveSystem {
            public static IButton driveConfig = new Button(driver, 7, IButton.ButtonType.Toggle);
            public static IButton shift = new Button(driver, 1, IButton.ButtonType.Toggle);
        }
    }
}