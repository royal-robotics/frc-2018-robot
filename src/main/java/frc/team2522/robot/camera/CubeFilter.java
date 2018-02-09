package frc.team2522.robot.camera;

import edu.wpi.cscore.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class CubeFilter extends CvSink{
    CvSink cvInputSink;
    Mat inputFrame = new Mat();
    Mat hsvFrame = new Mat();

    private CubeFilter(String name) {
        super(name);
    }

    public CubeFilter(CvSink cvSink) {
        super("Cube-Filter");
        this.cvInputSink = cvSink;
    }

    @Override
    public long grabFrame(Mat outputFrame) {
        long result = cvInputSink.grabFrame(inputFrame);
        if(result == 0)
            return 0; //timeout error


        Imgproc.cvtColor(inputFrame, hsvFrame, Imgproc.COLOR_RGB2HSV);
        Core.inRange(hsvFrame, new Scalar(10, 10, 10), new Scalar(100, 100, 100), outputFrame);

        return result;
    }
}
