package frc.team2522.robot;

import frc.team2522.robot.camera.CameraPipeline;

import edu.wpi.first.wpilibj.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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

    Joystick leftStick = new Joystick(0);
    Joystick rightStick = new Joystick(1);

    DoubleSolenoid ratchet = new DoubleSolenoid(0, 2, 5);
    DoubleSolenoid brake = new DoubleSolenoid(0, 1, 6);
    DoubleSolenoid inHi = new DoubleSolenoid(0, 0, 7);

    DoubleSolenoid inLo = new DoubleSolenoid(1, 3, 4);
    DoubleSolenoid shift = new DoubleSolenoid(1, 2, 5);
    DoubleSolenoid pto = new DoubleSolenoid(1, 1, 6);


    Drivebase drivebase = new Drivebase();

//    CvSink cvSink;
//    CvSource outputStream;
//
//    Mat source = new Mat();
//    Mat output = new Mat();

    CameraPipeline camera = new CameraPipeline(leftStick);

    @Override
    public void robotInit() {
//        CameraServer.getInstance().startAutomaticCapture();
//
//        cvSink = CameraServer.getInstance().getVideo();
//        outputStream = new CvSource("blur", VideoMode.PixelFormat.kMJPEG, 640, 480, 30);
//
//        MjpegServer mjpegServer2 = CameraServer.getInstance().addServer("server_blur", 1182);
//        mjpegServer2.setSource(outputStream);

        SmartDashboard.putNumber("Servo/angle", 0);

        SmartDashboard.putString("example/test-string", "hello world");
        SmartDashboard.putBoolean("example/test-boolean", true);
        SmartDashboard.putNumber("example/test-number", 42);
    }

    @Override
    public void disabledInit() { }

    @Override
    public void disabledPeriodic() {
//        cvSink.grabFrame(source);
//        Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
//        outputStream.putFrame(output);
    }

    @Override
    public void autonomousInit() { }

    @Override
    public void autonomousPeriodic() { }

    @Override
    public void teleopInit() { }

    @Override
    public void teleopPeriodic() {
        if (leftStick.getRawButton(1)) {
            ratchet.set(DoubleSolenoid.Value.kForward);
        } else {
            ratchet.set(DoubleSolenoid.Value.kReverse);
        }

        if (leftStick.getRawButton(2)) {
            brake.set(DoubleSolenoid.Value.kForward);
        } else {
            brake.set(DoubleSolenoid.Value.kReverse);
        }

        if (leftStick.getRawButton(3)) {
            inHi.set(DoubleSolenoid.Value.kForward);
        } else {
            inHi.set(DoubleSolenoid.Value.kReverse);
        }

        if (leftStick.getRawButton(4)) {
            inLo.set(DoubleSolenoid.Value.kForward);
        } else {
            inLo.set(DoubleSolenoid.Value.kReverse);
        }

        if (leftStick.getRawButton(5)) {
            shift.set(DoubleSolenoid.Value.kForward);
        } else {
            shift.set(DoubleSolenoid.Value.kReverse);
        }

        if (leftStick.getRawButton(6)) {
            pto.set(DoubleSolenoid.Value.kForward);
        } else {
            pto.set(DoubleSolenoid.Value.kReverse);
        }
    }
}
