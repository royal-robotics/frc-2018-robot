package frc.team2522.robot.autonomous;

import java.util.*;

// Auto manager / top level step
public class AutoManager {
    public List<AutoStep> autoSteps;
    public int currentStep = 0;

    private Timer timer = new Timer();
    private final long msPeriodic = 50;

    public AutoManager(List<AutoStep> autoSteps) {
        this.autoSteps = autoSteps;

        if(autoSteps.size() == 0)
            return;

        autoSteps.get(currentStep).initialize();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                AutoStep currentAutoStep = autoSteps.get(currentStep);
                if(currentAutoStep.isCompleted()) {
                    if(autoSteps.size() == currentStep + 1) {
                        timer.cancel();
                        timer.purge();
                        timer = null;
                        return; //Auto completed
                    } else {
                        currentAutoStep = autoSteps.get(++currentStep);
                        currentAutoStep.initialize();
                    }
                }

                currentAutoStep.periodic();
            }
        }, 0, msPeriodic);
    }

    public void stop() {
        for(AutoStep step : autoSteps) {
            //TODO: once child steps move to base AutoStep this should stop the child steps
            step.stop();
        }
    }
}