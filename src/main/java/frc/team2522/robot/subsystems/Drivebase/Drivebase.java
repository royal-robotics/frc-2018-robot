package frc.team2522.robot.subsystems.Drivebase;

import com.ctre.phoenix.drive.DiffDrive;
import com.ctre.phoenix.drive.DriveMode;
import com.ctre.phoenix.mechanical.Gearbox;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.libs.ObservableBoolean;
import frc.team2522.robot.libs.TankDrive;

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
    DoubleSolenoid shift = new DoubleSolenoid(1, 2, 5);

    TalonSRX leftDrive1 = new TalonSRX(0);
    VictorSPX leftDrive2 = new VictorSPX(1);
    TalonSRX rightDrive1 = new TalonSRX(6);
    VictorSPX rightDrive2 = new VictorSPX(7);

    Gearbox leftMotors = new Gearbox(leftDrive1);
    Gearbox rightMotors = new Gearbox(rightDrive1);

    DiffDrive differentialDrive = new DiffDrive(leftMotors, rightMotors);
    TankDrive tankDrive = new TankDrive(leftMotors, rightMotors);

    DriveData driveDataLeft = new DriveData(10, 11, true);
    DriveData driveDataRight = new DriveData(12,13, false);

    Joystick driver;
    ObservableBoolean isClimbingMode;

    public Drivebase(Joystick driver, ObservableBoolean isClimbingMode) {
        this.driver = driver;
        this.isClimbingMode = isClimbingMode;

        leftDrive2.follow(leftDrive1);
        rightDrive2.follow(rightDrive1);
        reset();
    }

    boolean tankDriveSet = true;
    boolean tankDriveSetPressed = false;

    boolean shiftOn = false;
    boolean shiftOnPressed = false;

    public void fmsUpdateTeleop() {
        if (driver.getRawButton(7) && !tankDriveSetPressed) {
            tankDriveSetPressed = true;
            tankDriveSet = !tankDriveSet;
        } else if (!driver.getRawButton(7)) {
            tankDriveSetPressed = false;
        }

        if ((driver.getRawButton(9) || driver.getRawButton(10)) && !shiftOnPressed) {
            shiftOnPressed = true;
            shiftOn = !shiftOn;
        } else if (!driver.getRawButton(9) && !driver.getRawButton(10)) {
            shiftOnPressed = false;
        }

        SmartDashboard.putBoolean("Drive/TankDriveSet", tankDriveSet);
        SmartDashboard.putBoolean("Drive/CheesyDriveSet", !tankDriveSet);
        SmartDashboard.putBoolean("Drive/HighGear", shiftOn);
        SmartDashboard.putBoolean("Drive/LowGear", !shiftOn);

        if (shiftOn) {
            shift.set(DoubleSolenoid.Value.kForward);
        } else {
            shift.set(DoubleSolenoid.Value.kReverse);
        }

        if (tankDriveSet) {
            double left = driver.getRawAxis(1);
            double right = driver.getRawAxis(5);
            if (left < 0.2 && left > -0.2) {
                left = 0;
            }
            if (right < 0.2 && right > -0.2) {
                right = 0;
            }

            SmartDashboard.putNumber("Drive/TankDrive/LeftPercent", left);
            SmartDashboard.putNumber("Drive/TankDrive/RightPercent", right);

            tankDrive.set(DriveMode.PercentOutput, left, right);
        } else {
            double forward = driver.getRawAxis(1);
            double turn = driver.getRawAxis(4);
            if (forward < 0.2 && forward > -0.2) {
                forward = 0;
            }
            if (turn < 0.2 && turn > -0.2) {
                turn = 0;
            }
            differentialDrive.set(DriveMode.PercentOutput, forward, turn);
        }
    }

    public void reset() {
        driveDataLeft.reset();
        driveDataRight.reset();
    }
}
