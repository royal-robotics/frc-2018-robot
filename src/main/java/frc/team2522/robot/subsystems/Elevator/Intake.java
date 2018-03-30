package frc.team2522.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Timer;
import java.util.TimerTask;

public class Intake {
    IMotorController elevatorIntakeMotor;
    TalonSRX intakeAngleMotor;

    Timer timer = null;
    long rotateMode;
    boolean intakeOut = false;

    public Intake(IMotorController elevatorIntakeMotor, TalonSRX intakeAngleMotor) {
        this.elevatorIntakeMotor = elevatorIntakeMotor;
        this.intakeAngleMotor = intakeAngleMotor;

        this.initEncoder();
    }

    /**
     *
     */
    public void reset() {

    }

    public void initEncoder() {
        this.intakeAngleMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);

        this.intakeAngleMotor.configForwardSoftLimitThreshold(3470, 10);
        this.intakeAngleMotor.configForwardSoftLimitEnable(true, 10);

        this.intakeAngleMotor.configReverseSoftLimitThreshold(2270, 10);
        this.intakeAngleMotor.configReverseSoftLimitEnable(true, 10);

        this.intakeAngleMotor.config_kF(0, 0.0, 10);
        this.intakeAngleMotor.config_kP(0, 10.0, 10);
        this.intakeAngleMotor.config_kI(0, 0.0, 10);
        this.intakeAngleMotor.config_kD(0, 0.0, 10);

        // Get the absolute pulse width position
        int pulseWidth = intakeAngleMotor.getSensorCollection().getPulseWidthPosition();

        intakeAngleMotor.setSensorPhase(true);
        intakeAngleMotor.setInverted(true);


        final boolean kDiscontinuityPresent = false;
        final int kBookEnd_0 = 2270; /* 200 deg OUT Position */
        final int kBookEnd_1 = 3432; /* 301 deg UP Position*/

        // If there is a discontinuity in our measured range, subtract one half rotation to remove it
        //
        if (kDiscontinuityPresent) {
            // Calculate the center
            int newCenter;
            newCenter = (kBookEnd_0 + kBookEnd_1) / 2;
            newCenter &= 0xFFF;

            // Apply the offset so the discontinuity is in the unused portion of the sensor
            //
            pulseWidth -= newCenter;
        }

        // Mask out the bottom 12 bits to normalize to [0,4095],
        // or in other words, to stay within [0,360) degrees
        //
        pulseWidth = pulseWidth & 0xFFF;

        // ave it to quadrature
        this.intakeAngleMotor.getSensorCollection().setQuadraturePosition(pulseWidth, 10);
    }

    /**
     *
     */
    public void teleopPeriodic() {

    }

    public void setPull() {
        elevatorIntakeMotor.set(ControlMode.PercentOutput, -1.00);
    }

    public void setPush() {
        setPush(1.0);
    }

    public void setPush(double spitPower) {
        elevatorIntakeMotor.set(ControlMode.PercentOutput, spitPower);
    }

    public void setStop() {
        elevatorIntakeMotor.set(ControlMode.PercentOutput, 0.0);
    }

    public void setUp() {
        SmartDashboard.putString("IntakeState", "Up");
        this.intakeOut = false;
    }

    public void setOut() {
        SmartDashboard.putString("IntakeState", "Out");
        this.intakeOut = true;
    }

    public boolean getIntakeOut() {
        return this.intakeOut;
    }

    public double getIntakeAngle() {
        int selSenPos = this.intakeAngleMotor.getSelectedSensorPosition(0);
        double deg = selSenPos * 360.0 / 4096.0;

        /* truncate to 0.1 res */
        deg *= 10.0;
        deg = Math.round(deg);
        deg /= 10.0;

        return deg;
    }

    private int pos = 0;

    public void writeToDashboard() {
        SmartDashboard.putNumber("IntakeEncoder", this.intakeAngleMotor.getSelectedSensorPosition(0));
        SmartDashboard.putNumber("IntakeAngle", this.getIntakeAngle());

        if (pos != this.intakeAngleMotor.getSelectedSensorPosition(0)) {
            pos = this.intakeAngleMotor.getSelectedSensorPosition(0);
            System.out.println("IntakeEncoder="+pos);
        }
    }

}