package frc.team2522.robot.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.Controls;
import frc.team2522.robot.Robot;
import frc.team2522.robot.autonomous.BuildingBlocks.*;
import openrio.powerup.MatchData;
import org.json.simple.JSONArray;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class AutoRoutines {

    public static MatchData.OwnedSide getOwnedSide(MatchData.GameFeature feature) {
        MatchData.OwnedSide side = MatchData.getOwnedSide(feature);

        if(side == MatchData.OwnedSide.UNKNOWN)
            return MatchData.OwnedSide.LEFT; //Side will always return left

        return side;
    }

    public static void writeRoutinesToDashboard() {
        Method[] methods = AutoRoutines.class.getDeclaredMethods();

        JSONArray routines = new JSONArray();
        for (Method method : methods) {
            Parameter[] parameters = method.getParameters();
            if (parameters.length == 1 && parameters[0].getType() == Robot.class)
                routines.add(method.getName());
        }

        //System.out.println(routines.toJSONString());
        SmartDashboard.putString("AutoRoutines/RoutinesList", routines.toJSONString());
    }

    private static String autoModeLast = null;
    public static String getAutoMode() {
        String autoMode = Controls.getManualAutoMode();
        if(autoMode == null)
            autoMode = SmartDashboard.getString("AutoRoutines/SelectedRoutine", "");

        if(autoMode != autoModeLast)
            System.out.println("Auto Mode: " + autoMode);

        autoModeLast = autoMode;
        return autoMode;
    }

    public static AutoManager selectAutoMode(String autoName, Robot robot) {
        try {
            System.out.println("AutoName: " + autoName);
            return (AutoManager) AutoRoutines.class.getMethod(autoName, Robot.class).invoke(null, robot);
        } catch (Exception ex) {
            System.out.println("Failed to select Auto mode.");
            System.out.println(ex.getMessage());
            System.out.println(ex.fillInStackTrace());
            return NoRoutine(robot);
        }
    }

    public static AutoManager NoRoutine(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();
        steps.add(new AutoDrive(robot.driveController, 100));
        return new AutoManager(steps);
    }

    public static AutoManager Test_MoveLiftDown(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();
        steps.add(new AutoIntakeArms(robot.elevatorController, AutoIntakeArms.ArmPosition.pickup));
        steps.add(new AutoLift(robot.elevatorController, 12));

        return new AutoManager(steps);
    }

    public static AutoManager Center_SwitchOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();
        if (AutoRoutines.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.RIGHT) {
            System.out.println("GetOwnedSide is RIGHT");
            steps.add(new AutoDrivePath(robot.driveController, "center-right_side_switch"));
        } else {
            System.out.println("GetOwnedSide is LEFT");
            steps.add(new AutoDrivePath(robot.driveController, "center-left_side_switch"));
        }
        steps.add(new AutoSpit(robot.elevatorController));

        return new AutoManager(steps);
    }

    public static AutoManager Left_OnlyScale(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (AutoRoutines.getOwnedSide(MatchData.GameFeature.SCALE) == MatchData.OwnedSide.LEFT) {
            steps.add(new AutoDriveAndLift(robot.driveController, "left-scale_left", robot.elevatorController, 80, 100));
            steps.add(new AutoSpit(robot.elevatorController));
        } else {
            steps.add(new AutoIntakeArms(robot.elevatorController, AutoIntakeArms.ArmPosition.pickup));
            steps.add(new AutoDriveAndLift(robot.driveController, "left-scale_right", robot.elevatorController, 12, 0));
            steps.add(new AutoRotate(robot.driveController, -120));
            steps.add(new AutoLift(robot.elevatorController, 80.0));
            steps.add(new AutoDrive(robot.driveController, 30));
            steps.add(new AutoSpit(robot.elevatorController));
//            steps.add(new AutoIntakeArms(robot.elevatorController, AutoIntakeArms.ArmPosition.pickup));
//            steps.add(new AutoDriveAndLift(robot.driveController, 226, robot.elevatorController, 12, 0));
//            steps.add(new AutoRotate(robot.driveController, 93));
//            steps.add(new AutoDriveAndLift(robot.driveController, 220, robot.elevatorController, 80, 100));
//            steps.add(new AutoRotate(robot.driveController, 60));
//            steps.add(new AutoDrive(robot.driveController, 48));
//            steps.add(new AutoSpit(robot.elevatorController));
        }

        return new AutoManager(steps);
    }

    public static AutoManager Right_OnlyScale(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (AutoRoutines.getOwnedSide(MatchData.GameFeature.SCALE) == MatchData.OwnedSide.RIGHT) {
            steps.add(new AutoDriveAndLift(robot.driveController, "right-scale_right", robot.elevatorController, 80, 100));
            steps.add(new AutoSpit(robot.elevatorController));
        } else {
            steps.add(new AutoIntakeArms(robot.elevatorController, AutoIntakeArms.ArmPosition.pickup));
            steps.add(new AutoDriveAndLift(robot.driveController, "right-scale_left", robot.elevatorController, 12, 0));
            steps.add(new AutoRotate(robot.driveController, 120));
            steps.add(new AutoLift(robot.elevatorController, 80.0));
            steps.add(new AutoDrive(robot.driveController, 30));
            steps.add(new AutoSpit(robot.elevatorController));
//            steps.add(new AutoIntakeArms(robot.elevatorController, AutoIntakeArms.ArmPosition.pickup));
//            steps.add(new AutoDriveAndLift(robot.driveController, 226, robot.elevatorController, 12, 0));
//            steps.add(new AutoRotate(robot.driveController, -93));
//            steps.add(new AutoDriveAndLift(robot.driveController, 220, robot.elevatorController, 80, 100));
//            steps.add(new AutoRotate(robot.driveController, 120));
//            steps.add(new AutoDrive(robot.driveController, 48));
//            steps.add(new AutoSpit(robot.elevatorController));
        }

        return new AutoManager(steps);
    }

    public static AutoManager Left_SwitchOrScale(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (AutoRoutines.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT) {
            steps.add(new AutoDrive(robot.driveController, 155));
            steps.add(new AutoRotate(robot.driveController, 90));
            steps.add(new AutoDrive(robot.driveController, 25.0));
            steps.add(new AutoSpit(robot.elevatorController));
        } else {
            return Left_OnlyScale(robot);
        }

        return new AutoManager(steps);
    }

    public static AutoManager Right_SwitchOrScale(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (AutoRoutines.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.RIGHT) {
            steps.add(new AutoDrive(robot.driveController, 155));
            steps.add(new AutoRotate(robot.driveController, -90));
            steps.add(new AutoDrive(robot.driveController, 25.0));
            steps.add(new AutoSpit(robot.elevatorController));
        } else {
            return Right_OnlyScale(robot);
        }

        return new AutoManager(steps);
    }
}

