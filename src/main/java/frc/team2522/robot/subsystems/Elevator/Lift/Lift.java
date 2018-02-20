package frc.team2522.robot.subsystems.Elevator.Lift;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import javax.naming.ldap.Control;

public class Lift {

    TalonSRX liftMotors = new TalonSRX(2);
    VictorSPX lift2 = new VictorSPX(3);
    VictorSPX lift3 = new VictorSPX(4);
    TalonSRX carriage = null; //Shared with Lift;

    DoubleSolenoid brake = new DoubleSolenoid(0, 1, 6);

    LiftData data = new LiftData();

    Joystick driver;

    public Lift(Joystick driver, TalonSRX carriage) {
        this.driver = driver;
        this.carriage = carriage;

        lift2.follow(liftMotors);
        lift3.follow(liftMotors);
    }

    public void fmsUpdateTeleop() {
        lift2.getOutputCurrent();

        if(driver.getPOV() == 0) {
            SmartDashboard.putNumber("Lift/Motors1/current", liftMotors.getOutputCurrent());
            liftMotors.set(ControlMode.PercentOutput, 0.5);
        } else if(driver.getPOV() == 180) {
            liftMotors.set(ControlMode.PercentOutput, -0.5);
            SmartDashboard.putNumber("Lift/Motors1/current", liftMotors.getOutputCurrent());
        } else {
            liftMotors.set(ControlMode.PercentOutput, 0.0);
        }
    }
}