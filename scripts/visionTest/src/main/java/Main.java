package VisionTest;

import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.opencv.videoio.*;
import org.opencv.imgcodecs.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("hello world");
        System.loadLibrary("opencv_java320"); //This dll file needs to be copied into the path... right now it's at C:\Everything\Royal Robotics\FRC-2018\test-pathfinder\libs\pathfinderjava\shared\any64
        
        Mat frame = new Mat();

        VideoCapture videoCapture = new VideoCapture();
        videoCapture.open(0);
        boolean itWorked = videoCapture.read(frame);
        videoCapture.release();
        
        System.out.println(itWorked);
        System.out.printf("Cols: %d, rows: %d\n", frame.cols(), frame.rows());

        Mat frameFiltered = new Mat();
        Core.inRange(frame, new Scalar(0, 0, 0), new Scalar(20, 20, 50), frameFiltered);

        double[] vals = frame.get(frame.rows() / 2, frame.cols() / 2);
        System.out.printf("r: %f, g: %f, b: %f\n", vals[0], vals[1], vals[2]);
        
        Imgcodecs.imwrite("output.jpg", frame);
        Imgcodecs.imwrite("filtered.jpg", frameFiltered);
    }
}