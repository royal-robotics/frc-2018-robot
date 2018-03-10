package frc.team2522.robot.camera;

import edu.wpi.cscore.*;
import frc.team2522.robot.Controls;
import org.opencv.core.Mat;

public class CameraPipeline {

    public CameraPipeline() {

        new Thread(() -> {
            CvSink cameraStream = createCameraStream();

            CubeFilter cubeFilter = new CubeFilter(cameraStream);

            CvSource outputStream = createOutputStream();
            Mat frame = new Mat();

            while (!Thread.interrupted()) {
                if (Controls.debugDriveForward())
                    cubeFilter.grabFrame(frame);
                else
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
