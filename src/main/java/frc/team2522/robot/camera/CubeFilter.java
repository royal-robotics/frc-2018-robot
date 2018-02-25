package frc.team2522.robot.camera;

import edu.wpi.cscore.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

        SmartDashboard.putNumber("Camera/CubeFilter/lowH", 10);
        SmartDashboard.putNumber("Camera/CubeFilter/lowS", 10);
        SmartDashboard.putNumber("Camera/CubeFilter/lowV", 10);
        SmartDashboard.putNumber("Camera/CubeFilter/highH", 100);
        SmartDashboard.putNumber("Camera/CubeFilter/highS", 100);
        SmartDashboard.putNumber("Camera/CubeFilter/highV", 100);

    }

    @Override
    public long grabFrame(Mat outputFrame) {
        long result = cvInputSink.grabFrame(inputFrame);
        if(result == 0)
            return 0; //timeout error


        double lowH = SmartDashboard.getNumber("Camera/CubeFilter/lowH", 10);
        double lowS = SmartDashboard.getNumber("Camera/CubeFilter/lowS", 10);
        double lowV = SmartDashboard.getNumber("Camera/CubeFilter/lowV", 10);
        double highH = SmartDashboard.getNumber("Camera/CubeFilter/highH", 100);
        double highS = SmartDashboard.getNumber("Camera/CubeFilter/highS", 100);
        double highV = SmartDashboard.getNumber("Camera/CubeFilter/highV", 100);


        Imgproc.cvtColor(inputFrame, hsvFrame, Imgproc.COLOR_RGB2HSV);
        Core.inRange(hsvFrame, new Scalar(lowH, lowS, lowV), new Scalar(highH, highS, highV), outputFrame);

        return result;
    }
}
