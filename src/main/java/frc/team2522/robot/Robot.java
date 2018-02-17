package frc.team2522.robot;

import frc.team2522.robot.camera.CameraPipeline;

import edu.wpi.first.wpilibj.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import frc.team2522.robot.subsystems.*;

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
     * ENC0: LEFT DRIVE
     * ENC1: RIGHT DRIVE
     * ENC2: ELEVATOR
    ************************************************************************/
    TalonSRX motorcontroller = new TalonSRX(1);

    DoubleSolenoid ratchet = new DoubleSolenoid(0, 2, 5);
    DoubleSolenoid brake = new DoubleSolenoid(0, 1, 6);
    DoubleSolenoid inHi = new DoubleSolenoid(0, 0, 7);

    DoubleSolenoid inLo = new DoubleSolenoid(1, 3, 4);
    DoubleSolenoid shift = new DoubleSolenoid(1, 2, 5);
    DoubleSolenoid pto = new DoubleSolenoid(1, 1, 6);

    AHRS gyro = new AHRS(SPI.Port.kMXP);

    Joystick driver = new Joystick(0);


    Drivebase drivebase = new Drivebase();
    DriveData driveDataLeft = new DriveData(2, 3, true);
    DriveData driveDataRight = new DriveData(6,7, false);
    DriveController driveController = new DriveController(drivebase, driveDataLeft, driveDataRight);

    CameraPipeline camera = new CameraPipeline(driver);

    @Override
    public void robotInit() {
        gyro.reset();
    }

    @Override
    public void disabledInit() { }

    @Override
    public void disabledPeriodic() { }

    @Override
    public void autonomousPeriodic() { }

    @Override
    public void autonomousInit() {
        driveDataLeft.reset();
        driveDataRight.reset();
        driveController.Start();
    }
    
    @Override
    public void teleopInit() {
        driveDataLeft.reset();
        driveDataRight.reset();
        driveController.Stop();
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

        drivebase.setPower(driver.getRawAxis(1), driver.getRawAxis(5));
    }
}