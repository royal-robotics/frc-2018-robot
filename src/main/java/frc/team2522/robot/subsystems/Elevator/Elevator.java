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

    }

    public void robotPeriodic() {
        this.lift.writeToDashboard();
        this.intake.writeToDashboard();
    }
}
