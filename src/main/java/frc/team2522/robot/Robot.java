package frc.team2522.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.camera.CameraPipeline;

import edu.wpi.first.wpilibj.*;

import frc.team2522.robot.libs.ObservableBoolean;
import frc.team2522.robot.libs.Stopwatch;
import frc.team2522.robot.subsystems.Drivebase.Drivebase;
import frc.team2522.robot.subsystems.Elevator.Elevator;

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

    //ADXRS450_Gyro gyro = new ADXRS450_Gyro();


    CameraPipeline camera = new CameraPipeline(Controls.driver);

    Boolean isClimbingMode = new Boolean(false);

    Stopwatch robotStopwatch = Stopwatch.StartNew();
    Drivebase drivebase = new Drivebase(Controls.driver, isClimbingMode);
    Elevator elevator = new Elevator(Controls.driver, new ObservableBoolean(isClimbingMode));

    @Override
    public void robotInit() {
        //gyro.reset();
    }

    @Override
    public void robotPeriodic() {
        SmartDashboard.putNumber("robot/uptime/", robotStopwatch.getElapsedTime().getSeconds());
    }

    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() { }

    @Override
    public void autonomousPeriodic() { }

    @Override
    public void autonomousInit() {
        drivebase.reset();
    }
    
    @Override
    public void teleopInit() {
        drivebase.reset();
    }

    @Override
    public void teleopPeriodic() {
        drivebase.fmsUpdateTeleop();
        elevator.fmsUpdateTeleop();
    }
}