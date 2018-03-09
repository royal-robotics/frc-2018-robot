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

        public int id;
        Buttons(int id) {
            this.id = id;
        }
    }

    public enum Axes {
        LeftX (0),
        LeftY (1),
        LeftTrigger (2),
        RightTrigger (3),
        RightX (4),
        RightY (5);

        public int id;
        Axes(int id) {
            this.id = id;
        }
    }

    public static Joystick driver = new Joystick(0);
    public static Joystick operator = new Joystick (1);


    private static Axis drive;
    private static Axis turn;
    private static Axis tankRightDrive;

    private static IButton pickup;
    private static IButton closed;
    private static IButton open;

    private static IButton pullCube;
    private static IButton pushCube;
    private static IButton rotateCube;

    private static Axis liftAxis;

    private static IButton calibrate;
    private static IButton moveLift;

    private static IButton activateClimb;

    private static IButton driveConfig;
    private static IButton shift;


    public static double driveValue = 0.0;
    public static double turnValue = 0.0;
    public static double tankRightDriveValue = 0.0;

    public static boolean pickupPressed = false;
    public static boolean closedPressed = false;
    public static boolean openPressed = false;

    public static boolean pullCubePressed = false;
    public static boolean pushCubePressed = false;
    public static boolean rotateCubePressed = false;

    public static boolean liftAxisPressed = false;
    public static boolean calibratePressed = false;
    public static boolean moveliftPressed = false;
    public static boolean activateClimbPressed = false;
    public static boolean driveConfigPressed = false;
    public static boolean shiftPressed = false;
    public static double liftAxisValue = 0.0;

    private static boolean onePersonMode = true;

    static {
        drive = new Axis(driver, Axes.LeftY, 0.1);
        turn = new Axis(driver, Axes.RightX, 0.1);
        tankRightDrive = new Axis(driver, Axes.RightY, 0.1);

        pickup = new Button(operator, Buttons.A, IButton.ButtonType.Toggle);
        closed = new Button(operator, Buttons.B, IButton.ButtonType.Toggle);
        open = new Button(operator, Buttons.X, IButton.ButtonType.Toggle);

        pullCube = new Axis(operator, Axes.LeftTrigger,0.1);
        pushCube = new Axis(operator, Axes.RightTrigger,0.1);
        rotateCube = new MultiButton(new IButton[] {pullCube, pushCube}, IButton.ButtonType.Hold,
                                     MultiButton.MultiButtonType.BothButton);

        liftAxis = new Axis(operator, Axes.LeftY,0.1);

        calibrate = new Button(operator, Buttons.Y, IButton.ButtonType.Toggle);
        moveLift = new Button(driver, Buttons.Y, IButton.ButtonType.Hold);

        activateClimb = new MultiButton(operator, new Buttons[] {Buttons.LeftBumper, Buttons.RightBumper},
                IButton.ButtonType.Toggle, MultiButton.MultiButtonType.BothButton);

        driveConfig = new Button(driver, Buttons.Back, IButton.ButtonType.Toggle);
        shift = new Button(driver, Buttons.A, IButton.ButtonType.Toggle);
    }

    public static void readController() {
        driveValue = drive.getValue();
        turnValue = turn.getValue();
        tankRightDriveValue = tankRightDrive.getValue();

        pickupPressed = pickup.isPressed();
        closedPressed = closed.isPressed();
        openPressed = open.isPressed();

        pullCubePressed = pullCube.isPressed();
        pushCubePressed = pushCube.isPressed();
        rotateCubePressed = rotateCube.isPressed();

        liftAxisPressed = liftAxis.isPressed();

        calibratePressed = calibrate.isPressed();
        moveliftPressed = moveLift.isPressed();

        activateClimbPressed = activateClimb.isPressed();

        driveConfigPressed = driveConfig.isPressed();
        shiftPressed = shift.isPressed();
        liftAxisValue = liftAxis.getValue();
    }
}
