package frc.team2522.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.Controls;

public class Intake {
    IMotorController elevatorIntakeMotor;
    TalonSRX carriageIntakeArm;
    DigitalInput intakeArmUpSwitch;
    DigitalInput intakeArmDownSwitch;

    final double kArmPowerMaxUp = 0.75;
    final double kArmPowerMaxDown = 0.50;

    public final int kBackEncoderPosition = 3840;   // 138 degrees back
    public final int kUpEncoderPosition = 3590;    // 95 degrees up
    public final int kOutEncoderPosition = 2470;    // 0 degrees out


    public Intake(IMotorController elevatorIntakeMotor, TalonSRX intakeAngleMotor, DigitalInput intakeArmUpSwitch, DigitalInput intakeArmDownSwitch) {
        this.elevatorIntakeMotor = elevatorIntakeMotor;
        this.carriageIntakeArm = intakeAngleMotor;
        this.intakeArmUpSwitch = intakeArmUpSwitch;
        this.intakeArmDownSwitch = intakeArmDownSwitch;

        this.initEncoder();
    }

    /**
     *
     */
    public void reset() {

    }

    public void initEncoder() {
        this.carriageIntakeArm.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);

        this.carriageIntakeArm.configForwardSoftLimitThreshold(kUpEncoderPosition, 10);
        this.carriageIntakeArm.configForwardSoftLimitEnable(false, 10);

        this.carriageIntakeArm.configReverseSoftLimitThreshold(kOutEncoderPosition, 10);
        this.carriageIntakeArm.configReverseSoftLimitEnable(false, 10);

        this.carriageIntakeArm.config_kF(0, 0.0, 10);
        this.carriageIntakeArm.config_kP(0, 10.0, 10);
        this.carriageIntakeArm.config_kI(0, 0.0, 10);
        this.carriageIntakeArm.config_kD(0, 0.0, 10);

        // Get the absolute pulse width position
        int pulseWidth = carriageIntakeArm.getSensorCollection().getPulseWidthPosition();

        carriageIntakeArm.setSensorPhase(false);
        carriageIntakeArm.setInverted(false);


        // If there is a discontinuity in our measured range, subtract one half rotation to remove it
        //
        final boolean kDiscontinuityPresent = false;
        final int kBookEnd_0 = kOutEncoderPosition; /* OUT Position */
        final int kBookEnd_1 = kUpEncoderPosition; /* UP Position*/
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
        this.carriageIntakeArm.getSensorCollection().setQuadraturePosition(pulseWidth, 10);
    }

    /**
     *
     */
    public void teleopPeriodic() {

        double armAxisValue = Controls.Elevator.Intake.getArmAxisValue();
        if (armAxisValue > 0.0) {
            this.moveArm(armAxisValue * kArmPowerMaxUp);
        }
        else if (armAxisValue < 0.0) {
            this.moveArm(armAxisValue * kArmPowerMaxDown);
        }
        else{
            this.moveArm(0);
        }

        if (Controls.Elevator.Intake.pushCube()) {
            this.setPush(0.75);
        }
        else if (Controls.Elevator.Intake.pushCubeSoft()) {
            this.setPush(0.40);
        }
        else if (Controls.Elevator.Intake.pushCubeHard()) {
            this.setPush(1.0);
        }
        else if (Controls.Elevator.Intake.pullCube()) {
            this.setPull();
        }
        else {
            this.setStop();
        }
    }

    public void setPull() {
        elevatorIntakeMotor.set(ControlMode.PercentOutput, -0.75);
    }

    public void setPush() {
        setPush(0.75);
    }

    public void setPush(double power) {
        elevatorIntakeMotor.set(ControlMode.PercentOutput, power);
    }

    public void setStop() {
        elevatorIntakeMotor.set(ControlMode.PercentOutput, 0.0);
    }

    public boolean isArmUp() {
        return !this.intakeArmUpSwitch.get();
    }

    public boolean isArmDown() {
        return !this.intakeArmDownSwitch.get();
    }

    public void moveArmUp() {
        this.moveArm(kArmPowerMaxUp);
    }

    public void moveArmDown() {
        this.moveArm(-kArmPowerMaxDown);
    }

    public void moveArm(double power) {
        if (power > 0.0 && !this.isArmUp()) {
            this.carriageIntakeArm.set(ControlMode.PercentOutput, power);
        }
        else if (power < 0.0 && !this.isArmDown()) {
            this.carriageIntakeArm.set(ControlMode.PercentOutput, power);
        }
        else
        {
            this.carriageIntakeArm.set(ControlMode.PercentOutput, 0.0);
        }
    }

    public void setIntakeOut() {
        this.setIntakeAngle(0);
    }

    public double getIntakeAngle() {
        int selSenPos = getIntakeArmRaw() - kOutEncoderPosition;
        double deg = selSenPos * 360.0 / 4096.0;

        /* truncate to 0.1 res */
        deg *= 10.0;
        deg = Math.round(deg);
        deg /= 10.0;

        return deg;
    }

    public void setIntakeAngle(double angle) {
        int pos = (int)Math.round((angle * 4096.0) / 360.0) + kOutEncoderPosition;
        this.setIntakeArmTargetRaw(pos);
    }

    public void setIntakeNeutral() {
        this.carriageIntakeArm.set(ControlMode.PercentOutput, 0);
    }
    private int getIntakeArmRaw() {
        return this.carriageIntakeArm.getSelectedSensorPosition(0);
    }

    private void setIntakeArmTargetRaw(int value) {
        this.carriageIntakeArm.set(ControlMode.Position, value);
    }

    public void writeToDashboard() {
        SmartDashboard.putNumber("IntakeEncoder", this.getIntakeArmRaw());
        SmartDashboard.putNumber("IntakeAngle", this.getIntakeAngle());

        SmartDashboard.putString("IntakeState", "" + this.getIntakeAngle());

        SmartDashboard.putBoolean("IntakeUpSwitch", this.intakeArmUpSwitch.get());
        SmartDashboard.putBoolean("IntakeDownSwitch", this.intakeArmDownSwitch.get());
    }

}