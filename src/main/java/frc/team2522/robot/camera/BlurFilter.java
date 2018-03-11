package frc.team2522.robot.camera;

import edu.wpi.cscore.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class BlurFilter extends CvSink{
    CvSink cvInputSink;
    Mat inputFrame = new Mat();

    public BlurFilter(CvSink cvSink) {
        super("Blur-Filter");
        this.cvInputSink = cvSink;

        SmartDashboard.putNumber("Camera/Filter/blob/blur/kernelSize", 5);
        SmartDashboard.putNumber("Camera/Filter/blob/blur/sigma", 0.1);
    }

    @Override
    public long grabFrame(Mat outputFrame) {
        long result = cvInputSink.grabFrame(inputFrame);
        if(result == 0)
            return 0; //timeout error

        int kernelSize = (int)Math.floor(SmartDashboard.getNumber("Camera/Filter/blob/blur/kernelSize", 5));
        if(kernelSize % 2 == 0)
            kernelSize++; // Kernel must be an odd number

        double sigma = SmartDashboard.getNumber("Camera/Filter/blob/blur/sigma", 0.1);

        Imgproc.GaussianBlur(inputFrame, outputFrame, new Size(kernelSize, kernelSize), sigma);

        return result;
    }
}
