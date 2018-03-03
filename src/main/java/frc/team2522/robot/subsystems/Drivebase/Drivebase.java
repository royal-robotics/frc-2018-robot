package frc.team2522.robot.subsystems.Drivebase;

import com.ctre.phoenix.drive.DiffDrive;
import com.ctre.phoenix.mechanical.Gearbox;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.Controls;
import frc.team2522.robot.libs.*;
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

    Axis left;
    Axis right;
    Axis turn;

    public Drivebase(Joystick driver, Boolean isClimbingMode) {
        this.driver = driver;
        this.isClimbingMode = isClimbingMode;

        left = new Axis(driver, 1, 0.1);
        right = new Axis(driver, 5, 0.1);
        turn = new Axis(driver, 4, 0.1);
        leftDrive2.follow(leftDrive1);
        rightDrive2.follow(rightDrive1);
        driveSystem.reset();
        leftDrive1.setNeutralMode(NeutralMode.Brake);
        rightDrive1.setNeutralMode(NeutralMode.Brake);
    }

    public void fmsUpdateTeleop() {
        if(Controls.Drivebase.DriveSystem.driveConfig.isPressed()) {
            driveSystem.toggleControlsType();
        }

        if(Controls.Drivebase.DriveSystem.shift.isPressed()) {
            driveSystem.toggleShift();
        }

        if(Controls.Drivebase.Climber.activateClimb.isPressed()) {
            climber.turnClimbModeOn();
        }

        //TODO: Make an `Axis` type and pass that in instead of driver
        if (isClimbingMode) {
            //climber.climb(left, right);
        } else {  // !isClimbingMode
            driveSystem.drive(left, right, turn);
        }
        //driveSystem.writeToDashboard();
        //climber.writeToDashboard();
    }

    public void reset() {
        driveSystem.reset();
    }
}
