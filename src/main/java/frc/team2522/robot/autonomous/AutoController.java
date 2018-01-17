package frc.team2522.robot.autonomous;

import java.util.*;

public class AutoController {
    public List<AutoStep> autoSteps;
    public int currentStep = 0;

    public AutoController(List<AutoStep> autoSteps) {
        if(autoSteps.size() < 1)
            throw new IllegalArgumentException("'autoSteps' must contain at least 1 AutoStep");

        this.autoSteps = autoSteps;
        autoSteps.get(currentStep).initialize();
    }

    public void periodic() {
        AutoStep autoStep = autoSteps.get(currentStep);
        if(autoStep.isCompleted()) {
            autoStep.stop();
            if(autoSteps.size() == currentStep + 1) {
                return; //Auto completed
            } else {
                autoStep = autoSteps.get(++currentStep);
                autoStep.initialize();
            }
        }

        autoStep.periodic();
    }
}