package frc.team2522.robot.subsystems.Elevator.Intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.libs.Axis;
import frc.team2522.robot.libs.Button;
import frc.team2522.robot.*;

import java.util.Timer;
import java.util.TimerTask;

public class Intake {
    DoubleSolenoid inHi = new DoubleSolenoid(0, 0, 7);
    DoubleSolenoid inLo = new DoubleSolenoid(1, 3, 4);

    VictorSPX leftIntake = new VictorSPX(5);
    VictorSPX rightIntake = new VictorSPX(9);
    TalonSRX carriage = null; //Shared with Lift;

    boolean rotateMode = false;

    Timer timer = null;


    public Intake(Joystick driver, TalonSRX carriage) {
        this.carriage = carriage;

        SmartDashboard.putNumber("Intake/Pull/carriage", 0.75);
        SmartDashboard.putNumber("Intake/Pull/left", 0.8);
        SmartDashboard.putNumber("Intake/Pull/right", 0.8);

        SmartDashboard.putNumber("Intake/Rotate/carriage", 0.75);
        SmartDashboard.putNumber("Intake/Rotate/left", 0.8);
        SmartDashboard.putNumber("Intake/Rotate/right", -0.2);

        SmartDashboard.putNumber("Intake/Rotate/interval", 333);
        SmartDashboard.putBoolean("Intake/Position/Pickup", false);
        SmartDashboard.putBoolean("Intake/Position/In", false);
        SmartDashboard.putBoolean("Intake/Position/Out", false);
    }

    boolean isPickup = false;
    boolean isIn = false;
    boolean isOut = false;
    public void fmsUpdateTeleop() {
        if (Controls.Elevator.Intake.pickup.isPressed()) {
            inHi.set(DoubleSolenoid.Value.kReverse);
            inLo.set(DoubleSolenoid.Value.kReverse);
            isPickup = true;
            isIn = false;
            isOut = false;
        }

        if (Controls.Elevator.Intake.closed.isPressed()) {
            inHi.set(DoubleSolenoid.Value.kForward);
            inLo.set(DoubleSolenoid.Value.kReverse);
            isIn = true;
            isOut =  false;
            isPickup = false;
        }

        if (Controls.Elevator.Intake.open.isPressed()) {
            inHi.set(DoubleSolenoid.Value.kReverse);
            inLo.set(DoubleSolenoid.Value.kForward);
            isOut = true;
            isIn = false;
            isPickup = false;
        }
        SmartDashboard.putBoolean("Intake/Position/Pickup", isPickup);
        SmartDashboard.putBoolean("Intake/Position/In", isIn);
        SmartDashboard.putBoolean("Intake/Position/Out", isOut);

        if(Controls.Elevator.Intake.pullCube.isPressed() && Controls.Elevator.Intake.pushCube.isPressed()) {
            makeRotateTimer();
        }
        else if (Controls.Elevator.Intake.pullCube.isPressed()) {
            stopRotateTimer();
            setPull();
        } else if (Controls.Elevator.Intake.pushCube.isPressed()) {
            stopRotateTimer();
            setPush();
        } else {
            stopRotateTimer();
            setStop();
        }
    }

    private void makeRotateTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    rotateMode = !rotateMode;
                    if (rotateMode) {
                        setPull();
                    } else {
                        setRotate();
                    }
                }
            }, 0, (int) SmartDashboard.getNumber("Intake/Rotate/interval", 333));
        }
    }

    private void stopRotateTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void setPull() {
        carriage.set(ControlMode.PercentOutput, -SmartDashboard.getNumber("Intake/Pull/carriage", 0.75));
        leftIntake.set(ControlMode.PercentOutput, -SmartDashboard.getNumber("Intake/Pull/left", 0.8));
        rightIntake.set(ControlMode.PercentOutput, SmartDashboard.getNumber("Intake/Pull/right", 0.8));
    }

    private void setPush() {
        carriage.set(ControlMode.PercentOutput, SmartDashboard.getNumber("Intake/Pull/carriage", 0.75));
        leftIntake.set(ControlMode.PercentOutput, SmartDashboard.getNumber("Intake/Pull/left", 0.8));
        rightIntake.set(ControlMode.PercentOutput, -SmartDashboard.getNumber("Intake/Pull/right", 0.8));
    }

    private void setRotate() {
        carriage.set(ControlMode.PercentOutput, -SmartDashboard.getNumber("Intake/Rotate/carriage", 0.75));
        leftIntake.set(ControlMode.PercentOutput, -SmartDashboard.getNumber("Intake/Rotate/left", 0.8));
        rightIntake.set(ControlMode.PercentOutput, SmartDashboard.getNumber("Intake/Rotate/right", -0.2));
    }

    private void setStop() {
        carriage.set(ControlMode.PercentOutput, 0);
        leftIntake.set(ControlMode.PercentOutput, 0);
        rightIntake.set(ControlMode.PercentOutput, 0);
    }
}