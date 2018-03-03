package frc.team2522.robot.subsystems.Elevator.Lift;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.Controls;

import javax.naming.ldap.Control;

public class Lift {

    TalonSRX liftMotors = new TalonSRX(2);
    VictorSPX lift2 = new VictorSPX(3);
    VictorSPX lift3 = new VictorSPX(4);
    TalonSRX carriage = null; //Shared with Lift;

    DoubleSolenoid brake = new DoubleSolenoid(0, 1, 6);

    LiftData data = new LiftData();

    Joystick driver;

    //If we fail to calibrate, or detect a problem switch to manual mode
    boolean isManualMode = true;

    public Lift(Joystick driver, TalonSRX carriage) {
        this.driver = driver;
        this.carriage = carriage;

        lift2.follow(liftMotors);
        lift3.follow(liftMotors);
    }

    public void fmsUpdateTeleop() {
        if(isManualMode)
            fmsUpdateTeleopManualMode();
        else
            fmsUpdateTeleopEncoderMode();
    }

    private void fmsUpdateTeleopManualMode() {
        if(Controls.Elevator.Lift.liftUp.isPressed()) {
            brake.set(DoubleSolenoid.Value.kReverse);
            SmartDashboard.putNumber("Lift/Motors1/current", liftMotors.getOutputCurrent());
            liftMotors.set(ControlMode.PercentOutput, 0.7);
        } else if(Controls.Elevator.Lift.liftdown.isPressed()) {
            brake.set(DoubleSolenoid.Value.kReverse);
            liftMotors.set(ControlMode.PercentOutput, -0.4);
            SmartDashboard.putNumber("Lift/Motors1/current", liftMotors.getOutputCurrent());
        } else {
            brake.set(DoubleSolenoid.Value.kForward);
            liftMotors.set(ControlMode.PercentOutput, 0.0);
        }
    }

    private void fmsUpdateTeleopEncoderMode() {

    }
}