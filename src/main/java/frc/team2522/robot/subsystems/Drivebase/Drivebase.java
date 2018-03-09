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

    public Drivebase(Boolean isClimbingMode) {
        this.isClimbingMode = isClimbingMode;

        leftDrive2.follow(leftDrive1);
        rightDrive2.follow(rightDrive1);
        driveSystem.reset();
        leftDrive1.setNeutralMode(NeutralMode.Brake);
        rightDrive1.setNeutralMode(NeutralMode.Brake);
    }

    boolean climbOn = false;
    public void fmsUpdateTeleop() {
        if(Controls.driveConfigPressed) {
            driveSystem.toggleControlsType();
        }

        if(Controls.shiftPressed) {
            driveSystem.toggleShift();
        }

        if(Controls.activateClimbPressed) {
            climber.turnClimbModeOn();
            climbOn = true;
        } else {
            climbOn = false;
        }
        SmartDashboard.putBoolean("Climber/climbOn", climbOn);

        //TODO: Make an `Axis` type and pass that in instead of driver
        if (climbOn) {
            climber.climb(Controls.liftAxisValue);
        } else {  // !isClimbingMode
            driveSystem.drive(Controls.driveValue, Controls.tankRightDriveValue, Controls.turnValue);
        }
        //driveSystem.writeToDashboard();
        //climber.writeToDashboard();
    }

    public void reset() {
        driveSystem.reset();
    }
}
