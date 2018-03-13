package frc.team2522.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2522.robot.autonomous.AutoManager;
import frc.team2522.robot.autonomous.AutoRoutines;
import frc.team2522.robot.camera.CameraPipeline;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.*;

import frc.team2522.robot.libs.Stopwatch;
import frc.team2522.robot.subsystems.Drivebase.DriveController;
import frc.team2522.robot.subsystems.Elevator.Elevator;
import frc.team2522.robot.subsystems.Elevator.Intake;
import frc.team2522.robot.subsystems.Elevator.Lift;

public class Robot extends IterativeRobot {

    // CAN Motor Controller Usage
    //
    private TalonSRX leftDriveMotor1 = new TalonSRX(0);
    private VictorSPX leftDriveMotor2 = new VictorSPX(1);
    private TalonSRX liftMotor1 = new TalonSRX(2);
    private VictorSPX liftMotor2 = new VictorSPX(3);
    private VictorSPX liftMotor3 = new VictorSPX(4);
    private VictorSPX leftIntakeMotor = new VictorSPX(5);
    private TalonSRX rightDriveMotor1 = new TalonSRX(6);
    private VictorSPX rightDriveMotor2 = new VictorSPX(7);
    private TalonSRX carriageIntakeMotor = new TalonSRX(8);
    private VictorSPX rightIntakeMotor = new VictorSPX(9);

    // Digital I/O Usage
    //
    private DigitalInput elevatorLiftHallEffectSensor = new DigitalInput(0);

    private Encoder leftDriveEncoder = new Encoder(10,11, true);        // ENC0
    private Encoder rightDriveEncoder = new Encoder(12,13, false);      // ENC1
    private Encoder elevatorLiftEncoder = new Encoder(14, 15, false);   // ENC2
//    private Encoder unused = new Encoder(16,17, false);      // ENC3
//    private Encoder unused = new Encoder(18,19, false);      // ENC4
//    private Encoder unused = new Encoder(20,21, false);      // ENC5

    // Pneumatic Solenoid Module Usage
    private DoubleSolenoid intakeHi = new DoubleSolenoid(0, 0, 7);
    private DoubleSolenoid liftBrake = new DoubleSolenoid(0, 1, 6);
    private DoubleSolenoid liftRatchet = new DoubleSolenoid(0, 2, 5);
    private DoubleSolenoid pto = new DoubleSolenoid(1, 1, 6);
    private DoubleSolenoid shifter = new DoubleSolenoid(1, 2, 5);
    private DoubleSolenoid intakeLo = new DoubleSolenoid(1, 3, 4);

    // Gyro Configuration
    private ADXRS450_Gyro gyro = new ADXRS450_Gyro();

    //
    Stopwatch robotStopwatch = Stopwatch.StartNew();

    // Subsystem Definitions
    public DriveController driveController;
    public Elevator elevatorController;

    AutoManager auto;

    @Override
    public void robotInit() {
        CameraPipeline.initialize();
        Controls.initialize();

        // Setup Drivebase subsystem.
        //
        leftDriveMotor2.follow(leftDriveMotor1);
        leftDriveMotor1.setNeutralMode(NeutralMode.Brake);
        leftDriveMotor2.setNeutralMode(NeutralMode.Brake);

        rightDriveMotor1.setNeutralMode(NeutralMode.Brake);
        rightDriveMotor2.setNeutralMode(NeutralMode.Brake);
        rightDriveMotor2.follow(rightDriveMotor1);

        leftDriveEncoder.setReverseDirection(true);
        rightDriveEncoder.setReverseDirection(false);

        this.driveController = new DriveController(leftDriveMotor1, leftDriveEncoder, rightDriveMotor1, rightDriveEncoder, gyro, shifter, pto);

        // Setup Elevator subsystem
        //
        liftMotor2.follow(liftMotor1);
        liftMotor3.follow(liftMotor1);
        Intake intake = new Intake(carriageIntakeMotor, leftIntakeMotor, rightIntakeMotor, intakeHi, intakeLo);
        Lift   lift = new Lift(intake, liftMotor1, elevatorLiftEncoder, elevatorLiftHallEffectSensor, liftBrake, liftRatchet);
        this.elevatorController = new Elevator(intake, lift);
    }

    @Override
    public void robotPeriodic() {
        Controls.updateControls();

        this.driveController.robotPeriodic();
        this.elevatorController.robotPeriodic();
    }

    /**
     * Initialization code for disabled mode.
     *
     * <p>This code will be called every time the robot enters disabled mode.
     */
    @Override
    public void disabledInit() {

    }

    /**
     * Periodic code for disabled mode should go here.
     */
    @Override
    public void disabledPeriodic() {
        this.driveController.disablePeriodic();
        AutoRoutines.writeRoutinesToDashboard();
        SmartDashboard.putString("AutoRoutines/SelectedRoutineAck", AutoRoutines.getAutoMode());
    }

    /**
     * Initialization code for autonomous mode.
     *
     * <p>This code will be called every time the robot enters autonomous mode.
     */
    @Override
    public void autonomousInit() {

        this.driveController.reset();
        auto = AutoRoutines.selectAutoMode(AutoRoutines.getAutoMode(), this);
    }

    /**
     * Periodic code for autonomous mode should go here.
     */
    public void autonomousPeriodic() {
        this.driveController.autonomousPeriodic();
    }

    /**
     * Initialization code for teleop mode.
     *
     * <p>This code will be called every time the robot enters teleop mode.
     */
    @Override
    public void teleopInit() {
        this.driveController.reset();
    }

    /**
     * Periodic code for teleop mode should go here.
     */
    @Override
    public void teleopPeriodic() {
        driveController.teleopPeriodic();
        elevatorController.teleopPeriodic();
    }

}