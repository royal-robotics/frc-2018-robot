package frc.team2522.robot.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.Controls;
import frc.team2522.robot.Robot;
import frc.team2522.robot.autonomous.BuildingBlocks.*;
import openrio.powerup.MatchData;
import org.json.simple.JSONArray;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoRoutines {


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
        return new AutoManager(new ArrayList<>());
    }

    public static AutoManager DriveForward(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();
        steps.add(new AutoDrive(robot.driveController, 100));
        return new AutoManager(steps);
    }

    public static AutoManager Center_SwitchOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        String pathName = Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT
                ? "center-switch_left"
                : "center-switch_right";
        AutoDrivePath driveToSwitchFromStart = new AutoDrivePath(robot.driveController, pathName);
        driveToSwitchFromStart.addChildStep(120, new AutoSpit(robot.elevatorController));
        steps.add(driveToSwitchFromStart);

        pathName = Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT
                ? "center-switch_left_back_to_center"
                : "center-switch_right_back_to_center";
        steps.add(new AutoDrivePath(robot.driveController, pathName, true));
        steps.add(new AutoLift(robot.elevatorController, 3));

        AutoDrivePath driveForwardAndCollect = new AutoDrivePath(robot.driveController, "center-switch_forward_and_collect");
        driveForwardAndCollect.addChildStep(0, new AutoSpit(robot.elevatorController, 1.50, -1.0));
        steps.add(driveForwardAndCollect);

        steps.add(new AutoDrivePath(robot.driveController, "center-switch_backward_and_lift", true));
        steps.add(new AutoLift(robot.elevatorController, 30));

        pathName = Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT
                ? "center-switch_left_forward_and_spit"
                : "center-switch_right_forward_and_spit";
        steps.add(new AutoDrivePath(robot.driveController, pathName));
        steps.add(new AutoSpit(robot.elevatorController));

//        pathName = Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT
//                ? "center-switch_left_back_to_45"
//                : "center-switch_right_back_to_45";
//        steps.add(new AutoDrivePath(robot.driveController, pathName, true));
//        steps.add(new AutoLift(robot.elevatorController, 3));

//        pathName = Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT
//                ? "center-switch_left_forward45_and_collect"
//                : "center-switch_right_forward45_and_collect";
//        AutoDrivePath driveForward45AndCollect = new AutoDrivePath(robot.driveController, pathName);
//        driveForward45AndCollect.addChildStep(0, new AutoSpit(robot.elevatorController, 1.50, -1.0));
//        steps.add(driveForward45AndCollect);
//
//        pathName = Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT
//                ? "center-switch_left_back45_and_lift"
//                : "center-switch_right_back45_and_lift";
//        AutoDrivePath driveBasck45AndLift = new AutoDrivePath(robot.driveController, pathName, true);
//        driveBasck45AndLift.addChildStep(0, new AutoLift(robot.elevatorController, 40));
//        steps.add(driveBasck45AndLift);

        return new AutoManager(steps);
    }

    public static AutoManager Left_ScaleOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SCALE) == MatchData.OwnedSide.LEFT) {
            AutoDriveAndLift step = new AutoDriveAndLift(robot.driveController, "left-scale_left", robot.elevatorController);
            step.AddLiftMove(100, 73);
            steps.add(step);

            steps.add(new AutoSpit(robot.elevatorController));
        }
        else {
            AutoDriveAndLift step = new AutoDriveAndLift(robot.driveController, "left-scale_right", robot.elevatorController);
            step.AddLiftMove(320, 73);
            steps.add(step);

            steps.add(new AutoSpit(robot.elevatorController));
        }

        return new AutoManager(steps);
    }

    public static AutoManager Right_ScaleOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SCALE) == MatchData.OwnedSide.RIGHT) {
            AutoDriveAndLift step = new AutoDriveAndLift(robot.driveController, "right-scale_right", robot.elevatorController);
            step.AddLiftMove(100, 73);
            steps.add(step);

            steps.add(new AutoSpit(robot.elevatorController));

//            steps.add(new AutoDrive(robot.driveController, -25));
//
//            steps.add(new AutoRotate(robot.driveController,255));
//
//            steps.add(new AutoLift(robot.elevatorController, 2));
//
//            AutoDrivePath driveForwardPickUp = new AutoDrivePath(robot.driveController, 26);
//            driveForwardPickUp.addChildStep(5, new AutoSpit(robot.elevatorController,.75,-1));
//            steps.add(driveForwardPickUp);
//
//            steps.add(new AutoLift(robot.elevatorController, 73));
//
//            steps.add(new AutoRotate(robot.driveController, -190));
//
//            steps.add(new AutoDrive(robot.driveController, 60));
//
//            steps.add(new AutoSpit(robot.elevatorController));
        }
        else {
            AutoDriveAndLift step = new AutoDriveAndLift(robot.driveController, "right-scale_left", robot.elevatorController);
            step.AddLiftMove(320, 73);
            steps.add(step);

            steps.add(new AutoSpit(robot.elevatorController));
        }

        return new AutoManager(steps);
    }

    public static AutoManager Left_SwitchOrScale(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT) {
            steps.add(new AutoDrivePath(robot.driveController, "left-switch_left"));
            steps.add(new AutoSpit(robot.elevatorController));
//            steps.add(new AutoDrive(robot.driveController, 155));
//            steps.add(new AutoRotate(robot.driveController, 90));
//            steps.add(new AutoDrive(robot.driveController, 25.0));
//            steps.add(new AutoSpit(robot.elevatorController));
        } else {
            return Left_ScaleOnly(robot);
        }

        return new AutoManager(steps);
    }

    public static AutoManager Right_SwitchOrScale(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.RIGHT) {
            steps.add(new AutoDrivePath(robot.driveController, "right-switch_right"));
            steps.add(new AutoSpit(robot.elevatorController));
//            steps.add(new AutoDrive(robot.driveController, 155));
//            steps.add(new AutoRotate(robot.driveController, -90));
//            steps.add(new AutoDrive(robot.driveController, 25.0));
//            steps.add(new AutoSpit(robot.elevatorController));
        } else {
            return Right_ScaleOnly(robot);
        }

        return new AutoManager(steps);
    }
}

