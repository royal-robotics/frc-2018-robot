package frc.team2522.robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.team2522.robot.libs.*;

public class Controls {
    public enum Buttons {
        A (1),
        B (2),
        X (3),
        Y (4),
        LeftBumper (5),
        RightBumper (6),
        Back (7),
        Start (8),
        LeftStickPress (9),
        RightStickPress (10);

        private int id;
        Buttons(int id) {
            this.id = id;
        }
    }

    public enum Axes {
        //LeftX (0),
        //LeftY (1),
        //LeftTrigger (2),

    }
    public static Joystick driver = new Joystick(0);
    public static Joystick operator = new Joystick (1);

    private static IButton pickup;
    private static IButton closed;
    private static IButton open;
    private static IButton pullCube;
    private static IButton pushCube;
    private static Axis liftAxis;
    private static IButton calibrate;
    private static IButton moveLift;
    private static IButton activateClimb;
    private static  IButton driveConfig;
    private static IButton shift;
    public static boolean pickupPressed = false;
    public static boolean closedPressed = false;
    public static boolean openPressed = false;
    public static boolean pullCubePressed = false;
    public static boolean pushCubePressed = false;
    public static boolean liftAxisPressed = false;
    public static boolean calibratePressed = false;
    public static boolean moveliftPressed = false;
    public static boolean activateClimbPressed = false;
    public static boolean driveConfigPressed = false;
    public static boolean shiftPressed = false;
    public static double liftAxisValue = 0;

    private static boolean onePersonMode = true;

    public static void initialize(boolean onePersonMode) {
        Controls.onePersonMode = onePersonMode;
        pickup = new Button(operator,1, IButton.ButtonType.Toggle);
        closed = new Button(operator,2, IButton.ButtonType.Toggle);
        open = new Button(operator, 3, IButton.ButtonType.Toggle);
        pullCube = new Axis(operator,2,0.1);
        pushCube = new Axis(operator,3,0.1);
        liftAxis = new Axis(operator, 1,0.1);
        calibrate = new Button(operator,4, IButton.ButtonType.Toggle);
        moveLift = new Button(driver,4, IButton.ButtonType.Hold);
        activateClimb = new MultiButton(operator,new int[] {5,6}, IButton.ButtonType.Toggle, MultiButton.MultiButtonType.BothButton);
        driveConfig = new Button(driver, 7, IButton.ButtonType.Toggle);
        shift = new Button(driver, 1, IButton.ButtonType.Toggle);
    }

    public static void readController() {
        pickupPressed = pickup.isPressed();
        closedPressed = closed.isPressed();
        openPressed = open.isPressed();
        pullCubePressed = pullCube.isPressed();
        pushCubePressed = pushCube.isPressed();
        liftAxisPressed = liftAxis.isPressed();
        calibratePressed = calibrate.isPressed();
        moveliftPressed = moveLift.isPressed();
        activateClimbPressed = activateClimb.isPressed();
        driveConfigPressed = driveConfig.isPressed();
        shiftPressed = shift.isPressed();
        liftAxisValue = liftAxis.getValue();

    }
}