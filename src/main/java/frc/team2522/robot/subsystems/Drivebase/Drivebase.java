package frc.team2522.robot.subsystems.Drivebase;

import com.ctre.phoenix.drive.DiffDrive;
import com.ctre.phoenix.mechanical.Gearbox;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.*;
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
    Joystick driver;
    Boolean isClimbingMode;

    TalonSRX leftDrive1 = new TalonSRX(0);
    VictorSPX leftDrive2 = new VictorSPX(1);
    TalonSRX rightDrive1 = new TalonSRX(6);
    VictorSPX rightDrive2 = new VictorSPX(7);

    Gearbox leftMotors = new Gearbox(leftDrive1);
    Gearbox rightMotors = new Gearbox(rightDrive1);

    DiffDrive differentialDrive = new DiffDrive(leftMotors, rightMotors);
    TankDrive tankDrive = new TankDrive(leftMotors, rightMotors);

    DriveSystem driveSystem = new DriveSystem(tankDrive, differentialDrive);
    Climber climber = new Climber(tankDrive, isClimbingMode);

    private static final double DEADZONE = 0.2;

    public Drivebase(Joystick driver, Boolean isClimbingMode) {
        this.driver = driver;
        this.isClimbingMode = isClimbingMode;

        leftDrive2.follow(leftDrive1);
        rightDrive2.follow(rightDrive1);
        driveSystem.reset();
    }

    public void fmsUpdateTeleop() {
        driveSystem.updateDriveType(driver.getRawButton(7));
        driveSystem.updateShift(driver.getRawButton(9), driver.getRawButton(10));
        //climber.updateClimbing(driver.getRawButton(5), driver.getRawButton(6));

        double left = driver.getRawAxis(1);
        double right = driver.getRawAxis(5);
        double turn = driver.getRawAxis(4);
//        if (isClimbingMode) {
//            climber.climb(left, right, DEADZONE);
//        } else {  // !isClimbingMode
            driveSystem.drive(left, right, left, turn, DEADZONE);
//        }
    }

    public void reset() {
        driveSystem.reset();
    }
}
