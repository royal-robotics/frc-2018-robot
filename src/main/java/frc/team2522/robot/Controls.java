package frc.team2522.robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.team2522.robot.libs.*;

public class Controls {
    public static Joystick driver = new Joystick(0);
    private static boolean onePersonMode = true;

    public static class Elevator {
        public static class Intake {
            public static Button pickup = new Button(driver, 1, Button.ButtonType.Toggle); // A Button
            public static Button closed = new Button(driver, 2, Button.ButtonType.Toggle); // B Button
            public static Button open = new Button(driver, 3, Button.ButtonType.Toggle); // X Button

            //If both of these buttons are on rotate runs (we should make a multi-button that handles this)
            public static IButton pullCube = new Axis(driver, 2, 0.1); // Left Trigger
            public static IButton pushCube = new Axis(driver, 3, 0.1); // Right Trigger
        }
        public static class Lift {
            public static IButton liftUp = new POVButton(driver, 0);
            public static IButton liftdown = new POVButton(driver, 180);
        }
    }
    public static class Drivebase {
        public static class Climber {
            public static IButton activateClimb = new MultiButton(driver, new int[] {5,6}, IButton.ButtonType.Toggle, MultiButton.MultiButtonType.BothButton);
        }
        public static class DriveSystem {
            public static Button driveConfig = new Button(driver, 7, IButton.ButtonType.Toggle);
            public static IButton shift = new MultiButton(driver, new int [] {9,10}, IButton.ButtonType.Toggle, MultiButton.MultiButtonType.EitherButton);
        }
    }
}