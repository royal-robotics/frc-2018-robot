package frc.team2522.robot.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.Robot;
import frc.team2522.robot.autonomous.BuildingBlocks.AutoDrive;
import frc.team2522.robot.autonomous.BuildingBlocks.AutoDrivePath;
import frc.team2522.robot.autonomous.BuildingBlocks.AutoRotate;
import frc.team2522.robot.autonomous.BuildingBlocks.AutoSpit;
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
            return (AutoManager) AutoRoutines.class.getMethod(autoName, Robot.class).invoke(robot);
        } catch (Exception ex) {
            System.out.println("Failed to select Auto mode");
            return null;
        }
    }

    public static AutoManager SimpleTest(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();
        steps.add(new AutoDrive(robot.driveController, 30));
        steps.add(new AutoRotate(robot.driveController, 90));
        steps.add(new AutoDrive(robot.driveController, 30));

        return new AutoManager(steps);
    }

    public static AutoManager SimpleTest2(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();
        steps.add(new AutoDrive(robot.driveController, 50));
        steps.add(new AutoRotate(robot.driveController, 180));
        steps.add(new AutoDrive(robot.driveController, 30));

        return new AutoManager(steps);
    }

    public static AutoManager Center_SwitchOnly(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();
        if (true) {// TODO: this should be a field managment check for switch is on right side
            steps.add(new AutoDrivePath(robot.driveController, "center-right_side_switch"));
        } else {
            steps.add(new AutoDrivePath(robot.driveController, "center-left_side_switch"));
        }
        steps.add(new AutoSpit(robot.elevatorController, 0.25));

        return new AutoManager(steps);
    }

    public static AutoManager Right_SwitchOrScale(Robot robot) {
        List<AutoStep> steps = new ArrayList<>();

        if (false) {// TODO: this should be a field managment check for switch is on right side
            steps.add(new AutoDrive(robot.driveController, 155));
            steps.add(new AutoRotate(robot.driveController, -90));
            steps.add(new AutoDrive(robot.driveController, 25.0));
            steps.add(new AutoSpit(robot.elevatorController, 0.25));
        } else {
            if (true) { // FSM check for scale on right
                steps.add(new AutoDrive(robot.driveController, 290));
                steps.add(new AutoRotate(robot.driveController, -90));
                // TODO: raise elevator
                steps.add(new AutoDrive(robot.driveController, 10.0));
                steps.add(new AutoSpit(robot.elevatorController, 0.25));
            } else {
                steps.add(new AutoDrive(robot.driveController, 190));
                steps.add(new AutoRotate(robot.driveController, -90));
                steps.add(new AutoDrive(robot.driveController, 200));
                steps.add(new AutoRotate(robot.driveController, 90));
                // TODO: raise elevator
                steps.add(new AutoDrive(robot.driveController, 10.0));
                steps.add(new AutoSpit(robot.elevatorController, 0.25));
            }
        }

        return new AutoManager(steps);
    }
}

