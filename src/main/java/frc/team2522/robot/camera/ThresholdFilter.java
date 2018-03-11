package frc.team2522.robot.camera;

import edu.wpi.cscore.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ThresholdFilter extends CvSink{
    CvSink cvInputSink;
    Mat inputFrame = new Mat();
    Mat hsvFrame = new Mat();
    Mat blurFrame = new Mat();

    private ThresholdFilter(String name) {
        super(name);
    }

    public ThresholdFilter(CvSink cvSink) {
        super("Threshold-Filter");
        this.cvInputSink = cvSink;
    }

    @Override
    public long grabFrame(Mat outputFrame) {
        long result = cvInputSink.grabFrame(inputFrame);
        if(result == 0)
            return 0; //timeout error

        double lowH = SmartDashboard.getNumber("Camera/Filter/blob/color/low/h", 10);
        double lowS = SmartDashboard.getNumber("Camera/Filter/blob/color/low/s", 10);
        double lowV = SmartDashboard.getNumber("Camera/Filter/blob/color/low/v", 10);
        double highH = SmartDashboard.getNumber("Camera/Filter/blob/color/high/h", 250);
        double highS = SmartDashboard.getNumber("Camera/Filter/blob/color/high/s", 250);
        double highV = SmartDashboard.getNumber("Camera/Filter/blob/color/high/v", 250);

        //Imgproc.GaussianBlur(inputFrame, blurFrame, new Size(5, 5), .1);

        Imgproc.cvtColor(inputFrame, hsvFrame, Imgproc.COLOR_BGR2HSV);
        Core.inRange(hsvFrame, new Scalar(lowH, lowS, lowV), new Scalar(highH, highS, highV), outputFrame);

        return result;
    }
}
