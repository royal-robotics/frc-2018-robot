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
        AutoDrivePath driveToSwitchAndLift = new AutoDrivePath(robot.driveController, pathName);
        //driveToSwitchAndLift.addChildStep(0, new AutoLift(robot.elevatorController, robot.elevatorController.lift.getPosition() + 10));
        steps.add(driveToSwitchAndLift);
        steps.add(new AutoSpit(robot.elevatorController));

        pathName = Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT
                ? "center-switch_left_back_to_center"
                : "center-switch_right_back_to_center";
        AutoDrivePath driveBackToCenter = new AutoDrivePath(robot.driveController, pathName, true);
        driveBackToCenter.addChildStep(10, new AutoLift(robot.elevatorController, robot.elevatorController.lift.getPosition() - 25));
        steps.add(driveBackToCenter);
        steps.add(new AutoSpit(robot.elevatorController));

        AutoDrivePath driveForwardAndCollect = new AutoDrivePath(robot.driveController, "center-switch_forward_and_collect");
        driveForwardAndCollect.addChildStep(0, new AutoSpit(robot.elevatorController, 1.50, -1.0));
        steps.add(driveForwardAndCollect);

        AutoDrivePath driveBackAndLift = new AutoDrivePath(robot.driveController, "center-switch_backward_and_lift", true);
        driveBackAndLift.addChildStep(0, new AutoLift(robot.elevatorController, 40));
        steps.add(driveBackAndLift);

        steps.add(new AutoDrivePath(robot.driveController, "center-switch_forward_and_spit"));
        steps.add(new AutoSpit(robot.elevatorController));

        return new AutoManager(steps);
    }

    public static AutoManager Left_ScaleOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SCALE) == MatchData.OwnedSide.LEFT) {
            AutoDriveAndLift step = new AutoDriveAndLift(robot.driveController, "left-scale_left", robot.elevatorController);
            step.AddLiftMove(100, 76);
            steps.add(step);

            steps.add(new AutoSpit(robot.elevatorController));
        }
        else {
            steps.add(new AutoIntakeArms(robot.elevatorController, AutoIntakeArms.ArmPosition.pickup));

            AutoDriveAndLift step = new AutoDriveAndLift(robot.driveController, "left-scale_right", robot.elevatorController);
            step.AddLiftMove(320, 76);
            steps.add(step);

            steps.add(new AutoSpit(robot.elevatorController));
        }

        return new AutoManager(steps);
    }

    public static AutoManager Right_ScaleOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SCALE) == MatchData.OwnedSide.RIGHT) {
            AutoDriveAndLift step = new AutoDriveAndLift(robot.driveController, "right-scale_right", robot.elevatorController);
            step.AddLiftMove(100, 76);
            steps.add(step);

            steps.add(new AutoSpit(robot.elevatorController));
        }
        else {
            steps.add(new AutoIntakeArms(robot.elevatorController, AutoIntakeArms.ArmPosition.pickup));

            AutoDriveAndLift step = new AutoDriveAndLift(robot.driveController, "right-scale_left", robot.elevatorController);
            step.AddLiftMove(320, 76);
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

