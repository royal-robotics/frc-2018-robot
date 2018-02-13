package frc.team2522.robot;

import frc.team2522.robot.camera.CameraPipeline;

import edu.wpi.first.wpilibj.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team2522.robot.subsystems.*;

public class Robot extends IterativeRobot {
    TalonSRX motorcontroller = new TalonSRX(1);

    Joystick leftStick = new Joystick(0);
    Joystick rightStick = new Joystick(1);

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
    public void teleopPeriodic() { }
}
