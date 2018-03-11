package frc.team2522.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.libs.*;

public class Controls {
    public enum Logitech310Button {
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
        Logitech310Button(int id) {
            this.id = id;
        }
    }
    public enum Logitech310Axis {
        LeftStickX (0),
        LeftStickY (1),
        LeftTrigger (2),
        RightTrigger (3),
        RightStickX (4),
        RightStickY (5);
        public int id;
        Logitech310Axis(int id) {
            this.id = id;
        }

    }

    public static class DriveSystem {
        public static DriveType getDriveType() { return driveType; }
        public static boolean isHighGear() { return isHighGear; }
        public static class DiffDrive {
            public static double getThrottleValue() { return -throttle.getValue();}
            public static double getTurnValue() { return turn.getValue();}
            public static double turnDampener() { return (3.0 / 4.0);}
        }
        public static class TankDrive {
            public static double getLeftThrottleValue() { return -leftThrottle.getValue(); }
            public static double getRightThrottleValue() { return -rightThrottle.getValue(); }
        }
    }

    public static class Elevator {
        public static class Intake {
            public static boolean pullCube() { return pullCube; }
            public static boolean pushCube() { return pushCube; }
            public static boolean rotateCube() { return (pushCube() && pullCube()); }
            public static boolean armsOpen() { return armsOpen; }
            public static boolean armsClose() { return armsClose; }
        }

        public static class Lift {
            public static double getLiftAxisValue() { return -liftAxis.getValue(); }
            public static boolean getLiftAxisOn() { return liftAxis.isPressed(); }

            public static boolean moveBottom() { return moveBottom; }
            public static boolean moveSwitch() { return moveSwitch; }
            public static boolean moveScale() { return moveScale; }
            public static boolean moveClimb() { return moveClimb; }

            public static boolean startCalibration() { return calibrate; }
            public static boolean debugMoveLift() { return debugMoveLift;}
        }
    }

    public static boolean inClimberMode() {return inClimberMode; }

    public static boolean debugDriveForward() { return debugDriveForward && DebugMode; }

    public static boolean showTargets() { return showTargets; }


    public static void initialize() {
        SmartDashboard.putBoolean("Controls/Debugging", true);
        SmartDashboard.putNumber("Controls/MoveDistance", 60.0);
    }

    public static double getMoveDistance() {
        return SmartDashboard.getNumber("Controls/MoveDistance", 60.0);
    }

    public static void updateControls() {
        DebugMode = SmartDashboard.getBoolean("Controls/Debugging", true);

        armsClose = armsCloseButton.isPressed();
        armsOpen = armsOpenButton.isPressed();
        pullCube = pullCubeButton.isPressed();
        pushCube = pushCubeButton.isPressed();

        moveBottom = moveBottomButton.isPressed();
        moveSwitch = moveSwitchButton.isPressed();
        moveScale = moveScaleButton.isPressed();
        moveClimb = moveClimbButton.isPressed();

        debugDriveForward = debugDriveForwardButton.isPressed();
        showTargets = showTargetsButton.isPressed();
        calibrate = calibrateButton.isPressed();
        debugMoveLift = moveLiftButton.isPressed() && DebugMode;


        if (toggleClimberStateButton.isPressed()) {
            inClimberMode = ! inClimberMode;
        }

        if (toggleDrive.isPressed()) {
            if (driveType == DriveType.DiffDrive) {
                driveType = DriveType.TankDrive;
            }
            else {
                driveType = DriveType.DiffDrive;
            }
        }

        if (shiftToggleButton.isPressed()) {
            isHighGear = !isHighGear;
        }

        SmartDashboard.putBoolean("Controls/ClimberEnabled", inClimberMode());
    }

    // Driver Joystick Configuration
    //
    private static Joystick driver = new Joystick(0);

    private static IButton shiftToggleButton = new Button(driver, Logitech310Button.A, IButton.ButtonType.Toggle);
    private static IButton moveLiftButton = new Button(driver,Logitech310Button.B, IButton.ButtonType.Hold);
    private static IButton showTargetsButton = new Button(driver, Logitech310Button.X, IButton.ButtonType.Hold);
    private static IButton debugDriveForwardButton = new Button(driver, Logitech310Button.Y, IButton.ButtonType.Hold);
    private static IButton toggleDrive = new Button(driver, Logitech310Button.Back, IButton.ButtonType.Toggle);

    // Tank Drive
    private static Axis leftThrottle = new Axis(driver, Logitech310Axis.LeftStickY, 0.1);
    private static Axis rightThrottle = new Axis(driver, Logitech310Axis.RightStickY, 0.1);

    // Diff Drive
    private static Axis throttle = new Axis(driver, Logitech310Axis.LeftStickY, 0.1);
    private static Axis turn = new Axis(driver, Logitech310Axis.RightStickX, 0.1);


    // Operator Joystick Configuration
    //
    private static Joystick operator = new Joystick (1);
    private static IButton armsCloseButton = new Button(operator,Logitech310Button.A, IButton.ButtonType.Hold);
    private static IButton armsOpenButton = new Button(operator,Logitech310Button.B, IButton.ButtonType.Hold);
    // X is not used
    private static IButton calibrateButton = new Button(operator,Logitech310Button.Y, IButton.ButtonType.Toggle);

    private static IButton toggleClimberStateButton = new MultiButton(operator,
            new Logitech310Button[] {Logitech310Button.LeftBumper, Logitech310Button.RightBumper},
            IButton.ButtonType.Toggle,
            MultiButton.MultiButtonType.BothButton);

    public static IButton moveBottomButton = new POVButton(operator, 180);
    public static IButton moveSwitchButton = new POVButton(operator, 90);
    public static IButton moveScaleButton = new POVButton(operator, 0);
    public static IButton moveClimbButton = new POVButton(operator, 270);

    public static Axis liftAxis = new Axis(operator, Logitech310Axis.LeftStickY, 0.1);

    private static IButton pullCubeButton = new Axis(operator,Logitech310Axis.LeftTrigger, 0.1);
    private static IButton pushCubeButton = new Axis(operator,Logitech310Axis.RightTrigger,0.1);

    //
    //
    private static boolean DebugMode = true;

    private static DriveType driveType = DriveType.DiffDrive;
    private static boolean isHighGear = true;

    private static boolean pushCube = false;
    private static boolean pullCube = false;
    private static boolean armsClose = false;
    private static boolean armsOpen = false;

    private static boolean moveScale = false;
    private static boolean moveSwitch = false;
    private static boolean moveBottom = false;
    private static boolean moveClimb = false;

    private static boolean inClimberMode = false;

    private static boolean calibrate = false;
    private static boolean debugMoveLift = false;

    private static boolean debugDriveForward = false;
    private static boolean showTargets = false;
}