package frc.team2522.robot.subsystems.Elevator.Intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.subsystems.Elevator.Carriage.Carriage;

import java.util.Timer;
import java.util.TimerTask;

public class Intake {
    VictorSPX leftIntake = new VictorSPX(5);
    VictorSPX rightIntake = new VictorSPX(9);

    Joystick driver;
    Carriage carriage;

    public Intake(Joystick driver, Carriage carriage) {
        this.driver = driver;
        this.carriage = carriage;

        SmartDashboard.putNumber("Intake/Pull/carriage", 0.35);
        SmartDashboard.putNumber("Intake/Pull/left", 0.8);
        SmartDashboard.putNumber("Intake/Pull/right", 0.8);

        SmartDashboard.putNumber("Intake/Rotate/carriage", 0.35);
        SmartDashboard.putNumber("Intake/Rotate/left", 0.8);
        SmartDashboard.putNumber("Intake/Rotate/right", -0.2);

        SmartDashboard.putNumber("Intake/Rotate/interval", 333);
    }

    boolean rotateMode = false;
    Timer timer = null;
    public void fmsUpdateTeleop() {
        if(driver.getRawButton(5)) { //Pull the cube in
            stopRotateTimer();
            setPull();
        } else if(driver.getRawButton(6)) { //Correct the orientation of the cube
            if(timer == null) {
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        rotateMode = !rotateMode;
                        if(rotateMode) {
                            setPull();
                        } else {
                            setRotate();
                        }
                    }
                }, 0, (int)SmartDashboard.getNumber("Intake/Rotate/interval", 333));
            }
        } else {
            stopRotateTimer();
            setStop();
        }
    }

    private void stopRotateTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void setPull() {
        carriage.set(SmartDashboard.getNumber("Intake/Pull/carriage", 0.35));
        leftIntake.set(ControlMode.PercentOutput, -SmartDashboard.getNumber("Intake/Pull/left", 0.8));
        rightIntake.set(ControlMode.PercentOutput, SmartDashboard.getNumber("Intake/Pull/right", 0.8));
    }

    private void setRotate() {
        carriage.set(SmartDashboard.getNumber("Intake/Rotate/carriage", 0.35));
        leftIntake.set(ControlMode.PercentOutput, -SmartDashboard.getNumber("Intake/Rotate/left", 0.8));
        rightIntake.set(ControlMode.PercentOutput, SmartDashboard.getNumber("Intake/Rotate/right", -0.2));
    }

    private void setStop() {
        carriage.set(0);
        leftIntake.set(ControlMode.PercentOutput, 0);
        rightIntake.set(ControlMode.PercentOutput, 0);
    }
}