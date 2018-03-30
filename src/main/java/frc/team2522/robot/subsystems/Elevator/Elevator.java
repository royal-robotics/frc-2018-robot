package frc.team2522.robot.subsystems.Elevator;

import frc.team2522.robot.Controls;

public class Elevator {
    public Intake intake;
    public Lift lift;


    public Elevator(Intake intake, Lift lift) {
        this.intake = intake;
        this.lift = lift;

        //setupIntakeManager();
    }

    public void reset() {
        this.intake.reset();
        this.lift.reset();
    }

    public void teleopPeriodic() {
        intake.teleopPeriodic();
        lift.teleopPeriodic();

        if(Controls.Elevator.Intake.armsClose()) {
            intake.setStop();
        }

        if (Controls.Elevator.Intake.pullCube()) {
            intake.setPull();
        } else if (Controls.Elevator.Intake.pushCube()) {
            double spitPower = Controls.Elevator.Intake.pushCubeModifier() ? 0.5 : 0.75;
            intake.setPush(spitPower);
        } else {
            intake.setStop();
        }
    }

    public void robotPeriodic() {
        this.lift.writeToDashboard();
        this.intake.writeToDashboard();
    }
}
