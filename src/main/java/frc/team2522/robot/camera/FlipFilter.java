package frc.team2522.robot.camera;

import edu.wpi.cscore.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class FlipFilter extends CvSink{
    CvSink cvInputSink;
    Mat inputFrame = new Mat();

    private FlipFilter(String name) {
        super(name);
    }

    public FlipFilter(CvSink cvSink) {
        super("Threshold-Filter");
        this.cvInputSink = cvSink;
    }

    @Override
    public long grabFrame(Mat outputFrame) {
        long result = cvInputSink.grabFrame(inputFrame);
        if(result == 0)
            return 0; //timeout error

        Core.rotate(inputFrame, outputFrame, Core.ROTATE_180);

        return result;
    }
}
