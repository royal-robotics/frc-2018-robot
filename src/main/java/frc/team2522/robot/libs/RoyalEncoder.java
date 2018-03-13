package frc.team2522.robot.libs;

import edu.wpi.first.wpilibj.Encoder;

public class RoyalEncoder extends Encoder {

    double encoderOffeset = 0.0;

    public RoyalEncoder(int channelA, final int channelB) {
        super(channelA, channelB);
    }

    public RoyalEncoder(final int channelA, final int channelB, boolean reverseDirection){
        super(channelA, channelB, reverseDirection);
    }

    @Override
    public double getDistance() {
        return super.getDistance() + this.encoderOffeset;
    }

    @Override
    public void reset() {
        super.reset();
        this.encoderOffeset = 0.0;
    }

    public void reset(double offset) {
        super.reset();
        this.encoderOffeset = offset;
    }
}
