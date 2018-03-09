package frc.team2522.robot.camera;

import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Filter;

import edu.wpi.cscore.*;
import edu.wpi.first.wpilibj.Joystick;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class CameraPipeline {

    Joystick joystick;

    public CameraPipeline(Joystick joystick) {
        this.joystick = joystick;

        new Thread(() -> {
            CvSink cameraStream = createCameraStream();

            CubeFilter cubeFilter = new CubeFilter(cameraStream);

            CvSource outputStream = createOutputStream();
            Mat frame = new Mat();

            while (!Thread.interrupted()) {
                //if (joystick.getRawButton(1))
                //    cubeFilter.grabFrame(frame);
                //else
                    cameraStream.grabFrame(frame);

                outputStream.putFrame(frame);
            }
        }).start();
    }

    private CvSink createCameraStream() {
        //TODO: The camera supports YUYV images, experiment with setVideoMode
        //and passing raw image to the video pipeline.

        UsbCamera  usbCamera = new UsbCamera("USB Camera 0", 0);
        CvSink cvSink = new CvSink("opencv_USB Camera 0");
        cvSink.setSource(usbCamera);
        return cvSink;
    }

    private CvSource createOutputStream() {
        MjpegServer mjpegServer1 = new MjpegServer("serve_USB Camera 0", 1181);
        CvSource outputStream = new CvSource("Blur", VideoMode.PixelFormat.kMJPEG, 640, 480, 5);
        mjpegServer1.setSource(outputStream);
        return outputStream;
    }
}
