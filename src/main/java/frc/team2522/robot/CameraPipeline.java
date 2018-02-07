package frc.team2522.robot;

import edu.wpi.cscore.*;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class CameraPipeline {

    CvSink cvSink;

    public CameraPipeline() {
        UsbCamera usbCamera = new UsbCamera("USB Camera 0", 0);
        MjpegServer mjpegServer1 = new MjpegServer("serve_USB Camera 0", 1181);

        mjpegServer1.setSource(usbCamera);
        cvSink = new CvSink("opencv_USB Camera 0");
        cvSink.setSource(usbCamera);

        Mat source = new Mat();
        cvSink.grabFrame(source);
        //Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);

        System.out.println("Cols: " + source.cols());

        CvSource outputStream = new CvSource("Blur", VideoMode.PixelFormat.kMJPEG, 640, 480, 30);
        outputStream.putFrame(source);
        MjpegServer mjpegServer2 = new MjpegServer("serve_Blur", 1182);
        mjpegServer2.setSource(outputStream);

    }
}
