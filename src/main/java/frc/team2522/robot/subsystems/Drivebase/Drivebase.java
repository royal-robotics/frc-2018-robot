package frc.team2522.robot.subsystems.Drivebase;

import com.ctre.phoenix.drive.DiffDrive;
import com.ctre.phoenix.drive.DriveMode;
import com.ctre.phoenix.mechanical.Gearbox;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.libs.DriveType;
import frc.team2522.robot.libs.TankDrive;
import frc.team2522.robot.subsystems.Drivebase.Climber.Climber;
import frc.team2522.robot.subsystems.Drivebase.DriveSystem.DriveSystem;

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

    Gearbox leftMotors = new Gearbox(leftDrive1);
    Gearbox rightMotors = new Gearbox(rightDrive1);

    DiffDrive differentialDrive = new DiffDrive(leftMotors, rightMotors);
    TankDrive tankDrive = new TankDrive(leftMotors, rightMotors);

    DriveData driveDataLeft = new DriveData(10, 11, true);
    DriveData driveDataRight = new DriveData(12,13, false);

    Joystick driver;
    Boolean isClimbingMode;

    DriveSystem driveSystem = new DriveSystem();
    Climber climber = new Climber(isClimbingMode);

    private static final double DEADZONE = 0.2;

    public Drivebase(Joystick driver, Boolean isClimbingMode) {
        this.driver = driver;
        this.isClimbingMode = isClimbingMode;

        leftDrive2.follow(leftDrive1);
        rightDrive2.follow(rightDrive1);
        reset();
    }

    public void fmsUpdateTeleop() {
        DriveType currentDriveType = driveSystem.updateDriveType(driver.getRawButton(7));
        driveSystem.updateShift(driver.getRawButton(9), driver.getRawButton(10));
        climber.updateClimbing(driver.getRawButton(5), driver.getRawButton(6));

        if (isClimbingMode) {
            double leftPower = driver.getRawAxis(1);
            double rightPower = driver.getRawAxis(5);
            if (leftPower < DEADZONE) {
                leftPower = 0;
            }
            if (rightPower < DEADZONE) {
                rightPower = 0;
            }

            double power = (leftPower + rightPower) / 2;

            SmartDashboard.putNumber("Drive/ClimbDrive/Percent", power);

            tankDrive.set(DriveMode.PercentOutput, power, power);
        } else {  // !isClimbingMode
            if (currentDriveType == DriveType.TankDrive) {
                double left = driver.getRawAxis(1);
                double right = driver.getRawAxis(5);
                if (left < DEADZONE && left > -DEADZONE) {
                    left = 0;
                }
                if (right < DEADZONE && right > -DEADZONE) {
                    right = 0;
                }

                SmartDashboard.putNumber("Drive/TankDrive/LeftPercent", left);
                SmartDashboard.putNumber("Drive/TankDrive/RightPercent", right);

                tankDrive.set(DriveMode.PercentOutput, left, right);
            } else {  // currentDriveType == DriveType.CheesyDrive
                double forward = driver.getRawAxis(1);
                double turn = driver.getRawAxis(4);
                if (forward < DEADZONE && forward > -DEADZONE) {
                    forward = 0;
                }
                if (turn < DEADZONE && turn > -DEADZONE) {
                    turn = 0;
                }

                SmartDashboard.putNumber("Drive/CheesyDrive/ForwardPercent", forward);
                SmartDashboard.putNumber("Drive/CheesyDrive/TurnPercent", turn);

                differentialDrive.set(DriveMode.PercentOutput, forward, turn);
            }
        }
    }

    public void reset() {
        driveDataLeft.reset();
        driveDataRight.reset();
    }
}
