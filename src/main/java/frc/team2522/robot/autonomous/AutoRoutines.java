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
        steps.add(new AutoDrivePath(robot.driveController, 100));
        return new AutoManager(steps);
    }

    public static AutoManager Center_SwitchOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        String pathName = Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT
                ? "center-switch_left"
                : "center-switch_right";
        AutoDrivePath driveToSwitchFromStart = new AutoDrivePath(robot.driveController, pathName);
        driveToSwitchFromStart.addChildStep(0, new AutoIntakeArm(robot.elevatorController));
        driveToSwitchFromStart.addChildStep(115, new AutoIntakeWheels(robot.elevatorController));
        steps.add(driveToSwitchFromStart);

        pathName = Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT
                ? "center-switch_left_back_to_center"
                : "center-switch_right_back_to_center";
        steps.add(new AutoDrivePath(robot.driveController, pathName, true));
        steps.add(new AutoLift(robot.elevatorController, 1.5));

        AutoDrivePath driveForwardAndCollect = new AutoDrivePath(robot.driveController, "center-switch_forward_and_collect");
        driveForwardAndCollect.addChildStep(0, new AutoIntakeWheels(robot.elevatorController, 1.50, -1.0));
        steps.add(driveForwardAndCollect);

        steps.add(new AutoDrivePath(robot.driveController, "center-switch_backward_and_lift", true));
        steps.add(new AutoLift(robot.elevatorController, 23));

        pathName = Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT
                ? "center-switch_left_forward_and_spit"
                : "center-switch_right_forward_and_spit";
        AutoDrivePath driveToSwitchWithSecondCube = new AutoDrivePath(robot.driveController, pathName);
        driveToSwitchWithSecondCube.addChildStep(89, new AutoIntakeWheels(robot.elevatorController));
        steps.add(driveToSwitchWithSecondCube);

        return new AutoManager(steps);
    }

    public static AutoManager Left_SwitchOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT) {
            AutoDrivePath driveToNearSwitch = new AutoDrivePath(robot.driveController, "left-switch_left");
            driveToNearSwitch.addChildStep(0, new AutoIntakeArm(robot.elevatorController));
            steps.add(driveToNearSwitch);

            steps.add(new AutoIntakeWheels(robot.elevatorController));
        } else {
            return DriveForward(robot);
        }

        return new AutoManager(steps);
    }

    public static AutoManager Right_SwitchOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.RIGHT) {
            AutoDrivePath driveToNearSwitch = new AutoDrivePath(robot.driveController, "right-switch_right");
            driveToNearSwitch.addChildStep(0, new AutoIntakeArm(robot.elevatorController));
            steps.add(driveToNearSwitch);

            steps.add(new AutoIntakeWheels(robot.elevatorController));
        } else {
            return DriveForward(robot);
        }

        return new AutoManager(steps);
    }

    public static AutoManager Left_NearScale(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SCALE) == MatchData.OwnedSide.LEFT) {
            AutoDrivePath driveToNearScaleFront = new AutoDrivePath(robot.driveController, "left-scale_left");
            driveToNearScaleFront.addChildStep(0, new AutoIntakeArm(robot.elevatorController));
            driveToNearScaleFront.addChildStep(100, new AutoLift(robot.elevatorController, 73));
//            driveToNearScaleFront.addChildStep(260, new AutoIntakeWheels(robot.elevatorController, 0.5, 0.5));
            steps.add(driveToNearScaleFront);

            steps.add(new AutoIntakeWheels(robot.elevatorController));

            steps.add(new AutoDrivePath(robot.driveController, -25));

            steps.add(new AutoLift(robot.elevatorController, 1.5));

            steps.add(new AutoRotate(robot.driveController,-200));

            AutoDrivePath driveForwardAndCollect = new AutoDrivePath(robot.driveController, 34);
            driveForwardAndCollect.addChildStep(0, new AutoIntakeWheels(robot.elevatorController, 1.25, -1.0));
            steps.add(driveForwardAndCollect);

            steps.add(new AutoLift(robot.elevatorController, 24));

            if (Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT) {
                steps.add(new AutoIntakeWheels(robot.elevatorController));
            }
            else {
                steps.add(new AutoRotate(robot.driveController,+210));
            }
        }
        else {
            return Left_SwitchOnly(robot);
        }

        return new AutoManager(steps);
    }

    public static AutoManager Right_NearScale(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SCALE) == MatchData.OwnedSide.RIGHT) {
            AutoDrivePath driveToNearScaleFront = new AutoDrivePath(robot.driveController, "right-scale_right");
            driveToNearScaleFront.addChildStep(0, new AutoIntakeArm(robot.elevatorController));
            driveToNearScaleFront.addChildStep(100, new AutoLift(robot.elevatorController, 73));
//            driveToNearScaleFront.addChildStep(260, new AutoIntakeWheels(robot.elevatorController, 0.5, 0.5));
            steps.add(driveToNearScaleFront);

            steps.add(new AutoIntakeWheels(robot.elevatorController));

            steps.add(new AutoDrivePath(robot.driveController, -25));

            steps.add(new AutoLift(robot.elevatorController, .75));

            steps.add(new AutoRotate(robot.driveController,200));

            AutoDrivePath driveForwardAndCollect = new AutoDrivePath(robot.driveController, 34);
            driveForwardAndCollect.addChildStep(0, new AutoIntakeWheels(robot.elevatorController, 1.25, -1.0));
            steps.add(driveForwardAndCollect);

            steps.add(new AutoLift(robot.elevatorController, 24));

            if (Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.RIGHT) {
                steps.add(new AutoIntakeWheels(robot.elevatorController));
            }
            else {
                steps.add(new AutoRotate(robot.driveController,-210));
            }
        }
        else {
            return Right_SwitchOnly(robot);
        }

        return new AutoManager(steps);
    }

    public static AutoManager Left_ScaleOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SCALE) == MatchData.OwnedSide.LEFT) {
            return Left_NearScale(robot);
        }
        else {  // LEFT FAR SCALE
            AutoDrivePath driveToFarScaleFront = new AutoDrivePath(robot.driveController, "left-scale_right");
            driveToFarScaleFront.addChildStep(0, new AutoIntakeArm(robot.elevatorController));
            driveToFarScaleFront.addChildStep(320, new AutoLift(robot.elevatorController, 73));
            steps.add(driveToFarScaleFront);

            steps.add(new AutoRotate(robot.driveController,-110));

            steps.add(new AutoDrivePath(robot.driveController, 38));

            steps.add(new AutoIntakeWheels(robot.elevatorController));

            steps.add(new AutoDrivePath(robot.driveController, -26));

            steps.add(new AutoLift(robot.elevatorController, 0.75));

            steps.add(new AutoRotate(robot.driveController,+210));

            AutoDrivePath driveForwardAndCollect = new AutoDrivePath(robot.driveController, 12);
            driveForwardAndCollect.addChildStep(0, new AutoIntakeWheels(robot.elevatorController, 1, -1.0));
            steps.add(driveForwardAndCollect);
        }

        return new AutoManager(steps);
    }

    public static AutoManager Right_ScaleOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (Controls.getOwnedSide(MatchData.GameFeature.SCALE) == MatchData.OwnedSide.RIGHT) {
            return Right_NearScale(robot);
        }
        else {  // RIGHT FAR SCALE
            AutoDrivePath driveToFarScaleFront = new AutoDrivePath(robot.driveController, "right-scale_left");
            driveToFarScaleFront.addChildStep(0, new AutoIntakeArm(robot.elevatorController));
            driveToFarScaleFront.addChildStep(320, new AutoLift(robot.elevatorController, 73));
            steps.add(driveToFarScaleFront);

            steps.add(new AutoRotate(robot.driveController,110));

            steps.add(new AutoDrivePath(robot.driveController, 38));

            steps.add(new AutoIntakeWheels(robot.elevatorController));

            steps.add(new AutoDrivePath(robot.driveController, -26));

            steps.add(new AutoLift(robot.elevatorController, 0.75));

            steps.add(new AutoRotate(robot.driveController,-210));

            AutoDrivePath driveForwardAndCollect = new AutoDrivePath(robot.driveController, 12);
            driveForwardAndCollect.addChildStep(0, new AutoIntakeWheels(robot.elevatorController, 1, -1.0));
            steps.add(driveForwardAndCollect);
        }

        return new AutoManager(steps);
    }

    public static AutoManager Left_SwitchOrNearScale(Robot robot) {
        if (Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT) {
            return Left_SwitchOnly(robot);
        } else {
            return Left_NearScale(robot);
        }
    }

    public static AutoManager Right_SwitchOrNearScale(Robot robot) {
        if (Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.RIGHT) {
            return Right_SwitchOnly(robot);
        } else {
            return Right_NearScale(robot);
        }
    }

    public static AutoManager Left_SwitchOrScale(Robot robot) {
        if (Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.LEFT) {
            return Left_SwitchOnly(robot);
        } else {
            return Left_ScaleOnly(robot);
        }
    }

    public static AutoManager Right_SwitchOrScale(Robot robot) {
        if (Controls.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR) == MatchData.OwnedSide.RIGHT) {
            return Right_SwitchOnly(robot);
        } else {
            return Right_ScaleOnly(robot);
        }
    }
}

