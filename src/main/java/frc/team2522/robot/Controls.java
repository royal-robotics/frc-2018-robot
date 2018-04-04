package frc.team2522.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.libs.*;
import openrio.powerup.MatchData;

public class Controls {

    private static Joystick driver = new Joystick(0);
    private static Joystick operator = new Joystick (1);
    private static Joystick autoSelector = new Joystick(2);


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
            public static boolean pushCubeSoft() { return pushCubeSoft; }
            public static boolean pushCubeHard() { return pushCubeHard; }
            public static boolean armUp() { return armUp; }
            public static boolean armOut() { return armOut; }
            public static double getArmAxisValue() { return -armAxis.getValue();}
        }

        public static class Lift {
            public static double getLiftAxisValue() {
                double power = -liftAxis.getValue();
                power *= power > 0 ? 0.6 : 0.3;
                return power;
            }
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
        SmartDashboard.putBoolean("Debugging", true);
        SmartDashboard.putNumber("MoveDistance", 60.0);
    }

    public static double getMoveDistance() {
        return SmartDashboard.getNumber("MoveDistance", 60.0);
    }

    public static void updateControls() {
        DebugMode = SmartDashboard.getBoolean("Debugging", true);

        armUp = armUpButton.isPressed();
        pullCube = pullCubeButton.isPressed();
        pushCube = pushCubeButton.isPressed();
        pushCubeSoft = pushCubeSoftButton.isPressed();
        pushCubeHard = pushCubeHardButton.isPressed();

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

        updateAutoMode();
    }

    /**
     *
     */
    private static String autoMode = null;
    public static String getAutoMode() {
        return autoMode;
    }

    /**
     *
     */
    private static void updateAutoMode() {
        String newMode;

        switch(getFieldStartPosition()) {
            case 10: {  // LEFT
                switch (getAutoRoutineId()) {
                    case 1: {
                        newMode = "Left_ScaleOnly";
                        break;
                    }
                    case 2: {
                        newMode = "Left_NearScale";
                        break;
                    }
                    case 3: {
                        newMode = "Left_SwitchOnly";
                        break;
                    }
                    case 4: {
                        newMode = "Left_SwitchOrScale";
                        break;
                    }
                    case 5: {
                        newMode = "Left_SwitchOrNearScale";
                        break;
                    }
                    case 10: {
                        newMode = "DriveForward";
                        break;
                    }
                    default: {
                        newMode = "NoRoutine";
                        break;
                    }
                }
                break;
            }
            case 1: {   // CENTER
                switch (getAutoRoutineId()) {
                    case 1: {
                        newMode = "Center_SwitchOnly";
                        break;
                    }
                    default: {
                        newMode = "NoRoutine";
                        break;
                    }
                }
                break;
            }
            case 2: {   // RIGHT
                switch (getAutoRoutineId()) {
                    case 1: {
                        newMode = "Right_ScaleOnly";
                        break;
                    }
                    case 2: {
                        newMode = "Right_NearScale";
                        break;
                    }
                    case 3: {
                        newMode = "Right_SwitchOnly";
                        break;
                    }
                    case 4: {
                        newMode = "Right_SwitchOrScale";
                        break;
                    }
                    case 5: {
                        newMode = "Right_SwitchOrNearScale";
                        break;
                    }
                    case 10: {
                        newMode = "DriveForward";
                        break;
                    }
                    default: {
                        newMode = "NoRoutine";
                        break;
                    }
                }
                break;
            }
            default: {
                newMode = "NoRoutine";
                break;
            }
        }


        if (newMode != autoMode) {
            autoMode = newMode;
            SmartDashboard.putString("AutoRoutine", autoMode);
        }
    }

    /**
     * Return the result of the robot field start position selector.
     *
     *  For 2018 the positions are as follows:
     *
     *  	1: Robot left bumper next to right side of exchange zone tape and back bumper against station wall
     *  	2: Robot right bumper against right edge of back diamond plate and back bumper against station wall
     *  	10: Robot left bumper against left edge of back diamond plate and back bumper against station wall
     *
     * @return The id of the field starting position.
     */
    public static int getFieldStartPosition()

    {
        int result = 0;

        result += autoSelector.getRawButton(2) ? 1 : 0;
        result += autoSelector.getRawButton(3) ? 2 : 0;
        result += autoSelector.getRawButton(4) ? 4 : 0;
        result += autoSelector.getRawButton(5) ? 8 : 0;

        return result + 1;
    }

    /**
     *
     *
     * @return
     */
    public static int getAutoRoutineId()

    {
        int autoValue = 0;

        autoValue += autoSelector.getRawButton(13) ? 1 : 0;
        autoValue += autoSelector.getRawButton(14) ? 2 : 0;
        autoValue += autoSelector.getRawButton(15) ? 4 : 0;
        autoValue += autoSelector.getRawButton(16) ? 8 : 0;

        return autoValue + 1;
    }

    public static MatchData.OwnedSide getOwnedSide(MatchData.GameFeature feature) {
        MatchData.OwnedSide side = MatchData.getOwnedSide(feature);

        if(side == MatchData.OwnedSide.UNKNOWN) {
            System.out.println("MatchData UNKNOWN setting value based on AutoSelect Switches.");
            if(feature == MatchData.GameFeature.SCALE) {
                side = autoSelector.getRawButton(1) ? MatchData.OwnedSide.RIGHT : MatchData.OwnedSide.LEFT;
            } else {
                side = autoSelector.getRawButton(12) ? MatchData.OwnedSide.RIGHT : MatchData.OwnedSide.LEFT;
            }
        }
        else {
            System.out.println("Match data returned from FMS.");
        }

        if (feature == MatchData.GameFeature.SCALE) {
            System.out.print("SCALE is on ");
        }
        else {
            System.out.print("SWITCH is on ");
        }

        if (side == MatchData.OwnedSide.LEFT) {
            System.out.println(" LEFT.");
        }
        else {
            System.out.println(" RIGHT.");
        }

        return side;
    }


    // Driver Joystick Configuration
    //
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
    private static IButton armUpButton = new Button(operator,Logitech310Button.A, IButton.ButtonType.Hold);
    private static IButton pushCubeHardButton = new Button(operator, Logitech310Button.B, IButton.ButtonType.Hold);
    private static IButton pushCubeSoftButton = new Button(operator, Logitech310Button.X, IButton.ButtonType.Hold);
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
    public static Axis armAxis = new Axis(operator, Logitech310Axis.RightStickY, 0.1);

    private static IButton pullCubeButton = new Axis(operator,Logitech310Axis.LeftTrigger, 0.1);
    private static IButton pushCubeButton = new Axis(operator,Logitech310Axis.RightTrigger,0.1);

    //
    //
    private static boolean DebugMode = true;

    private static DriveType driveType = DriveType.DiffDrive;
    private static boolean isHighGear = true;

    private static boolean pushCube = false;
    private static boolean pushCubeSoft = false;
    private static boolean pushCubeHard = false;
    private static boolean pullCube = false;
    private static boolean armOut = false;
    private static boolean armUp = false;

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