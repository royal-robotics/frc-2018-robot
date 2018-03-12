package frc.team2522.robot.autonomous;

import java.util.ArrayList;
import java.util.List;

public abstract class AutoStep {

    private List<AutoStep> childTasks = new ArrayList<>();

    public abstract void initialize();

    public boolean isCompleted() {
        for (int i = 0; i < childTasks.size(); i++) {
            if(!childTasks.get(i).isCompleted())
                return false;
        }

        return true;
    }

    public void periodic() {
        for (int i = 0; i < childTasks.size(); i++) {
            if(!childTasks.get(i).isCompleted())
                childTasks.get(i).periodic();
        }
    }
}