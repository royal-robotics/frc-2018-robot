package frc.team2522.robot.camera;

import edu.wpi.cscore.*;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.team2522.robot.Controls;
import org.opencv.core.Mat;

public class CameraPipeline {

    static {
        SmartDashboard.putString("Camera/Filter", "raw");
    }

    //Setup image pipe
    CvSink cameraStream = createCameraStream();
    BlurFilter blurFilter = new BlurFilter(cameraStream);
    ThresholdFilter thresholdFilter = new ThresholdFilter(blurFilter);

    public CameraPipeline() {

        new Thread(() -> {
            CvSource outputStream = createOutputStream();
            Mat frame = new Mat();

            while (!Thread.interrupted()) {
                CvSink sink = getFilter(SmartDashboard.getString("Camera/Filter", "raw"));
                sink.grabFrame(frame);

                outputStream.putFrame(frame);
            }
        });//.start();
    }

    private CvSink getFilter(String filter) {
        if(filter.equals("blur"))
            return blurFilter;
        else if(filter.equals("threshold"))
            return thresholdFilter;
        else //"raw"
            return cameraStream;
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
