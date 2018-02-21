package frc.team2522.robot.subsystems.Drivebase;

import com.ctre.phoenix.drive.DiffDrive;
import com.ctre.phoenix.mechanical.Gearbox;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.*;
import frc.team2522.robot.libs.Button;
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

    Button btnToggleDriveType;
    Button btnToggleShift1;
    Button btnToggleShift2;

    public Drivebase(Joystick driver, Boolean isClimbingMode) {
        this.driver = driver;
        this.isClimbingMode = isClimbingMode;

        btnToggleDriveType = new Button(driver, 7, Button.ButtonType.Toggle);
        btnToggleShift1 = new Button(driver, 9, Button.ButtonType.Toggle);
        btnToggleShift2 = new Button(driver, 10, Button.ButtonType.Toggle);

        leftDrive2.follow(leftDrive1);
        rightDrive2.follow(rightDrive1);
        driveSystem.reset();
    }

    public void fmsUpdateTeleop() {
        if(btnToggleDriveType.isPressed()) {
            driveSystem.toggleControlsType();
        }

        if(btnToggleShift1.isPressed() || btnToggleShift2.isPressed()) {
            driveSystem.toggleShift();
        }

        //TODO: Make a double button to trigger this
        //climber.updateClimbing(driver.getRawButton(5), driver.getRawButton(6));

//        if (isClimbingMode) {
//            climber.climb(left, right, DEADZONE);
//        } else {  // !isClimbingMode
            //TODO: Make a `Axis` type and pass that in
            driveSystem.drive(driver);
//        }
    }

    public void reset() {
        driveSystem.reset();
    }
}
