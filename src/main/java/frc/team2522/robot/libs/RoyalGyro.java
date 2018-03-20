package frc.team2522.robot.libs;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public class RoyalGyro {
    ADXRS450_Gyro gyro;
    boolean reversed;

    public RoyalGyro (ADXRS450_Gyro gyro, boolean reversed){
        this.gyro = gyro;
        this.reversed = reversed;
    }

    public double getAngle () {
        double angle = gyro.getAngle();
        if (this.reversed == true) {
            angle = angle + 180 % 360;
        }
        return angle;
    }

    public void reset (){
        gyro.reset();
    }

    public void setReversed (boolean reversed){
        this.reversed = reversed;
    }
}

