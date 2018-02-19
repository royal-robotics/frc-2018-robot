package frc.team2522.robot.libs;

/*
    Used to pass a get only reference to a boolean value
 */
public class ObservableBoolean {
    Boolean value;

    public ObservableBoolean(Boolean value) {
        this.value = value;
    }

    boolean get() {
        return value.booleanValue();
    }
}
