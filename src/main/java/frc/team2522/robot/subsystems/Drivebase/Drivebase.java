package frc.team2522.robot.subsystems.Drivebase;

import com.ctre.phoenix.drive.DiffDrive;
import com.ctre.phoenix.drive.DriveMode;
import com.ctre.phoenix.mechanical.Gearbox;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/*
    Drivebase owns all sensors and motors that are part of the sub assembly.

    During Teleop `fmsUpdate` is called. Joystick values should be read at this
    time and motors/modes should be updated.

    DriveController is an important subclass that's responsible for autonomously
    controlling the drive motors. Some of this functionality will be exposed publicly
    so that it can be set by the AutonomousController. It can also be used in various
    teleop 'modes'
 */
public class Drivebase {
    TalonSRX leftDrive1 = new TalonSRX(0);
    VictorSPX leftDrive2 = new VictorSPX(1);
    TalonSRX rightDrive1 = new TalonSRX(6);
    VictorSPX rightDrive2 = new VictorSPX(7);

    Gearbox leftMotors = new Gearbox(leftDrive1, leftDrive2);
    Gearbox rightMotors = new Gearbox(rightDrive1, rightDrive2);
    DiffDrive differentialDrive = new DiffDrive(leftMotors, rightMotors);

    DriveData driveDataLeft = new DriveData(2, 3, true);
    DriveData driveDataRight = new DriveData(4,5, false);

    DriveController driveController = new DriveController(differentialDrive, driveDataLeft, driveDataRight);

    Joystick driver;

    public Drivebase(Joystick driver) {
        this.driver = driver;
        reset();
    }

    public void fmsUpdate() {
        double leftStick = driver.getRawAxis(1);
        double rightStick = driver.getRawAxis(5);
        double forward = (leftStick + rightStick) / 2;
        double turn = leftStick - forward;
        differentialDrive.set(DriveMode.PercentOutput, forward, turn);
    }

    public void reset() {
        driveDataLeft.reset();
        driveDataRight.reset();
    }
}
