package frc.team2522.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.*;

import java.util.Timer;
import java.util.TimerTask;

public class Intake {
    IMotorController elevatorIntakeMotor;
    IMotorController leftIntakeMotor;
    IMotorController rightIntakeMotor;

    DoubleSolenoid intakeHi;
    DoubleSolenoid intakeLo;

    Timer timer = null;
    boolean rotateMode;
    boolean armsOut = false;

    public Intake(IMotorController elevatorIntakeMotor, IMotorController leftIntakeMotor, IMotorController rightIntakeMotor, DoubleSolenoid intakeHi, DoubleSolenoid intakeLo) {
        this.elevatorIntakeMotor = elevatorIntakeMotor;
        this.leftIntakeMotor = leftIntakeMotor;
        this.rightIntakeMotor = rightIntakeMotor;
        this.intakeHi = intakeHi;
        this.intakeLo = intakeLo;

        //SmartDashboard.putNumber("Intake/Pull/carriage", 0.75);
        SmartDashboard.putNumber("Intake/Pull/left", 0.8);
        SmartDashboard.putNumber("Intake/Pull/right", 0.8);

        SmartDashboard.putNumber("Intake/Rotate/carriage", 0.75);
        SmartDashboard.putNumber("Intake/Rotate/left", 0.8);
        SmartDashboard.putNumber("Intake/Rotate/right", -0.2);

        SmartDashboard.putNumber("Intake/Rotate/interval", 333);
//        SmartDashboard.putBoolean("Intake/Position/Pickup", false);
//        SmartDashboard.putBoolean("Intake/Position/In", false);
//        SmartDashboard.putBoolean("Intake/Position/Out", false);
    }

    /**
     *
     */
    public void reset() {

    }

    /**
     *
     */
    public void teleopPeriodic() {

    }

    public void startRotate() {
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

    public void stopRotate() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setPull() {
        elevatorIntakeMotor.set(ControlMode.PercentOutput, -0.75/*-SmartDashboard.getNumber("Intake/Pull/carriage", 0.75)*/);
        leftIntakeMotor.set(ControlMode.PercentOutput, -0.8/*-SmartDashboard.getNumber("Intake/Pull/left", 0.8)*/);
        rightIntakeMotor.set(ControlMode.PercentOutput, 0.8/*SmartDashboard.getNumber("Intake/Pull/right", 0.8)*/);
    }

    public void setPush() {
        setPush(0.75);
    }

    public void setPush(double spitPower) {
        elevatorIntakeMotor.set(ControlMode.PercentOutput, spitPower);
        leftIntakeMotor.set(ControlMode.PercentOutput, SmartDashboard.getNumber("Intake/Pull/left", 0.8));
        rightIntakeMotor.set(ControlMode.PercentOutput, -SmartDashboard.getNumber("Intake/Pull/right", 0.8));
    }

    public void setRotate() {
        elevatorIntakeMotor.set(ControlMode.PercentOutput, -SmartDashboard.getNumber("Intake/Rotate/carriage", 0.75));
        leftIntakeMotor.set(ControlMode.PercentOutput, -SmartDashboard.getNumber("Intake/Rotate/left", 0.8));
        rightIntakeMotor.set(ControlMode.PercentOutput, SmartDashboard.getNumber("Intake/Rotate/right", -0.2));
    }

    public void setStop() {
        elevatorIntakeMotor.set(ControlMode.PercentOutput, -0.2);
        leftIntakeMotor.set(ControlMode.PercentOutput, 0.0);
        rightIntakeMotor.set(ControlMode.PercentOutput, 0.0);
    }

    public void setClosed() {
        SmartDashboard.putString("IntakeState", "Closed");
        this.armsOut = false;
        intakeHi.set(DoubleSolenoid.Value.kForward);
        intakeLo.set(DoubleSolenoid.Value.kReverse);
    }

    public void setPickup() {
        SmartDashboard.putString("IntakeState", "Pickup");
        this.armsOut = true;
        intakeHi.set(DoubleSolenoid.Value.kReverse);
        intakeLo.set(DoubleSolenoid.Value.kReverse);
    }

    public void setOpen() {
        SmartDashboard.putString("IntakeState", "Open");
        this.armsOut = true;
        intakeHi.set(DoubleSolenoid.Value.kReverse);
        intakeLo.set(DoubleSolenoid.Value.kForward);
    }

    public boolean getArmsOut() {
        return this.armsOut;
    }

}