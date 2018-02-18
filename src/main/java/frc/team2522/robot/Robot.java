package frc.team2522.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.team2522.robot.camera.CameraPipeline;

import edu.wpi.first.wpilibj.*;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import frc.team2522.robot.subsystems.Drivebase.Drivebase;

public class Robot extends IterativeRobot {
    /************************************************************************
     * IMPORTANT!!!!!!!!!!!
     *
     * MOTOR CONTROLLER CAN ADDRESSES:
     * 0: LEFT DRIVE*
     * 1: LEFT DRIVE
     * 2: ELEVATOR*
     * 3: ELEVATOR
     * 4: ELEVATOR
     * 5: LEFT INTAKE
     * 6: RIGHT DRIVE*
     * 7: RIGHT DRIVE
     * 8: ELEV IN*
     * 9: RIGHT INTAKE
     * * = TALON
     *
     *
     * PNEUMATIC PORTS:
     *
     * MODULE 0:
     * 2 & 5: RATCHET
     * 1 & 6: BRAKE
     * 0 & 7: IN HI
     *
     * MODULE 1:
     * 3 & 4: IN LO
     * 2 & 5: SHIFT
     * 1 & 6: PTO
     *
     *
     * ENCODERS:
     * ENC0 (DIO 10 & 11): LEFT DRIVE  6141
     * ENC1 (DIO 12 & 13): RIGHT DRIVE  6125
     * ENC2 (DIO 14 & 15): ELEVATOR
    ************************************************************************/
    VictorSPX leftIntake = new VictorSPX(5);
    VictorSPX rightIntake = new VictorSPX(9);

    Encoder leftEncoder = new Encoder(10, 11, true);
    Encoder rightEncoder = new Encoder(12, 13);

    DoubleSolenoid ratchet = new DoubleSolenoid(0, 2, 5);
    DoubleSolenoid brake = new DoubleSolenoid(0, 1, 6);
    DoubleSolenoid inHi = new DoubleSolenoid(0, 0, 7);

    DoubleSolenoid inLo = new DoubleSolenoid(1, 3, 4);
    DoubleSolenoid shift = new DoubleSolenoid(1, 2, 5);
    DoubleSolenoid pto = new DoubleSolenoid(1, 1, 6);

    AHRS gyro = new AHRS(SPI.Port.kMXP);

    Joystick driver = new Joystick(0);


    Drivebase drivebase = new Drivebase(driver);
    CameraPipeline camera = new CameraPipeline(driver);

    @Override
    public void robotInit() {
        gyro.reset();
        leftEncoder.reset();
        rightEncoder.reset();
    }

    @Override
    public void disabledInit() {
        leftEncoder.reset();
        rightEncoder.reset();
    }

    @Override
    public void disabledPeriodic() {
        System.out.println(leftEncoder.getRaw());
        System.out.println(rightEncoder.getRaw());
    }

    @Override
    public void autonomousPeriodic() { }

    @Override
    public void autonomousInit() {
        drivebase.reset();
    }
    
    @Override
    public void teleopInit() {
        drivebase.reset();
        leftEncoder.reset();
        rightEncoder.reset();
    }

    @Override
    public void teleopPeriodic() {
        if (driver.getRawButton(1)) {
            ratchet.set(DoubleSolenoid.Value.kForward);
        } else {
            ratchet.set(DoubleSolenoid.Value.kReverse);
        }

        if (driver.getRawButton(2)) {
            brake.set(DoubleSolenoid.Value.kForward);
        } else {
            brake.set(DoubleSolenoid.Value.kReverse);
        }

        if (driver.getRawButton(3)) {
            inHi.set(DoubleSolenoid.Value.kForward);
        } else {
            inHi.set(DoubleSolenoid.Value.kReverse);
        }

        if (driver.getRawButton(4)) {
            inLo.set(DoubleSolenoid.Value.kForward);
        } else {
            inLo.set(DoubleSolenoid.Value.kReverse);
        }

        if (driver.getRawButton(5)) {
            shift.set(DoubleSolenoid.Value.kForward);
        } else {
            shift.set(DoubleSolenoid.Value.kReverse);
        }

        if (driver.getRawButton(6)) {
            pto.set(DoubleSolenoid.Value.kForward);
        } else {
            pto.set(DoubleSolenoid.Value.kReverse);
        }

        drivebase.fmsUpdate();

        double leftTrigger = driver.getRawAxis(2);
        if (leftTrigger > 0.2) {
            leftIntake.set(ControlMode.PercentOutput, leftTrigger);
        } else {
            leftIntake.set(ControlMode.PercentOutput, 0);
        }

        double rightTrigger = driver.getRawAxis(3);
        if (rightTrigger > 0.2) {
            rightIntake.set(ControlMode.PercentOutput, rightTrigger);
        } else {
            rightIntake.set(ControlMode.PercentOutput, 0);
        }

        System.out.println(leftEncoder.getRaw());
        System.out.println(rightEncoder.getRaw());
    }
}